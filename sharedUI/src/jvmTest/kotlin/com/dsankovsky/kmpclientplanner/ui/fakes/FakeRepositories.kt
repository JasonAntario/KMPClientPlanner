package com.dsankovsky.kmpclientplanner.ui.fakes

import com.dsankovsky.kmpclientplanner.domain.ClientsListRepository
import com.dsankovsky.kmpclientplanner.domain.ServicesListRepository
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime

/**
 * Репозитории на `MutableStateFlow` для тестов вью-моделей: экраны читают базу потоками,
 * поэтому проверять их проще всего, подменяя значение потока «как будто база обновилась».
 *
 * Всё, чего конкретному тесту не нужно, падает через [notNeeded] — так забытый вызов
 * виден сразу, а не превращается в пустой список.
 */
class FakeServicesRepository(
    private val services: MutableStateFlow<List<BaseService>>,
) : ServicesListRepository {

    override fun getAllServices(): Flow<List<BaseService>> = services

    override fun getAllServicesForHomeScreen(): Flow<List<BaseService>> = services
    override fun getServiceByServiceId(serviceId: Long): Flow<BaseService> = notNeeded()
    override suspend fun getServiceByClientId(clientId: Long): List<BaseService> = notNeeded()
    override fun getServiceByClientIdFlow(clientId: Long): Flow<List<BaseService>> = notNeeded()
    override suspend fun getLastServiceByClientId(clientId: Long): BaseService = notNeeded()
    override suspend fun addService(service: BaseService): Long = notNeeded()

    /** Запись правда меняется в потоке — иначе не проверить, что экран пересчитался. */
    override suspend fun updateService(service: BaseService) {
        services.update { list -> list.map { if (it.id == service.id) service else it } }
    }

    override suspend fun deleteService(service: BaseService) = deleteService(service.id)

    override suspend fun deleteService(serviceId: Long) {
        services.update { list -> list.filterNot { it.id == serviceId } }
    }

    override fun getAllUnpaidServices(clientId: Long): Flow<List<BaseService>> =
        services.map { list -> list.filter { it.clientId == clientId && !it.isPaid } }
    override suspend fun getServicesInInterval(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ): List<BaseService> = notNeeded()

    override fun getEducationSpecificFieldById(
        serviceId: Long,
    ): Flow<ServiceSpecificFields.EducationServiceSpecificFields> = notNeeded()

    override suspend fun addEducationSpecificField(
        field: ServiceSpecificFields.EducationServiceSpecificFields,
    ): Long = notNeeded()

    override suspend fun updateEducationSpecificField(
        field: ServiceSpecificFields.EducationServiceSpecificFields,
    ) = notNeeded()

    override fun getBeautySpecificFieldById(
        serviceId: Long,
    ): Flow<ServiceSpecificFields.BeautyServiceSpecificFields> = notNeeded()

    override suspend fun addBeautySpecificField(
        field: ServiceSpecificFields.BeautyServiceSpecificFields,
    ): Long = notNeeded()

    override suspend fun updateBeautySpecificField(
        field: ServiceSpecificFields.BeautyServiceSpecificFields,
    ) = notNeeded()

    override fun getTattooSpecificFieldById(
        serviceId: Long,
    ): Flow<ServiceSpecificFields.TattooServiceSpecificFields> = notNeeded()

    override suspend fun addTattooSpecificField(
        field: ServiceSpecificFields.TattooServiceSpecificFields,
    ): Long = notNeeded()

    override suspend fun updateTattooSpecificField(
        field: ServiceSpecificFields.TattooServiceSpecificFields,
    ) = notNeeded()

    override fun getSportSpecificFieldById(
        serviceId: Long,
    ): Flow<ServiceSpecificFields.SportServiceSpecificFields> = notNeeded()

    override suspend fun addSportSpecificField(
        field: ServiceSpecificFields.SportServiceSpecificFields,
    ): Long = notNeeded()

    override suspend fun updateSportSpecificField(
        field: ServiceSpecificFields.SportServiceSpecificFields,
    ) = notNeeded()
}

class FakeClientsRepository(
    private val clients: MutableStateFlow<List<BaseClient>>,
) : ClientsListRepository {

    override fun getAllClients(): Flow<List<BaseClient>> = clients

    override fun getClientByClientId(clientId: Long): Flow<BaseClient> = notNeeded()
    override suspend fun addClient(client: BaseClient): Long = notNeeded()
    override suspend fun updateClient(client: BaseClient) = notNeeded()
    override suspend fun deleteClient(clientId: Long) = notNeeded()

    override fun getEducationSpecificFieldByClientId(
        clientId: Long,
    ): Flow<ClientSpecificFields.EducationClientSpecificFields> = notNeeded()

    override suspend fun addEducationSpecificField(
        field: ClientSpecificFields.EducationClientSpecificFields,
    ): Long = notNeeded()

    override suspend fun updateEducationSpecificField(
        field: ClientSpecificFields.EducationClientSpecificFields,
    ) = notNeeded()

    override fun getSportSpecificFieldByClientId(
        clientId: Long,
    ): Flow<ClientSpecificFields.SportClientSpecificFields> = notNeeded()

    override suspend fun addSportSpecificField(
        field: ClientSpecificFields.SportClientSpecificFields,
    ): Long = notNeeded()

    override suspend fun updateSportSpecificField(
        field: ClientSpecificFields.SportClientSpecificFields,
    ) = notNeeded()

    override fun getTattooSpecificFieldByClientId(
        clientId: Long,
    ): Flow<ClientSpecificFields.TattooClientSpecificFields> = notNeeded()

    override suspend fun addTattooSpecificField(
        field: ClientSpecificFields.TattooClientSpecificFields,
    ): Long = notNeeded()

    override suspend fun updateTattooSpecificField(
        field: ClientSpecificFields.TattooClientSpecificFields,
    ) = notNeeded()
}

private fun notNeeded(): Nothing = error("этот вызов репозитория тесту не нужен")
