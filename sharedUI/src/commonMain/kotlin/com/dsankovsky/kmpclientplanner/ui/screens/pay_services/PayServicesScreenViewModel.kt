package com.dsankovsky.kmpclientplanner.ui.screens.pay_services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
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

    fun handleActions(action: PayServiceScreenAction) {
        when (action) {
            PayServiceScreenAction.LoadData -> {
                loadData()
            }

            is PayServiceScreenAction.OnChangeClientCLicked -> {
                updateAvailableServices(action.client)
            }

            is PayServiceScreenAction.OnServicesAmountChanged -> {
                val amount = action.amount.toIntOrNull() ?: 0

                _state.update {
                    it.copy(
                        isPaymentReady = it.client != null && amount > 0 && amount <= it.availableServices,
                        servicesAmount = action.amount
                    )
                }
            }

            PayServiceScreenAction.OnPayClicked -> {
                payServices()
            }

            PayServiceScreenAction.OnBackClicked -> {
                viewModelScope.launch {
                    event.emit(PayServiceScreenEvent.OnDismissClicked)
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val clients = getClientsUseCase.getAllClients().firstOrNull() ?: emptyList()
            _state.update {
                it.copy(
                    clientsList = clients
                )
            }
        }
    }

    private fun updateAvailableServices(client: BaseClient) {
        viewModelScope.launch {
            val availableServices =
                getServicesUseCase.getAllUnpaidServices(client.id)
                    .firstOrNull() ?: emptyList()
            _state.update {
                val amount = it.servicesAmount.toIntOrNull() ?: 0
                it.copy(
                    client = client,
                    availableServices = availableServices.size,
                    isPaymentReady = amount > 0 && amount <= availableServices.size
                )
            }
        }
    }

    private fun payServices() {
        viewModelScope.launch {
            val state = state.value
            val client = state.client ?: throw RuntimeException("Client must be not null")
            val servicesAmount =
                if (state.servicesAmount.isEmpty()) 0 else state.servicesAmount.toInt()

            val availableServices = getServicesUseCase.getAllUnpaidServices(client.id).firstOrNull()
                ?.take(servicesAmount) ?: emptyList()

            availableServices.forEach {
                val service = it.copy(isPaid = true)
                addEditDeleteServiceUseCase.update(service)
            }
            updateAvailableServices(state.client)
            event.emit(PayServiceScreenEvent.OnSuccess)
        }
    }
}