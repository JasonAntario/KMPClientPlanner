@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.services

import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.fakes.FakeClientsRepository
import com.dsankovsky.kmpclientplanner.ui.fakes.FakeServicesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Выделение занятия на экране 04 живёт в состоянии, а не в истории `ListDetailPaneScaffold`:
 * крестик в деталях должен возвращать ленту на всю ширину, а удалённое занятие — не оставаться
 * открытым в правой панели.
 */
class ServicesScreenViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `close clears the selection and deleted service does not stay open`() = runTest(dispatcher) {
        val now = getCurrentDateTime()
        val service = BaseService(
            id = 1,
            clientId = 1,
            title = "Английский",
            startDate = now,
            endDate = now,
            price = 40f,
        )
        val services = MutableStateFlow(listOf(service))
        val clients = MutableStateFlow(listOf(BaseClient(id = 1, name = "Анна")))
        val servicesRepository = FakeServicesRepository(services)

        val viewModel = ServicesScreenViewModel(
            getServicesUseCase = GetServicesUseCase(servicesRepository),
            getClientsUseCase = GetClientsUseCase(FakeClientsRepository(clients)),
            addEditDeleteServiceUseCase = AddEditDeleteServiceUseCase(servicesRepository),
        )
        viewModel.handleAction(ServicesListScreenAction.LoadData)

        // Лента грузится на Dispatchers.IO, поэтому ждём первое непустое состояние.
        val item = viewModel.state
            .first { state -> state.items.any { it is ServicesListScreenItem.ServiceItem } }
            .items
            .filterIsInstance<ServicesListScreenItem.ServiceItem>()
            .first()
        assertNull(viewModel.state.value.selectedServiceId, "лента открывается без деталей")

        viewModel.handleAction(ServicesListScreenAction.OnServiceClicked(item))
        assertEquals(item.id, viewModel.state.value.selectedServiceId)

        viewModel.handleAction(ServicesListScreenAction.OnCloseDetailsClicked)
        assertNull(viewModel.state.value.selectedServiceId, "крестик закрывает детали")

        viewModel.handleAction(ServicesListScreenAction.OnServiceClicked(item))
        services.value = emptyList()

        viewModel.state.first { it.items.isEmpty() }
        assertNull(
            viewModel.state.value.selectedServiceId,
            "детали не должны остаться на удалённом занятии",
        )
    }
}
