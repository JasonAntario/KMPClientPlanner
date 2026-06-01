package com.dsankovsky.kmpclientplanner.domain

import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import kotlinx.coroutines.flow.Flow

interface ClientsListRepository {

    fun getAllClients(): Flow<List<BaseClient>>
    fun getClientByClientId(clientId: Long): Flow<BaseClient>
    suspend fun addClient(client: BaseClient): Long
    suspend fun updateClient(client: BaseClient)
    suspend fun deleteClient(clientId: Long)

    fun getEducationSpecificFieldByClientId(clientId: Long): Flow<ClientSpecificFields.EducationClientSpecificFields>
    suspend fun addEducationSpecificField(field: ClientSpecificFields.EducationClientSpecificFields): Long
    suspend fun updateEducationSpecificField(field: ClientSpecificFields.EducationClientSpecificFields)

    fun getSportSpecificFieldByClientId(clientId: Long): Flow<ClientSpecificFields.SportClientSpecificFields>
    suspend fun addSportSpecificField(field: ClientSpecificFields.SportClientSpecificFields): Long
    suspend fun updateSportSpecificField(field: ClientSpecificFields.SportClientSpecificFields)

    fun getTattooSpecificFieldByClientId(clientId: Long): Flow<ClientSpecificFields.TattooClientSpecificFields>
    suspend fun addTattooSpecificField(field: ClientSpecificFields.TattooClientSpecificFields): Long
    suspend fun updateTattooSpecificField(field: ClientSpecificFields.TattooClientSpecificFields)

}