package com.dsankovsky.kmpclientplanner.ui.screens.clients

import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient

sealed interface ClientsListScreenAction {

    data object LoadClientsList : ClientsListScreenAction
    data class OnClientItemClicked(val client: BaseClient) : ClientsListScreenAction
    data class OnClientDeleteClicked(val client: BaseClient) : ClientsListScreenAction
}

sealed interface ClientsListScreenEvent {
    data class OpenClientInfo(val clientId: Long) : ClientsListScreenEvent
}