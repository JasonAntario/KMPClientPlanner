package com.dsankovsky.kmpclientplanner.ui.screens.services

import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter

sealed interface HomeScreenAction {

    data object LoadData : HomeScreenAction
    data object OnAddServiceClicked: HomeScreenAction
    data class OnServiceClicked(val serviceItem: HomeScreenItem.ServiceItem) : HomeScreenAction
    data class OnFilterClicked(val filter: ServicesFilter) : HomeScreenAction
    data class OnDeleteService(val serviceItem: HomeScreenItem.ServiceItem) : HomeScreenAction
    data class OnPaidStatusChanged(val serviceItem: HomeScreenItem.ServiceItem) : HomeScreenAction
    data class OnFinishStatusChanged(val serviceItem: HomeScreenItem.ServiceItem) : HomeScreenAction
}

sealed interface HomeScreenEvent {
    data class OpenServiceInfo(val serviceId: Long) : HomeScreenEvent
    data object StatusUpdated : HomeScreenEvent
    data object ServiceDeleted : HomeScreenEvent
    data object AddService: HomeScreenEvent
}
