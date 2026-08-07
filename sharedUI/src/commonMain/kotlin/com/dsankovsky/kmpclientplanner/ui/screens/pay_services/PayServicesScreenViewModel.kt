package com.dsankovsky.kmpclientplanner.ui.screens.pay_services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PayServicesScreenViewModel(
    private val getClientsUseCase: GetClientsUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val addEditDeleteServiceUseCase: AddEditDeleteServiceUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PayServiceScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<PayServiceScreenEvent>()

    private var observeJob: Job? = null

    fun handleActions(action: PayServiceScreenAction) {
        when (action) {
            PayServiceScreenAction.LoadData -> observeData()

            is PayServiceScreenAction.OnClientSelected -> _state.update { state ->
                // Другой клиент — другой долг, поэтому счётчик начинается заново.
                state
                    .copy(
                        selectedClientId = action.clientId,
                        amount = PayServiceScreenState.DefaultAmount,
                    )
                    .withUnpaidServices()
            }

            is PayServiceScreenAction.OnAmountChanged -> _state.update {
                it.copy(amount = action.amount.coerceIn(1, maxOf(1, it.maxAmount)))
            }

            PayServiceScreenAction.OnPayClicked -> payServices()

            PayServiceScreenAction.OnCloseClicked -> viewModelScope.launch {
                event.emit(PayServiceScreenEvent.OnDismissClicked)
            }
        }
    }

    /**
     * Занятия и клиенты читаются потоками: оплата меняет те же строки, что показывает
     * плашка, и список должен пересчитаться сам — без перезахода в модалку.
     */
    private fun observeData() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                getServicesUseCase.getAllServices(),
                getClientsUseCase.getAllClients(),
            ) { services, clients -> services to clients }
                .collectLatest { (services, clients) ->
                    _state.update { it.withData(services, clients) }
                }
        }
    }

    private fun payServices() {
        viewModelScope.launch {
            val services = state.value.servicesToPay
            if (services.isEmpty()) return@launch

            services.forEach { addEditDeleteServiceUseCase.update(it.copy(isPaid = true)) }
            event.emit(PayServiceScreenEvent.OnSuccess)
        }
    }

    /** Все занятия держим в VM: состоянию нужен только долг выбранного клиента. */
    private var allServices: List<BaseService> = emptyList()

    private fun PayServiceScreenState.withData(
        services: List<BaseService>,
        clients: List<BaseClient>,
    ): PayServiceScreenState {
        allServices = services
        val unpaidByClient = services.filter { !it.isPaid }.groupBy { it.clientId }
        val prepayClients = clients
            .sortedBy { it.getFullName().lowercase() }
            .map { PrepayClient(it, unpaidByClient[it.id].orEmpty().size) }

        return copy(
            isLoading = false,
            clients = prepayClients,
            // Клиента могли удалить, пока модалка открыта.
            selectedClientId = selectedClientId?.takeIf { id -> clients.any { it.id == id } },
        ).withUnpaidServices()
    }

    private fun PayServiceScreenState.withUnpaidServices(): PayServiceScreenState {
        val unpaid = allServices
            .filter { it.clientId == selectedClientId && !it.isPaid }
            // Оплачиваются самые ранние занятия — порядок здесь и есть это правило.
            .sortedBy { it.startDate }
        return copy(
            unpaidServices = unpaid,
            amount = amount.coerceIn(1, maxOf(1, unpaid.size)),
        )
    }
}
