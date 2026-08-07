@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.statistics

import com.dsankovsky.kmpclientplanner.domain.ClientsListRepository
import com.dsankovsky.kmpclientplanner.domain.ServicesListRepository
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Экран статистики должен следовать за данными.
 *
 * Раньше он брал занятия снимком (`firstOrNull`), и после предоплаты в модалке М5 суммы
 * под ней оставались прежними: пересчёт запускался только при заходе на экран.
 */
class StatisticsScreenViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `paid service marked in another screen updates the totals`() = runTest(dispatcher) {
        val today = getCurrentDateTime()
        val unpaid = BaseService(
            id = 1,
            clientId = 1,
            startDate = today,
            endDate = today,
            price = 40f,
            currency = CurrencyItem.BYN,
            isFinished = true,
        )
        val services = MutableStateFlow(listOf(unpaid))
        val clients = MutableStateFlow(listOf(BaseClient(id = 1, name = "Анна")))

        val viewModel = StatisticsScreenViewModel(
            getClientsUseCase = GetClientsUseCase(FakeClientsRepository(clients)),
            getServicesUseCase = GetServicesUseCase(FakeServicesRepository(services)),
        )
        viewModel.handleAction(StatisticsScreenAction.LoadData)

        assertEquals(0, viewModel.state.value.servicesPaid, "до оплаты нет оплаченных занятий")
        assertEquals(40f, viewModel.state.value.expectedTotalByCurrency.single().money)

        // Так же, как это делает предоплата: занятие становится оплаченным в базе.
        services.value = listOf(unpaid.copy(isPaid = true))

        assertEquals(1, viewModel.state.value.servicesPaid, "статистика должна пересчитаться")
        assertEquals(40f, viewModel.state.value.receivedTotalByCurrency.single().money)
        assertEquals(emptyList(), viewModel.state.value.expectedTotalByCurrency)
        assertEquals(1f, viewModel.state.value.paidPercentage)
    }
}

/** Из репозитория статистике нужен только поток всех занятий. */
private class FakeServicesRepository(
    private val services: MutableStateFlow<List<BaseService>>,
) : ServicesListRepository {

    override fun getAllServices(): Flow<List<BaseService>> = services

    override fun getAllServicesForHomeScreen(): Flow<List<BaseService>> = services
    override fun getServiceByServiceId(serviceId: Long): Flow<BaseService> = notNeeded()
    override suspend fun getServiceByClientId(clientId: Long): List<BaseService> = notNeeded()
    override fun getServiceByClientIdFlow(clientId: Long): Flow<List<BaseService>> = notNeeded()
    override suspend fun getLastServiceByClientId(clientId: Long): BaseService = notNeeded()
    override suspend fun addService(service: BaseService): Long = notNeeded()
    override suspend fun updateService(service: BaseService) = notNeeded()
    override suspend fun deleteService(service: BaseService) = notNeeded()
    override suspend fun deleteService(serviceId: Long) = notNeeded()
    override fun getAllUnpaidServices(clientId: Long): Flow<List<BaseService>> = notNeeded()
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

private class FakeClientsRepository(
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

private fun notNeeded(): Nothing = error("экрану статистики этот вызов не нужен")
