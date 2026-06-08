package com.dsankovsky.kmpclientplanner.ui.screens.services_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenItem
import com.dsankovsky.kmpclientplanner.ui.screens.services_history.ServicesHistoryScreenEvent.OpenServiceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServicesHistoryScreenViewModel(
    private val getServicesUseCase: GetServicesUseCase,
    private val getClientsUseCase: GetClientsUseCase,
    private val addEditDeleteServiceUseCase: AddEditDeleteServiceUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ServicesHistoryScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<ServicesHistoryScreenEvent>()

    fun handleAction(action: ServicesHistoryScreenAction) {
        when (action) {
            is ServicesHistoryScreenAction.LoadData -> loadData(action.clientId)
            is ServicesHistoryScreenAction.OnDeleteService -> deleteService(action.serviceItem)

            is ServicesHistoryScreenAction.OnServiceClicked -> {
                viewModelScope.launch {
                    event.emit(OpenServiceInfo(action.serviceItem.id))
                }
            }

            ServicesHistoryScreenAction.OnCloseScreenClicked -> {
                viewModelScope.launch {
                    event.emit(ServicesHistoryScreenEvent.CloseScreen)
                }
            }

            is ServicesHistoryScreenAction.OnFinishStatusChanged -> {
                updateFinishServiceStatus(action.serviceItem)
            }

            is ServicesHistoryScreenAction.OnPaidStatusChanged -> {
                updatePayServiceStatus(action.serviceItem)
            }
        }
    }

    private fun loadData(clientId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                getServicesUseCase.getServicesForClientFlow(clientId),
                getClientsUseCase.getClientById(clientId)
            ) { services, client ->
                if (client == null) return@combine emptyList()
                services
                    .map { service ->
                        ServicesListScreenItem.ServiceItem(
                            id = service.id,
                            title = service.title,
                            client = client,
                            timeInterval = service.getServiceTime(),
                            startDate = service.startDate,
                            endDate = service.endDate,
                            isPaid = service.isPaid,
                            isFinished = service.isFinished,
                            serviceType = service.serviceType,
                            service = service
                        )
                    }
                    .groupBy { it.startDate.date }
                    .toSortedMap()
                    .map { (date, items) ->
                        buildList {
                            add(ServicesListScreenItem.DateDivider(date))
                            addAll(items)
                        }
                    }
                    .flatten()
            }
                .collectLatest { items ->
                    _state.update {
                        it.copy(isLoading = false, items = items)
                    }
                }
        }
    }

    private fun deleteService(service: ServicesListScreenItem.ServiceItem) {
        viewModelScope.launch {
            addEditDeleteServiceUseCase.deleteService(service.id)
        }
    }

    private fun updateFinishServiceStatus(item: ServicesListScreenItem.ServiceItem) {
        viewModelScope.launch {
            val newService = item.service.copy(isFinished = !item.service.isFinished)
            addEditDeleteServiceUseCase.update(newService)
            event.emit(ServicesHistoryScreenEvent.StatusUpdated)
        }
    }

    private fun updatePayServiceStatus(item: ServicesListScreenItem.ServiceItem) {
        viewModelScope.launch {
            val newService = item.service.copy(isPaid = !item.service.isPaid)
            addEditDeleteServiceUseCase.update(newService)
            event.emit(ServicesHistoryScreenEvent.StatusUpdated)
        }
    }
}
