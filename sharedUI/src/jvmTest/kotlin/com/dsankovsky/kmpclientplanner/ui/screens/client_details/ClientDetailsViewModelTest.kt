@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.client_details

import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.usecases.client.AddEditDeleteClientUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditServiceSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AutofillServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.CheckServiceCrossingUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.ui.fakes.FakeClientsRepository
import com.dsankovsky.kmpclientplanner.ui.fakes.FakeServicesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Метрики карточки клиента: «не оплачено» — это долг, а не сумма всех занятий.
 * Будущее занятие ещё не проведено, поэтому в долг не идёт, — так же считает статистика.
 */
class ClientDetailsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `unpaid metric counts only finished services`() = runTest(dispatcher) {
        val services = MutableStateFlow(
            listOf(
                service(id = 1, day = 10, isFinished = true),
                service(id = 2, day = 11, isFinished = true),
                // Проведено и оплачено — не долг.
                service(id = 3, day = 12, isFinished = true, isPaid = true),
                // Ещё не проведено: в «не оплачено» попадать не должно.
                service(id = 4, day = 20),
                service(id = 5, day = 21),
                // Предоплаченное будущее занятие — тег «предоплачено», но не долг.
                service(id = 6, day = 22, isPaid = true),
            ),
        )
        val viewModel = viewModel(services)

        viewModel.handleActions(ClientDetailsActions.LoadData(ClientId))

        val state = viewModel.state.value
        assertEquals(120f, state.unpaidTotals.single().money, "долг — только за занятия 1 и 2")
        assertEquals(CurrencyItem.BYN, state.unpaidTotals.single().currency)
        assertEquals(1, state.prepaidCount, "предоплачено занятие 6")
        assertEquals(6, state.servicesCount)
    }

    @Test
    fun `prepay button follows unpaid services including future ones`() = runTest(dispatcher) {
        // Долга нет — всё проведённое оплачено, — но будущее занятие можно предоплатить.
        val services = MutableStateFlow(
            listOf(
                service(id = 1, day = 10, isFinished = true, isPaid = true),
                service(id = 2, day = 20),
            ),
        )
        val viewModel = viewModel(services)

        viewModel.handleActions(ClientDetailsActions.LoadData(ClientId))

        assertTrue(viewModel.state.value.unpaidTotals.isEmpty(), "проведённое всё оплачено")
        assertTrue(viewModel.state.value.showPrepay, "будущее занятие ещё можно предоплатить")

        services.value = listOf(service(id = 1, day = 10, isFinished = true, isPaid = true))
        assertFalse(viewModel.state.value.showPrepay, "оплачивать больше нечего")
    }

    private fun viewModel(services: MutableStateFlow<List<BaseService>>): ClientDetailsViewModel {
        val clients = MutableStateFlow(
            listOf(BaseClient(id = ClientId, name = "Дмитрий", surname = "Лис")),
        )
        val clientsRepository = FakeClientsRepository(clients)
        val servicesRepository = FakeServicesRepository(services)
        val getClientsUseCase = GetClientsUseCase(clientsRepository)
        val getServicesUseCase = GetServicesUseCase(servicesRepository)
        val getClientSpecificFieldsUseCase = GetClientSpecificFieldsUseCase(clientsRepository)

        return ClientDetailsViewModel(
            getClientsUseCase = getClientsUseCase,
            getClientSpecificFieldsUseCase = getClientSpecificFieldsUseCase,
            addEditDeleteClientUseCase = AddEditDeleteClientUseCase(clientsRepository),
            getServicesUseCase = getServicesUseCase,
            autofillServiceUseCase = AutofillServiceUseCase(
                getClientsUseCase = getClientsUseCase,
                checkServiceCrossingUseCase = CheckServiceCrossingUseCase(getServicesUseCase),
                addEditDeleteServiceUseCase = AddEditDeleteServiceUseCase(servicesRepository),
                addEditServiceSpecificFieldsUseCase =
                    AddEditServiceSpecificFieldsUseCase(servicesRepository),
                getClientSpecificFieldsUseCase = getClientSpecificFieldsUseCase,
            ),
        )
    }

    private fun service(
        id: Long,
        day: Int,
        isFinished: Boolean = false,
        isPaid: Boolean = false,
    ): BaseService {
        val dateTime = LocalDateTime(2026, 7, day, 19, 30)
        return BaseService(
            id = id,
            clientId = ClientId,
            startDate = dateTime,
            endDate = dateTime,
            price = 60f,
            currency = CurrencyItem.BYN,
            isFinished = isFinished,
            isPaid = isPaid,
            serviceType = ServiceType.BASE,
        )
    }

    private companion object {
        const val ClientId = 1L
    }
}
