@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.clients

import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.ui.fakes.FakeClientsRepository
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
import kotlin.test.assertNull

/**
 * Крестик в карточке клиента (экран 08) должен снимать выделение — и оно не должно
 * возвращаться само: на широком окне первый клиент выбирается автоматически, и без
 * отдельного признака «закрыли руками» ближайшее обновление списка открывало карточку заново.
 */
class ClientsScreenViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `close clears selection and list updates do not bring the card back`() = runTest(dispatcher) {
        val anna = BaseClient(id = 1, name = "Анна", surname = "Б")
        val boris = BaseClient(id = 2, name = "Борис", surname = "В")
        val clients = MutableStateFlow(listOf(anna, boris))
        val viewModel = ClientsScreenViewModel(GetClientsUseCase(FakeClientsRepository(clients)))

        viewModel.handleAction(ClientsListScreenAction.LoadClientsList)
        assertEquals(anna.id, viewModel.state.value.selectedClientId, "первый клиент выбран сам")

        viewModel.handleAction(ClientsListScreenAction.OnClientItemClicked(boris))
        assertEquals(boris.id, viewModel.state.value.selectedClientId)

        viewModel.handleAction(ClientsListScreenAction.CloseClientDetails)
        assertNull(viewModel.state.value.selectedClientId, "крестик снимает выделение")

        // База обновилась (например, клиента переименовали в другом месте).
        clients.value = listOf(anna.copy(name = "Анна Мария"), boris)
        assertNull(viewModel.state.value.selectedClientId, "карточка не должна открыться сама")

        // А вот явный выбор снова открывает панель.
        viewModel.handleAction(ClientsListScreenAction.OnClientItemClicked(anna))
        assertEquals(anna.id, viewModel.state.value.selectedClientId)
    }
}
