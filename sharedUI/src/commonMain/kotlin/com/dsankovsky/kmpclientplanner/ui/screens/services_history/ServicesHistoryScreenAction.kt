package com.dsankovsky.kmpclientplanner.ui.screens.services_history

import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenItem

sealed interface ServicesHistoryScreenAction {

    data class LoadData(val clientId: Long) : ServicesHistoryScreenAction
    data class OnDeleteService(val serviceItem: ServicesListScreenItem.ServiceItem) :
        ServicesHistoryScreenAction

    data class OnServiceClicked(val serviceItem: ServicesListScreenItem.ServiceItem) :
        ServicesHistoryScreenAction

    data object OnCloseScreenClicked : ServicesHistoryScreenAction
    data class OnPaidStatusChanged(val serviceItem: ServicesListScreenItem.ServiceItem) :
        ServicesHistoryScreenAction

    data class OnFinishStatusChanged(val serviceItem: ServicesListScreenItem.ServiceItem) :
        ServicesHistoryScreenAction
}

sealed interface ServicesHistoryScreenEvent {
    data object CloseScreen : ServicesHistoryScreenEvent
    data class OpenServiceInfo(val serviceId: Long) : ServicesHistoryScreenEvent
    data object StatusUpdated : ServicesHistoryScreenEvent
    data object ServiceDeleted : ServicesHistoryScreenEvent
}
