package com.dsankovsky.kmpclientplanner.domain.usecases.client

import com.dsankovsky.kmpclientplanner.domain.ClientsListRepository
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

class GetClientsUseCase(
    private val repository: ClientsListRepository
) {

    fun getAllClients(): Flow<List<BaseClient>> {
        return repository.getAllClients()
    }

    fun getClientById(clientId: Long?): Flow<BaseClient?> {
        if (clientId == null) return flowOf(null)
        return repository.getClientByClientId(clientId)
    }

    suspend fun getAddressesList(): List<String> {
        return repository.getAllClients()
            .firstOrNull()
            ?.mapNotNull { it.address }
            ?.distinct() ?: emptyList()
    }
}