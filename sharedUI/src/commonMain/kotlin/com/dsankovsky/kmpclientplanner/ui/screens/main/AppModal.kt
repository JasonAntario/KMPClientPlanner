package com.dsankovsky.kmpclientplanner.ui.screens.main

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.dsankovsky.kmpclientplanner.navigation.Screen
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHost
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientEvent
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.ClientFormModal
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceEvent
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.ServiceFormModal
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServiceScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PrepayModal
import com.dsankovsky.kmpclientplanner.ui.screens.settings.ResetAppModal
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.add_edit_client_created
import kmpclientplanner.sharedui.generated.resources.add_edit_client_deleted
import kmpclientplanner.sharedui.generated.resources.add_edit_client_updated
import kmpclientplanner.sharedui.generated.resources.add_edit_service_created
import kmpclientplanner.sharedui.generated.resources.add_edit_service_deleted
import kmpclientplanner.sharedui.generated.resources.add_edit_service_updated
import kmpclientplanner.sharedui.generated.resources.client_details_autofill_completed
import kmpclientplanner.sharedui.generated.resources.services_paid
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

/**
 * Раскрывает [ModalState] в конкретное окно.
 *
 * Скрим и панель — забота самого окна: формы (М1, М3) перехватывают Esc и клик мимо, чтобы
 * с несохранёнными правками сначала спросить М9, а не закрыться молча. Остальные окна отдают
 * закрытие наружу как есть.
 */
@Composable
fun AppModal(
    state: ModalState,
    fullScreen: Boolean,
    backStack: MutableList<Screen>,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
    onRestart: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    fun toast(resource: StringResource) {
        scope.launch {
            snackbarHostState.showSnackbar(getString(resource), duration = SnackbarDuration.Short)
        }
    }

    when (state) {
        is ModalState.ClientForm -> ClientFormModal(
            clientId = state.clientId,
            fullScreen = fullScreen,
            onEvent = { event ->
                when (event) {
                    AddEditClientEvent.OnDismissClicked -> onDismiss()

                    AddEditClientEvent.OnClientSaved -> {
                        toast(
                            if (state.clientId != null) {
                                Res.string.add_edit_client_updated
                            } else {
                                Res.string.add_edit_client_created
                            },
                        )
                        onDismiss()
                        // Первый клиент добавлен с экрана «нет клиентов» — на нём
                        // больше нечего показывать, стартовый экран пересчитывается.
                        if (backStack.lastOrNull() == Screen.NoClientsScreen) onRestart()
                    }

                    is AddEditClientEvent.OnClientDeleted -> {
                        toast(Res.string.add_edit_client_deleted)
                        onDismiss()
                        if (event.noClients) {
                            onRestart()
                        } else {
                            // Карточка удалённого клиента под модалкой больше не нужна.
                            backStack.clear()
                            backStack.add(Screen.ClientsScreen)
                        }
                    }

                    AddEditClientEvent.AutofillCompleted -> {
                        toast(Res.string.client_details_autofill_completed)
                    }
                }
            },
        )

        is ModalState.ServiceForm -> ServiceFormModal(
            serviceId = state.serviceId,
            fullScreen = fullScreen,
            onEvent = { event ->
                when (event) {
                    AddEditServiceEvent.OnDismissClicked -> onDismiss()

                    AddEditServiceEvent.OnServiceSaved -> {
                        toast(
                            if (state.serviceId != null) {
                                Res.string.add_edit_service_updated
                            } else {
                                Res.string.add_edit_service_created
                            },
                        )
                        onDismiss()
                    }

                    AddEditServiceEvent.OnServiceDeleted -> {
                        toast(Res.string.add_edit_service_deleted)
                        onDismiss()
                        // Детали удалённой услуги под модалкой закрываем.
                        if (backStack.lastOrNull() is Screen.ServiceDetailsScreen) {
                            backStack.removeLastOrNull()
                        }
                    }
                }
            },
        )

        ModalState.Prepay -> OrganicModalHost(onDismissRequest = onDismiss) {
            PrepayModal(
                fullScreen = fullScreen,
                onEvent = { event ->
                    when (event) {
                        PayServiceScreenEvent.OnDismissClicked -> onDismiss()
                        PayServiceScreenEvent.OnSuccess -> {
                            toast(Res.string.services_paid)
                            onDismiss()
                        }
                    }
                },
            )
        }

        ModalState.ResetApp -> OrganicModalHost(onDismissRequest = onDismiss) {
            ResetAppModal(
                onDismiss = onDismiss,
                onCleared = {
                    onDismiss()
                    backStack.clear()
                    backStack.add(Screen.ServiceTypeSelectionScreen)
                },
                fullScreen = fullScreen,
            )
        }
    }
}
