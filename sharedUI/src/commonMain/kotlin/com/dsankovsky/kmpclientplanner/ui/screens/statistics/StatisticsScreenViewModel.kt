package com.dsankovsky.kmpclientplanner.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.additional.getDateInterval
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.model.StatisticsClientItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class StatisticsScreenViewModel(
    private val getClientsUseCase: GetClientsUseCase,
    private val getServicesUseCase: GetServicesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsScreenState())
    val state = _state.asStateFlow()

    fun handleAction(action: StatisticsScreenAction) {
        when (action) {
            StatisticsScreenAction.LoadData -> loadData()

            is StatisticsScreenAction.OnFilterClicked -> {
                if (action.filter == ServicesFilter.CUSTOM_INTERVAL) {
                    _state.update { it.copy(showDatePicker = true) }
                } else {
                    updateDataByFilter(action.filter)
                }
            }

            StatisticsScreenAction.CloseDatePickerClicked -> {
                _state.update { it.copy(showDatePicker = false) }
            }

            is StatisticsScreenAction.SetCustomInterval -> {
                _state.update {
                    it.copy(
                        showDatePicker = false,
                        currentFilter = ServicesFilter.CUSTOM_INTERVAL,
                        customIntervalStart = action.startDate,
                        customIntervalEnd = action.endDate
                    )
                }
                loadData(
                    filter = ServicesFilter.CUSTOM_INTERVAL,
                    customStart = action.startDate,
                    customEnd = action.endDate
                )
            }
        }
    }

    private fun loadData(
        filter: ServicesFilter = state.value.currentFilter,
        customStart: LocalDate? = state.value.customIntervalStart,
        customEnd: LocalDate? = state.value.customIntervalEnd
    ) {
        viewModelScope.launch {
            val allServices = getServicesUseCase.getAllServices().firstOrNull() ?: emptyList()

            val services = if (filter == ServicesFilter.CUSTOM_INTERVAL) {
                if (customStart != null && customEnd != null) {
                    allServices.filter { it.startDate.date in customStart..customEnd }
                } else {
                    allServices
                }
            } else {
                val dateInterval = filter.getDateInterval()
                if (dateInterval != null) {
                    allServices.filter { it.startDate.date in dateInterval.first..dateInterval.second }
                } else {
                    allServices
                }
            }

            val clients = getClientsUseCase.getAllClients().firstOrNull() ?: emptyList()

            val clientItemList = clients.mapNotNull { client ->
                val servicesForClient = services
                    .filter { it.clientId == client.id }
                    .groupBy { it.currency }

                if (servicesForClient.isEmpty())
                    return@mapNotNull null

                val income = servicesForClient
                    .map {
                        val paidServices = it.value.filter { it.isPaid }
                        StatisticsClientItem.StatisticsPaymentItem(
                            money = paidServices.sumOf { it.price?.toDouble() ?: 0.0 }.toFloat(),
                            currency = it.key
                        )
                    }

                val mustBePaid = servicesForClient
                    .map {
                        val unpaidFinished = it.value.filter { !it.isPaid && it.isFinished }
                        StatisticsClientItem.StatisticsPaymentItem(
                            money = unpaidFinished.sumOf { it.price?.toDouble() ?: 0.0 }.toFloat(),
                            currency = it.key
                        )
                    }

                StatisticsClientItem(
                    client = client,
                    income = income,
                    mustBePaid = mustBePaid
                )
            }

            val revivedTotal = clientItemList
                .flatMap { it.income }
                .sumOf { it.money.toDouble() }
            val expectedTotal = clientItemList
                .flatMap { it.mustBePaid }
                .sumOf { it.money.toDouble() }

            val receivedTotalByCurrency = services
                .filter { it.isPaid }
                .groupBy { it.currency }
                .map {
                    StatisticsClientItem.StatisticsPaymentItem(
                        money = it.value.sumOf { it.price?.toDouble() ?: 0.0 }.toFloat(),
                        currency = it.key
                    )
                }

            val expectedTotalByCurrency = services
                .filter { it.isFinished && !it.isPaid }
                .groupBy { it.currency }
                .map {
                    StatisticsClientItem.StatisticsPaymentItem(
                        money = it.value.sumOf { it.price?.toDouble() ?: 0.0 }.toFloat(),
                        currency = it.key
                    )
                }

            val displayInterval = when {
                filter == ServicesFilter.CUSTOM_INTERVAL && customStart != null && customEnd != null ->
                    Pair(customStart, customEnd)
                filter != ServicesFilter.CUSTOM_INTERVAL -> filter.getDateInterval()
                else -> null
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    receivedTotalByCurrency = receivedTotalByCurrency,
                    expectedTotalByCurrency = expectedTotalByCurrency,
                    receivedTotal = revivedTotal.toFloat(),
                    expectedTotal = expectedTotal.toFloat(),
                    itemsByClients = clientItemList,
                    dateInterval = displayInterval,
                    currentFilter = filter
                )
            }
        }
    }

    private fun updateDataByFilter(filter: ServicesFilter) {
        _state.update {
            it.copy(currentFilter = filter, showDatePicker = false)
        }
        loadData(filter)
    }
}
