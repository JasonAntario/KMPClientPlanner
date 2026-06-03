package com.dsankovsky.kmpclientplanner.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Screen : NavKey {

    @Serializable
    data object WelcomeScreen : Screen()

    @Serializable
    data object LoadingScreen : Screen()

    @Serializable
    data object NoClientsScreen : Screen()

    @Serializable
    data object ServiceTypeSelectionScreen : Screen()

    @Serializable
    data object HomeScreen : Screen()

    @Serializable
    data class ServicesHistory(val clientId: Long) : Screen()

    @Serializable
    data object ClientsScreen : Screen()

    @Serializable
    data object StatisticsScreen : Screen()

    @Serializable
    data object SettingsScreen : Screen()

    @Serializable
    data class ClientDetailsScreen(val clientId: Long) : Screen()

    @Serializable
    data class ServiceDetailsScreen(val serviceId: Long) : Screen()

    @Serializable
    data class AddEditClientScreen(val clientId: Long? = null) : Screen()

    @Serializable
    data class AddEditServiceScreen(val serviceId: Long? = null) : Screen()

    @Serializable
    data object PayServicesScreen : Screen()
}