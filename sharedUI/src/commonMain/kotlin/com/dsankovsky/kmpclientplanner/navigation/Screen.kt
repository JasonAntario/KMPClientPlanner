package com.dsankovsky.kmpclientplanner.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Экраны в backstack. Формы клиента и услуги и предоплата сюда не входят: по новому
 * дизайну это модальные окна поверх экрана — см. `ModalState`.
 *
 * Карточки клиента здесь тоже нет: на экране 08 это правая панель master-detail.
 */
sealed class Screen : NavKey {

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
    data class ServiceDetailsScreen(val serviceId: Long) : Screen()
}