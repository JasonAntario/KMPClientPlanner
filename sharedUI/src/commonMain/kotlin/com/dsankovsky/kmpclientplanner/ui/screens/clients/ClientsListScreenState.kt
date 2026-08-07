package com.dsankovsky.kmpclientplanner.ui.screens.clients

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient

/**
 * Состояние списка клиентов (левая панель экрана 08).
 *
 * @param clients уже отфильтрованный поиском список с разделителями по первой букве
 * @param clientsCount всего клиентов — счётчик в заголовке «Клиенты · N» не зависит от поиска
 * @param selectedClientId выделенная строка; в master-detail она же определяет, что показывает
 *   правая панель
 */
@Immutable
data class ClientsListScreenState(
    val isLoading: Boolean = true,
    val clients: List<ClientListItem> = emptyList(),
    val clientsCount: Int = 0,
    val searchQuery: String = "",
    val selectedClientId: Long? = null,
)

sealed interface ClientListItem {
    data class Client(val client: BaseClient) : ClientListItem
    data class LetterDivider(val letter: String) : ClientListItem
}
