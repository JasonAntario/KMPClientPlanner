package com.dsankovsky.kmpclientplanner.ui.screens.main

import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.navigation.Screen

sealed interface MainScreenActions {
    data object GetStartDestination : MainScreenActions
    data class OnServiceTypeSelected(val serviceType: ServiceType) : MainScreenActions
}

sealed interface MainScreenEvent {
    data class Navigate(val screen: Screen) : MainScreenEvent
}