package com.dsankovsky.kmpclientplanner.data

import com.dsankovsky.kmpclientplanner.domain.ClientsListRepository
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.data.db.AppDatabase
import com.dsankovsky.kmpclientplanner.data.db.dao.client.ClientsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.client.EducationClientFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.client.SportClientFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.client.TattooClientFieldsDao
import com.dsankovsky.kmpclientplanner.data.mappers.base.toBaseClient
import com.dsankovsky.kmpclientplanner.data.mappers.base.toBaseClientsList
import com.dsankovsky.kmpclientplanner.data.mappers.base.toDbModel
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.client.toEducationClientSpecificFields
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.client.toEducationClientSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.client.toSportClientSpecificFields
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.client.toSportClientSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.client.toTattooClientSpecificFields
import com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.client.toTattooClientSpecificFieldsDbModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClientsListRepositoryImpl(
    database: AppDatabase
) : ClientsListRepository {

    private val clientsDao: ClientsDao = database.clientsDao()
    private val educationClientFieldsDao: EducationClientFieldsDao =
        database.educationClientFieldsDao()
    private val sportClientFieldsDao: SportClientFieldsDao = database.sportClientFieldsDao()
    private val tattooClientFieldsDao: TattooClientFieldsDao = database.tattooClientFieldsDao()

    override fun getAllClients(): Flow<List<BaseClient>> {
        return clientsDao.getAllClients().map { it.toBaseClientsList() }
    }

    override fun getClientByClientId(clientId: Long): Flow<BaseClient> {
        return clientsDao.getClientById(clientId).map { it.toBaseClient() }
    }

    override suspend fun addClient(client: BaseClient): Long {
        return clientsDao.addClient(client.toDbModel())
    }

    override suspend fun updateClient(client: BaseClient) {
        clientsDao.updateClient(client.toDbModel())
    }

    override suspend fun deleteClient(clientId: Long) {
        clientsDao.deleteClient(clientId)
    }

    override fun getEducationSpecificFieldByClientId(clientId: Long): Flow<ClientSpecificFields.EducationClientSpecificFields> {
        return educationClientFieldsDao.getEducationClientSpecificFieldByClientId(clientId)
            .map { it.toEducationClientSpecificFields() }
    }

    override suspend fun addEducationSpecificField(field: ClientSpecificFields.EducationClientSpecificFields): Long {
        return educationClientFieldsDao.addEducationClientSpecificField(field.toEducationClientSpecificFieldsDbModel())
    }

    override suspend fun updateEducationSpecificField(field: ClientSpecificFields.EducationClientSpecificFields) {
        educationClientFieldsDao.updateEducationClientSpecificField(field.toEducationClientSpecificFieldsDbModel())
    }

    override fun getSportSpecificFieldByClientId(clientId: Long): Flow<ClientSpecificFields.SportClientSpecificFields> {
        return sportClientFieldsDao.getSportClientSpecificFieldByClientId(clientId)
            .map { it.toSportClientSpecificFields() }
    }

    override suspend fun addSportSpecificField(field: ClientSpecificFields.SportClientSpecificFields): Long {
        return sportClientFieldsDao.addSportClientSpecificField(field.toSportClientSpecificFieldsDbModel())
    }

    override suspend fun updateSportSpecificField(field: ClientSpecificFields.SportClientSpecificFields) {
        sportClientFieldsDao.updateSportClientSpecificField(field.toSportClientSpecificFieldsDbModel())
    }

    override fun getTattooSpecificFieldByClientId(clientId: Long): Flow<ClientSpecificFields.TattooClientSpecificFields> {
        return tattooClientFieldsDao.getTattooClientSpecificFieldByClientId(clientId)
            .map { it.toTattooClientSpecificFields() }
    }

    override suspend fun addTattooSpecificField(field: ClientSpecificFields.TattooClientSpecificFields): Long {
        return tattooClientFieldsDao.addTattooClientSpecificField(field.toTattooClientSpecificFieldsDbModel())
    }

    override suspend fun updateTattooSpecificField(field: ClientSpecificFields.TattooClientSpecificFields) {
        tattooClientFieldsDao.updateTattooClientSpecificField(field.toTattooClientSpecificFieldsDbModel())
    }
}