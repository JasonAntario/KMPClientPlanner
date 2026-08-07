package com.dsankovsky.kmpclientplanner.ui.screens.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsListScreenEvent.OpenClientInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientsScreenViewModel(
    private val getClientsUseCase: GetClientsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ClientsListScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<ClientsListScreenEvent>()

    /** Весь список: поиск фильтрует его в UI-модель, не перезапрашивая базу. */
    private var allClients: List<BaseClient> = emptyList()

    /**
     * Карточку закрыли руками — значит, при следующем обновлении списка первый клиент
     * не должен выбраться сам и вернуть панель обратно.
     */
    private var detailsClosedByUser = false

    fun handleAction(action: ClientsListScreenAction) {
        when (action) {
            ClientsListScreenAction.LoadClientsList -> loadClients()

            is ClientsListScreenAction.OnSearchQueryChanged -> {
                _state.update {
                    it.copy(
                        searchQuery = action.query,
                        clients = allClients.toListItems(action.query),
                    )
                }
            }

            is ClientsListScreenAction.OnClientItemClicked -> {
                detailsClosedByUser = false
                _state.update { it.copy(selectedClientId = action.client.id) }
                viewModelScope.launch { event.emit(OpenClientInfo(action.client.id)) }
            }

            ClientsListScreenAction.CloseClientDetails -> {
                detailsClosedByUser = true
                _state.update { it.copy(selectedClientId = null) }
                viewModelScope.launch { event.emit(ClientsListScreenEvent.CloseClientInfo) }
            }

            ClientsListScreenAction.AddClientClicked -> {
                viewModelScope.launch { event.emit(ClientsListScreenEvent.AddClient) }
            }
        }
    }

    private fun loadClients() {
        viewModelScope.launch {
            getClientsUseCase
                .getAllClients()
                .collectLatest { clients ->
                    // Секции идут по алфавиту, поэтому сортируем один раз здесь,
                    // а не полагаемся на порядок выдачи базы.
                    allClients = clients.sortedBy { it.getFullName().lowercase() }

                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            clients = allClients.toListItems(state.searchQuery),
                            clientsCount = allClients.size,
                            // Панель деталей не должна остаться на удалённом клиенте;
                            // на широком окне первый в списке выбирается сам — но только
                            // пока карточку не закрыли крестиком.
                            selectedClientId = state.selectedClientId
                                ?.takeIf { id -> allClients.any { it.id == id } }
                                ?: allClients.firstOrNull()?.id?.takeIf { !detailsClosedByUser },
                        )
                    }
                }
        }
    }
}

private fun List<BaseClient>.toListItems(query: String): List<ClientListItem> {
    val trimmed = query.trim()
    val filtered = if (trimmed.isEmpty()) {
        this
    } else {
        filter { it.getFullName().contains(trimmed, ignoreCase = true) }
    }

    return buildList {
        filtered
            .groupBy { it.getFullName().take(1).uppercase() }
            .forEach { (letter, clients) ->
                add(ClientListItem.LetterDivider(letter))
                addAll(clients.map { ClientListItem.Client(it) })
            }
    }
}
