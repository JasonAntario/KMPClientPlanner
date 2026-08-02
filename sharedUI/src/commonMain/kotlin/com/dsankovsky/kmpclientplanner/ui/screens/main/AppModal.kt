package com.dsankovsky.kmpclientplanner.ui.screens.main

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.navigation.Screen
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHost
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalWidth
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientEvent
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientScreen
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceEvent
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceScreen
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServiceScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServicesScreen
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
 * Формы (М1, М3, М5) пока показывают ещё не переписанные экраны — они приходят со своим
 * `Scaffold` и тулбаром, поэтому кладутся в панель без паддинга и без шапки DS. На этапе 6
 * содержимое меняется на форму по макету, а сам этот слой остаётся как есть.
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

    OrganicModalHost(onDismissRequest = onDismiss) {
        when (state) {
            is ModalState.ClientForm -> LegacyFormModal(fullScreen) {
                AddEditClientScreen(
                    clientId = state.clientId,
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
            }

            is ModalState.ServiceForm -> LegacyFormModal(fullScreen) {
                AddEditServiceScreen(
                    serviceId = state.serviceId,
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
            }

            ModalState.Prepay -> LegacyFormModal(fullScreen, width = OrganicModalWidth.Medium) {
                PayServicesScreen(
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

            ModalState.ResetApp -> ResetAppModal(
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

/**
 * Панель под ещё не переписанный экран: без внутренних отступов и без шапки — они у экрана свои.
 */
@Composable
private fun LegacyFormModal(
    fullScreen: Boolean,
    width: Dp = OrganicModalWidth.Form,
    content: @Composable () -> Unit,
) {
    OrganicModal(
        width = width,
        fullScreen = fullScreen,
        contentPadding = 0.dp,
        verticalGap = 0.dp,
    ) {
        content()
    }
}
