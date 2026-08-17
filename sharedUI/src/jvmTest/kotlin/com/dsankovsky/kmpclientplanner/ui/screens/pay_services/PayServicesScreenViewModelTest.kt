@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.pay_services

import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
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
 * М5 обещает в тексте окна две вещи: платятся **самые ранние** неоплаченные занятия и
 * ровно столько, сколько показывает плашка. Тест держит именно это обещание.
 */
class PayServicesScreenViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `pays the earliest unpaid services and recounts the debt`() = runTest(dispatcher) {
        // В базе они лежат не по порядку — сортировка должна быть в VM, а не «как повезёт».
        val services = MutableStateFlow(
            listOf(
                service(id = 3, day = 27),
                service(id = 1, day = 20),
                service(id = 2, day = 22),
                service(id = 4, day = 29),
                service(id = 5, day = 30, isPaid = true),
            ),
        )
        val clients = MutableStateFlow(listOf(BaseClient(id = 1, name = "Дмитрий", surname = "Лис")))
        val repository = FakeServicesRepository(services)

        val viewModel = PayServicesScreenViewModel(
            getClientsUseCase = GetClientsUseCase(FakeClientsRepository(clients)),
            getServicesUseCase = GetServicesUseCase(repository),
            addEditDeleteServiceUseCase = AddEditDeleteServiceUseCase(repository),
        )
        viewModel.handleActions(PayServiceScreenAction.LoadData())

        assertEquals(4, viewModel.state.value.clients.single().unpaidCount, "оплаченное не считаем")
        assertFalse(viewModel.state.value.isPaymentReady, "без клиента платить нечего")

        viewModel.handleActions(PayServiceScreenAction.OnClientSelected(1))
        val state = viewModel.state.value
        assertEquals(listOf(1L, 2L, 3L, 4L), state.unpaidServices.map { it.id }, "от самого раннего")
        assertEquals(1, state.amount, "начинаем с одного занятия, а не со всего долга")
        assertTrue(state.isPaymentReady)

        // Больше долга не спишешь.
        viewModel.handleActions(PayServiceScreenAction.OnAmountChanged(9))
        assertEquals(4, viewModel.state.value.amount)

        viewModel.handleActions(PayServiceScreenAction.OnAmountChanged(3))
        val toPay = viewModel.state.value
        assertEquals(listOf(1L, 2L, 3L), toPay.servicesToPay.map { it.id })
        assertEquals(180f, toPay.totals.single().money)
        assertEquals(CurrencyItem.BYN, toPay.totals.single().currency)

        viewModel.handleActions(PayServiceScreenAction.OnPayClicked)

        assertEquals(
            listOf(1L, 2L, 3L, 5L),
            services.value.filter { it.isPaid }.map { it.id }.sorted(),
            "оплаченными становятся ровно те занятия, что были в плашке",
        )
        // Долг пересчитался сам: список в модалке живёт на потоке из базы.
        val after = viewModel.state.value
        assertEquals(1, after.clients.single().unpaidCount)
        assertEquals(listOf(4L), after.unpaidServices.map { it.id })
        assertEquals(1, after.amount, "счётчик поджался под остаток долга")
    }

    /**
     * Из карточки клиента модалка открывается с уже выбранным клиентом: сменить его нельзя,
     * и «нечего оплачивать» считается по нему, а не по всем клиентам разом.
     */
    @Test
    fun `preselects the client it was opened for`() = runTest(dispatcher) {
        val services = MutableStateFlow(
            listOf(
                service(id = 1, day = 20),
                service(id = 2, day = 21, clientId = 2),
            ),
        )
        val clients = MutableStateFlow(
            listOf(
                BaseClient(id = 1, name = "Дмитрий", surname = "Лис"),
                BaseClient(id = 2, name = "Анна", surname = "Ковалёва"),
            ),
        )
        val repository = FakeServicesRepository(services)

        val viewModel = PayServicesScreenViewModel(
            getClientsUseCase = GetClientsUseCase(FakeClientsRepository(clients)),
            getServicesUseCase = GetServicesUseCase(repository),
            addEditDeleteServiceUseCase = AddEditDeleteServiceUseCase(repository),
        )
        viewModel.handleActions(PayServiceScreenAction.LoadData(clientId = 1))

        val state = viewModel.state.value
        assertEquals(1L, state.selectedClientId, "клиент из карточки уже выбран")
        assertTrue(state.isClientLocked)
        assertEquals(listOf(1L), state.selectableClients.map { it.client.id }, "чужих в выборе нет")
        assertEquals(listOf(1L), state.unpaidServices.map { it.id })
        assertTrue(state.isPaymentReady)

        // У самого клиента долга нет, хотя у другого он остался.
        services.value = listOf(service(id = 2, day = 21, clientId = 2))
        assertTrue(viewModel.state.value.isEmpty, "долг считаем по выбранному клиенту")

        // Переоткрытие со статистики не должно донашивать прошлого клиента.
        viewModel.handleActions(PayServiceScreenAction.LoadData())
        assertEquals(null, viewModel.state.value.selectedClientId)
        assertFalse(viewModel.state.value.isClientLocked)
        assertFalse(viewModel.state.value.isEmpty, "у второго клиента долг остался")
    }

    private fun service(
        id: Long,
        day: Int,
        isPaid: Boolean = false,
        clientId: Long = 1,
    ): BaseService {
        val dateTime = LocalDateTime(2026, 7, day, 19, 30)
        return BaseService(
            id = id,
            clientId = clientId,
            startDate = dateTime,
            endDate = dateTime,
            price = 60f,
            currency = CurrencyItem.BYN,
            isPaid = isPaid,
        )
    }
}
