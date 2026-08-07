package com.dsankovsky.kmpclientplanner.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.additional.getDateInterval
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.model.StatisticsClientItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class StatisticsScreenViewModel(
    private val getClientsUseCase: GetClientsUseCase,
    private val getServicesUseCase: GetServicesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsScreenState())
    val state = _state.asStateFlow()

    /** Период — часть входных данных пересчёта, поэтому он тоже поток. */
    private val period = MutableStateFlow(Period(state.value.currentFilter))

    private var observeJob: Job? = null

    fun handleAction(action: StatisticsScreenAction) {
        when (action) {
            StatisticsScreenAction.LoadData -> observeData()

            is StatisticsScreenAction.OnFilterClicked -> {
                if (action.filter == ServicesFilter.CUSTOM_INTERVAL) {
                    _state.update { it.copy(showDatePicker = true) }
                } else {
                    _state.update { it.copy(showDatePicker = false) }
                    period.value = Period(action.filter)
                }
            }

            StatisticsScreenAction.CloseDatePickerClicked -> {
                _state.update { it.copy(showDatePicker = false) }
            }

            is StatisticsScreenAction.SetCustomInterval -> {
                _state.update { it.copy(showDatePicker = false) }
                period.value = Period(
                    filter = ServicesFilter.CUSTOM_INTERVAL,
                    customStart = action.startDate,
                    customEnd = action.endDate,
                )
            }
        }
    }

    /**
     * Занятия, клиенты и выбранный период — в одном потоке.
     *
     * Раньше данные брались снимком (`firstOrNull`), и экран не замечал изменений:
     * после предоплаты в модалке (М5) суммы под ней оставались прежними, потому что
     * пересчёт запускался только при заходе на экран и смене периода.
     */
    private fun observeData() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            combine(
                getServicesUseCase.getAllServices(),
                getClientsUseCase.getAllClients(),
                period,
            ) { services, clients, period ->
                Triple(services, clients, period)
            }.collectLatest { (services, clients, period) ->
                _state.update { it.withData(services, clients, period) }
            }
        }
    }
}

/** Выбранный период: фильтр и, для «своего интервала», его границы. */
private data class Period(
    val filter: ServicesFilter,
    val customStart: LocalDate? = null,
    val customEnd: LocalDate? = null,
) {

    /** Интервал для подписи и для отбора занятий; `null` — «всё время». */
    fun interval(): Pair<LocalDate, LocalDate>? = when {
        filter != ServicesFilter.CUSTOM_INTERVAL -> filter.getDateInterval()
        customStart != null && customEnd != null -> customStart to customEnd
        else -> null
    }
}

private fun StatisticsScreenState.withData(
    allServices: List<BaseService>,
    clients: List<BaseClient>,
    period: Period,
): StatisticsScreenState {
    val interval = period.interval()
    val services = if (interval == null) {
        allServices
    } else {
        allServices.filter { it.startDate.date in interval.first..interval.second }
    }

    val clientItemList = clients.mapNotNull { client ->
        val servicesForClient = services
            .filter { it.clientId == client.id }
            .groupBy { it.currency }

        if (servicesForClient.isEmpty()) return@mapNotNull null

        StatisticsClientItem(
            client = client,
            income = servicesForClient.map { (currency, clientServices) ->
                StatisticsClientItem.StatisticsPaymentItem(
                    money = clientServices.filter { it.isPaid }.sumPrice(),
                    currency = currency,
                )
            },
            mustBePaid = servicesForClient.map { (currency, clientServices) ->
                StatisticsClientItem.StatisticsPaymentItem(
                    money = clientServices.filter { !it.isPaid && it.isFinished }.sumPrice(),
                    currency = currency,
                )
            },
            paidServicesCount = servicesForClient.values.flatten().count { it.isPaid },
        )
    }

    // Счётчики для карточек: процент оплаты и «сколько ещё ждём».
    val paidCount = services.count { it.isPaid }
    val unpaidFinishedServices = services.filter { it.isFinished && !it.isPaid }

    return copy(
        isLoading = false,
        receivedTotalByCurrency = services.filter { it.isPaid }.totalsByCurrency(),
        expectedTotalByCurrency = unpaidFinishedServices.totalsByCurrency(),
        receivedTotal = clientItemList.flatMap { it.income }.sumOf { it.money.toDouble() }.toFloat(),
        expectedTotal = clientItemList.flatMap { it.mustBePaid }.sumOf { it.money.toDouble() }
            .toFloat(),
        itemsByClients = clientItemList
            .sortedByDescending { client -> client.income.sumOf { it.money.toDouble() } },
        servicesTotal = services.size,
        servicesPaid = paidCount,
        servicesUnpaid = unpaidFinishedServices.size,
        clientsWithDebt = unpaidFinishedServices.map { it.clientId }.distinct().size,
        paidPercentage = if (services.isEmpty()) 0f else paidCount.toFloat() / services.size,
        dateInterval = interval,
        currentFilter = period.filter,
        customIntervalStart = period.customStart,
        customIntervalEnd = period.customEnd,
    )
}

private fun List<BaseService>.sumPrice(): Float =
    sumOf { it.price?.toDouble() ?: 0.0 }.toFloat()

private fun List<BaseService>.totalsByCurrency(): List<StatisticsClientItem.StatisticsPaymentItem> =
    groupBy { it.currency }.map { (currency, services) ->
        StatisticsClientItem.StatisticsPaymentItem(money = services.sumPrice(), currency = currency)
    }
