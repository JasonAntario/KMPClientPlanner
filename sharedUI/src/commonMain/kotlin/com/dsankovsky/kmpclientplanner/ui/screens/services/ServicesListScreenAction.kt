package com.dsankovsky.kmpclientplanner.ui.screens.services

import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter

sealed interface ServicesListScreenAction {

    data object LoadData : ServicesListScreenAction
    data object OnAddServiceClicked: ServicesListScreenAction
    data class OnServiceClicked(val serviceItem: ServicesListScreenItem.ServiceItem) : ServicesListScreenAction

    /** Крестик в деталях: снимает выделение, лента возвращается на всю ширину. */
    data object OnCloseDetailsClicked : ServicesListScreenAction
    data class OnFilterClicked(val filter: ServicesFilter) : ServicesListScreenAction
    data class OnDeleteService(val serviceItem: ServicesListScreenItem.ServiceItem) : ServicesListScreenAction
    data class OnPaidStatusChanged(val serviceItem: ServicesListScreenItem.ServiceItem) : ServicesListScreenAction
    data class OnFinishStatusChanged(val serviceItem: ServicesListScreenItem.ServiceItem) : ServicesListScreenAction
}

sealed interface ServicesListScreenEvent {
    data class OpenServiceInfo(val serviceId: Long) : ServicesListScreenEvent
    data object StatusUpdated : ServicesListScreenEvent
    data object ServiceDeleted : ServicesListScreenEvent
    data object AddService: ServicesListScreenEvent
}
