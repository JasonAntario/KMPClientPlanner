package com.dsankovsky.kmpclientplanner.domain.usecases.client

import com.dsankovsky.kmpclientplanner.domain.ClientsListRepository
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient

class AddEditDeleteClientUseCase(
    private val repository: ClientsListRepository
) {

    suspend fun addClient(client: BaseClient): Long {
        return repository.addClient(client)
    }

    suspend fun update(client: BaseClient) {
        repository.updateClient(client)
    }

    suspend fun deleteClient(clientId: Long) {
        repository.deleteClient(clientId)
    }
}