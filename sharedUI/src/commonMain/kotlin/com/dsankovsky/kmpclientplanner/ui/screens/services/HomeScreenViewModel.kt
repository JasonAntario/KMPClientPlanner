package com.dsankovsky.kmpclientplanner.ui.screens.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.additional.getDateInterval
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.screens.services.HomeScreenEvent.OpenServiceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val getServicesUseCase: GetServicesUseCase,
    private val getClientsUseCase: GetClientsUseCase,
    private val addEditDeleteServiceUseCase: AddEditDeleteServiceUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<HomeScreenEvent>()

    fun handleAction(action: HomeScreenAction) {

        when (action) {
            HomeScreenAction.LoadData -> loadData()
            is HomeScreenAction.OnDeleteService -> deleteService(action.serviceItem)

            is HomeScreenAction.OnServiceClicked -> {
                viewModelScope.launch {
                    event.emit(OpenServiceInfo(action.serviceItem.id))
                }
            }

            is HomeScreenAction.OnFilterClicked -> {
                updateDataByFilter(action.filter)
            }

            is HomeScreenAction.OnFinishStatusChanged -> {
                updateFinishServiceStatus(action.serviceItem)
            }

            is HomeScreenAction.OnPaidStatusChanged -> {
                updatePayServiceStatus(action.serviceItem)
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
                        when (filter) {
                            ServicesFilter.CURRENT_MONTH -> {
                                service.startDate.month == dateInterval.first.month
                            }

                            else -> service.startDate.date in dateInterval.first..dateInterval.second
                        }
                    }
                    .mapNotNull { service ->
                        val client = clients.firstOrNull { service.clientId == it.id }
                            ?: return@mapNotNull null
                        HomeScreenItem.ServiceItem(
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
//                            val dayOfWeek =
//                                LocalizedDayOfWeek.entries.first { it.isoIndex == item.key.dayOfWeek.isoDayNumber }
//                            val month =
//                                LocalizedMonths.entries.first { it.monthNumber == item.key.month.number }
                            add(
                                HomeScreenItem.DateDivider(
                                    item.key.day,
                                    1,
                                    1
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
                        it is HomeScreenItem.ServiceItem
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

    private fun deleteService(service: HomeScreenItem.ServiceItem) {
        viewModelScope.launch {
            addEditDeleteServiceUseCase.deleteService(service.id)
            event.emit(HomeScreenEvent.ServiceDeleted)
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

    private fun updateFinishServiceStatus(item: HomeScreenItem.ServiceItem) {
        viewModelScope.launch {
            val service = item.service
            val newService = service.copy(isFinished = !service.isFinished)
            addEditDeleteServiceUseCase.update(newService)
            event.emit(HomeScreenEvent.StatusUpdated)
        }
    }

    private fun updatePayServiceStatus(item: HomeScreenItem.ServiceItem) {
        viewModelScope.launch {
            val service = item.service
            val newService = service.copy(isPaid = !service.isPaid)
            addEditDeleteServiceUseCase.update(newService)
            event.emit(HomeScreenEvent.StatusUpdated)
        }
    }
}