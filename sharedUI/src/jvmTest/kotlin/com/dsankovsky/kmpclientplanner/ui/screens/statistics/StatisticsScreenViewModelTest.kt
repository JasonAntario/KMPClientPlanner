@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.statistics

import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.fakes.FakeClientsRepository
import com.dsankovsky.kmpclientplanner.ui.fakes.FakeServicesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
