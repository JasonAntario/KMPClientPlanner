@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.clients

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsEmptyPane
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsPane
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Экран 08 — клиенты: список слева, карточка выбранного клиента справа.
 *
 * Форм в панелях нет: редактирование клиента и добавление нового поднимают модалку
 * уровня приложения ([onAddClient] / [onEditClient]), поэтому наружу уходят только
 * события, за которые отвечает `MainScreen`.
 *
 * Рейл живёт снаружи, в `AppScaffold`, — scaffold делит только оставшуюся ширину.
 */
@Composable
fun ClientsScreen(
    onAddClient: () -> Unit,
    onEditClient: (clientId: Long) -> Unit,
    onPrepayClient: (clientId: Long) -> Unit,
    onOpenServicesHistory: (clientId: Long) -> Unit,
    onAutofillCompleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ClientsScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
    val scope = rememberCoroutineScope()

    viewModel.event.collectWithLifecycle { event ->
        when (event) {
            // На узком окне панели показываются по одной, поэтому выбор клиента —
            // это ещё и переход к деталям.
            is ClientsListScreenEvent.OpenClientInfo -> scope.launch {
                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, event.clientId)
            }

            // На широком окне пустую панель показывает сам scaffold, а на узком надо ещё
            // и вернуться на список — иначе останется пустая панель без выхода.
            ClientsListScreenEvent.CloseClientInfo -> scope.launch {
                navigator.navigateTo(ListDetailPaneScaffoldRole.List)
            }

            ClientsListScreenEvent.AddClient -> onAddClient()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.handleAction(ClientsListScreenAction.LoadClientsList)
    }

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    val selectedClientId = state.selectedClientId
    ListDetailPaneScaffold(
        // Панели в макете стоят вплотную: список отделён своим фоном, а не воздухом.
        directive = navigator.scaffoldDirective.copy(horizontalPartitionSpacerSize = 0.dp),
        value = navigator.scaffoldValue,
        modifier = modifier,
        listPane = {
            AnimatedPane(Modifier.preferredWidth(ClientsListPaneWidth)) {
                ClientsListPane(state = state, onAction = viewModel::handleAction)
            }
        },
        detailPane = {
            AnimatedPane {
                if (selectedClientId == null) {
                    ClientDetailsEmptyPane()
                } else {
                    ClientDetailsPane(
                        clientId = selectedClientId,
                        onEditClient = { onEditClient(selectedClientId) },
                        onPrepayClient = { onPrepayClient(selectedClientId) },
                        onOpenServicesHistory = { onOpenServicesHistory(selectedClientId) },
                        onAutofillCompleted = onAutofillCompleted,
                        // Список сам подтянет удаление клиента: он на flow из базы.
                        onClientDeleted = {
                            scope.launch { navigator.navigateBack() }
                        },
                        // Крестик работает на любой ширине: выделение снимается, справа
                        // остаётся заглушка «выберите клиента».
                        onClose = {
                            viewModel.handleAction(ClientsListScreenAction.CloseClientDetails)
                        },
                    )
                }
            }
        },
    )
}
