package com.dsankovsky.kmpclientplanner.ui.screens.clients

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient

@Immutable
data class ClientsListScreenState(
    val isLoading: Boolean = true,
    val clients: List<ClientListItem> = emptyList()
)

sealed interface ClientListItem {
    data class Client(val client: BaseClient) : ClientListItem
    data class LetterDivider(val letter: String) : ClientListItem
}
