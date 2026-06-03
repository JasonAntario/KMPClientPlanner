package com.dsankovsky.kmpclientplanner.ui.screens.services_history

sealed interface ServicesHistoryScreenAction {

    data class LoadData(val clientId: Long) : ServicesHistoryScreenAction
    data class OnDeleteService(val serviceItem: ServicesHistoryScreenItem.ServiceItem) :
        ServicesHistoryScreenAction

    data class OnServiceClicked(val serviceItem: ServicesHistoryScreenItem.ServiceItem) :
        ServicesHistoryScreenAction

    data object OnCloseScreenClicked : ServicesHistoryScreenAction
    data class OnPaidStatusChanged(val serviceItem: ServicesHistoryScreenItem.ServiceItem) :
        ServicesHistoryScreenAction

    data class OnFinishStatusChanged(val serviceItem: ServicesHistoryScreenItem.ServiceItem) :
        ServicesHistoryScreenAction
}

sealed interface ServicesHistoryScreenEvent {
    data object CloseScreen : ServicesHistoryScreenEvent
    data class OpenServiceInfo(val serviceId: Long) : ServicesHistoryScreenEvent
    data object StatusUpdated : ServicesHistoryScreenEvent
    data object ServiceDeleted : ServicesHistoryScreenEvent
}
