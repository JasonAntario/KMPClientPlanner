package com.dsankovsky.kmpclientplanner.ui.screens.clients

import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient

sealed interface ClientsListScreenAction {

    data object LoadClientsList : ClientsListScreenAction
    data object AddClientClicked : ClientsListScreenAction
    data class OnClientItemClicked(val client: BaseClient) : ClientsListScreenAction
    data class OnSearchQueryChanged(val query: String) : ClientsListScreenAction

    /** Крестик в карточке: снимает выделение и оставляет только список. */
    data object CloseClientDetails : ClientsListScreenAction
}

sealed interface ClientsListScreenEvent {
    /** Строка выбрана: на широком окне подсвечивает её, на узком — открывает панель деталей. */
    data class OpenClientInfo(val clientId: Long) : ClientsListScreenEvent

    /** Выделение снято: на узком окне надо вернуть на передний план панель списка. */
    data object CloseClientInfo : ClientsListScreenEvent
    data object AddClient : ClientsListScreenEvent
}
