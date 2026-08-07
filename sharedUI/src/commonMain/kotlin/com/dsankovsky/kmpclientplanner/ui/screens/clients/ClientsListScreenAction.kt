package com.dsankovsky.kmpclientplanner.ui.screens.clients

import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient

sealed interface ClientsListScreenAction {

    data object LoadClientsList : ClientsListScreenAction
    data object AddClientClicked : ClientsListScreenAction
    data class OnClientItemClicked(val client: BaseClient) : ClientsListScreenAction
    data class OnSearchQueryChanged(val query: String) : ClientsListScreenAction
}

sealed interface ClientsListScreenEvent {
    /** Строка выбрана: на широком окне подсвечивает её, на узком — открывает панель деталей. */
    data class OpenClientInfo(val clientId: Long) : ClientsListScreenEvent
    data object AddClient : ClientsListScreenEvent
}
