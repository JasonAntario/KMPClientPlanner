package com.dsankovsky.kmpclientplanner.ui.screens.services_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.ui.screens.services_history.ServicesHistoryScreenEvent.OpenServiceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServicesHistoryScreenViewModel(
    private val getServicesUseCase: GetServicesUseCase,
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
            getServicesUseCase.getServicesForClientFlow(clientId)
                .map { services ->
                    services
                        .map { service ->
                            ServicesHistoryScreenItem.ServiceItem(
                                id = service.id,
                                title = service.title,
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
//                                val dayOfWeek =
//                                    LocalizedDayOfWeek.entries.first { it.isoIndex == item.key.dayOfWeek.isoDayNumber }
//                                val month =
//                                    LocalizedMonths.entries.first { it.monthNumber == item.key.month.number }
                                add(
                                    ServicesHistoryScreenItem.DateDivider(
                                        item.key.day,
                                        1,
                                        1
                                    )
                                )
                                addAll(item.value)
                            }
                        }
                        .flatten()
                }
                .collectLatest { items ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            items = items
                        )
                    }
                }
        }
    }

    private fun deleteService(service: ServicesHistoryScreenItem.ServiceItem) {
        viewModelScope.launch {
            addEditDeleteServiceUseCase.deleteService(service.id)
        }
    }

    private fun updateFinishServiceStatus(item: ServicesHistoryScreenItem.ServiceItem) {
        viewModelScope.launch {
            val service = item.service
            val newService = service.copy(isFinished = !service.isFinished)
            addEditDeleteServiceUseCase.update(newService)
            event.emit(ServicesHistoryScreenEvent.StatusUpdated)
        }
    }

    private fun updatePayServiceStatus(item: ServicesHistoryScreenItem.ServiceItem) {
        viewModelScope.launch {
            val service = item.service
            val newService = service.copy(isPaid = !service.isPaid)
            addEditDeleteServiceUseCase.update(newService)
            event.emit(ServicesHistoryScreenEvent.StatusUpdated)
        }
    }
}