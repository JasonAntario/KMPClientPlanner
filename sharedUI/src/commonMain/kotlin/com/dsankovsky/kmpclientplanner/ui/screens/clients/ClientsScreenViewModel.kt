package com.dsankovsky.kmpclientplanner.ui.screens.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.usecases.client.AddEditDeleteClientUseCase
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
    private val addEditDeleteClientUseCase: AddEditDeleteClientUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ClientsListScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<ClientsListScreenEvent>()

    fun handleAction(action: ClientsListScreenAction) {
        when (action) {
            ClientsListScreenAction.LoadClientsList -> loadClients()
            is ClientsListScreenAction.OnClientItemClicked -> {
                viewModelScope.launch {
                    event.emit(OpenClientInfo(action.client.id))
                }
            }

            is ClientsListScreenAction.OnClientDeleteClicked -> deleteClient(action.client)
            ClientsListScreenAction.AddClientClicked -> {
                viewModelScope.launch {
                    event.emit(ClientsListScreenEvent.AddClient)
                }
            }
        }
    }

    private fun loadClients() {
        viewModelScope.launch {
            getClientsUseCase
                .getAllClients()
                .collectLatest { clients ->
                    val items = buildList {
                        clients.groupBy { it.name.take(1) }.map {
                            val letter = it.key
                            val clientsList = it.value
                            add(ClientListItem.LetterDivider(letter))
                            addAll(clientsList.map { ClientListItem.Client(it) })
                        }
                    }

                    _state.update {
                        it.copy(clients = items, isLoading = false)
                    }
                }
        }
    }

    private fun deleteClient(client: BaseClient) {
        viewModelScope.launch {
            addEditDeleteClientUseCase.deleteClient(client.id)
        }
    }
}