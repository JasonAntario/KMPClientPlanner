package com.dsankovsky.kmpclientplanner.ui.screens.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.additional.getDateInterval
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenEvent.OpenServiceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServicesScreenViewModel(
    private val getServicesUseCase: GetServicesUseCase,
    private val getClientsUseCase: GetClientsUseCase,
    private val addEditDeleteServiceUseCase: AddEditDeleteServiceUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ServicesListScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<ServicesListScreenEvent>()

    fun handleAction(action: ServicesListScreenAction) {

        when (action) {
            ServicesListScreenAction.LoadData -> loadData()
            is ServicesListScreenAction.OnDeleteService -> deleteService(action.serviceItem)

            is ServicesListScreenAction.OnServiceClicked -> {
                viewModelScope.launch {
                    event.emit(OpenServiceInfo(action.serviceItem.id))
                }
            }

            is ServicesListScreenAction.OnFilterClicked -> {
                updateDataByFilter(action.filter)
            }

            is ServicesListScreenAction.OnFinishStatusChanged -> {
                updateFinishServiceStatus(action.serviceItem)
            }

            is ServicesListScreenAction.OnPaidStatusChanged -> {
                updatePayServiceStatus(action.serviceItem)
            }

            ServicesListScreenAction.OnAddServiceClicked -> {
                viewModelScope.launch {
                    event.emit(ServicesListScreenEvent.AddService)
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                getServicesUseCase.getAllServices(),
                getClientsUseCase.getAllClients()
            ) { services, clients ->
                services
                    .asSequence()
                    .filter { service ->
                        val filter = state.value.currentFilter
                        val dateInterval = filter.getDateInterval() ?: return@filter true
                        service.startDate.date in dateInterval.first..dateInterval.second
                    }
                    .mapNotNull { service ->
                        val client = clients.firstOrNull { service.clientId == it.id }
                            ?: return@mapNotNull null
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
                    .map { item ->
                        buildList {
                            add(
                                ServicesListScreenItem.DateDivider(
                                    item.key
                                )
                            )
                            addAll(item.value)
                        }
                    }
                    .flatten()
                    .toList()
            }
                .collectLatest { serviceItems ->
                    val currentDateTime = getCurrentDateTime()
                    val scrollIndex = serviceItems.indexOfFirst {
                        it is ServicesListScreenItem.ServiceItem
                                && (it.startDate < currentDateTime && it.endDate > currentDateTime
                                || it.startDate > currentDateTime)
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            items = serviceItems,
                            scrollToIndex = if (scrollIndex < 0) 0 else scrollIndex
                        )
                    }
                }
        }
    }

    private fun deleteService(service: ServicesListScreenItem.ServiceItem) {
        viewModelScope.launch {
            addEditDeleteServiceUseCase.deleteService(service.id)
            event.emit(ServicesListScreenEvent.ServiceDeleted)
        }
    }

    private fun updateDataByFilter(
        filter: ServicesFilter,
    ) {
        _state.update {
            it.copy(currentFilter = filter)
        }
        loadData()
    }

    private fun updateFinishServiceStatus(item: ServicesListScreenItem.ServiceItem) {
        viewModelScope.launch {
            val service = item.service
            val newService = service.copy(isFinished = !service.isFinished)
            addEditDeleteServiceUseCase.update(newService)
            event.emit(ServicesListScreenEvent.StatusUpdated)
        }
    }

    private fun updatePayServiceStatus(item: ServicesListScreenItem.ServiceItem) {
        viewModelScope.launch {
            val service = item.service
            val newService = service.copy(isPaid = !service.isPaid)
            addEditDeleteServiceUseCase.update(newService)
            event.emit(ServicesListScreenEvent.StatusUpdated)
        }
    }
}