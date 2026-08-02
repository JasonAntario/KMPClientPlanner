package com.dsankovsky.kmpclientplanner.ui.screens.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dsankovsky.kmpclientplanner.AppInfo
import com.dsankovsky.kmpclientplanner.navigation.Screen
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsEvents
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsScreen
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsListScreen
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsListScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.screens.main.empty.NoClientsScreen
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreen
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.service_type_selection.ServiceTypeSelectionScreen
import com.dsankovsky.kmpclientplanner.ui.screens.services.HomeScreen
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.services_history.ServicesHistoryScreen
import com.dsankovsky.kmpclientplanner.ui.screens.services_history.ServicesHistoryScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsScreen
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.StatisticsScreen
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.add_edit_service_deleted
import kmpclientplanner.sharedui.generated.resources.client_details_autofill_completed
import kmpclientplanner.sharedui.generated.resources.client_details_status_updated
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {
    val viewModel: MainScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val backStack = remember { mutableStateListOf<Screen>(Screen.LoadingScreen) }
    val screensWithNavBar = listOf(
        Screen.ClientsScreen,
        Screen.HomeScreen,
        Screen.StatisticsScreen,
        Screen.SettingsScreen
    )

    val showNavigationBar by remember {
        derivedStateOf { backStack.lastOrNull() in screensWithNavBar }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.handleActions(MainScreenActions.GetStartDestination)
    }

    viewModel.event.collectWithLifecycle { event ->
        when (event) {
            is MainScreenEvent.Navigate -> {
                backStack.clear()
                backStack.add(event.screen)
            }
        }
    }

    val isWideScreen = isWideWindow()
    val currentScreen by remember { derivedStateOf { backStack.lastOrNull() } }
    // Формы больше не destination'ы: по новому дизайну это модальные окна поверх экрана.
    var modal by remember { mutableStateOf<ModalState?>(null) }
    // Пока шапки экранов не переехали на новый дизайн, «Добавить» живёт в рейле.
    val addAction: (() -> Unit)? = when (currentScreen) {
        Screen.ClientsScreen -> ({ modal = ModalState.ClientForm(clientId = null) })
        Screen.HomeScreen -> ({ modal = ModalState.ServiceForm(serviceId = null) })
        else -> null
    }
    val modalContent: (@Composable () -> Unit)? = modal?.let { current ->
        {
            AppModal(
                state = current,
                fullScreen = !isWideScreen,
                backStack = backStack,
                snackbarHostState = snackbarHostState,
                onDismiss = { modal = null },
                onRestart = { viewModel.handleActions(MainScreenActions.GetStartDestination) },
            )
        }
    }

    AppScaffold(
        wideWindow = isWideScreen,
        showNavigation = showNavigationBar,
        currentScreen = currentScreen,
        onNavigate = { backStack.add(it) },
        snackbarHostState = snackbarHostState,
        railFooter = state.serviceType?.let { "${it.toUIName()} · v${AppInfo.VERSION}" },
        onAddClick = addAction,
        modal = modalContent,
    ) {
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.fillMaxSize(),
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<Screen.LoadingScreen> {
                    LoadingScreen()
                }

                entry<Screen.ServiceTypeSelectionScreen>(
                    metadata = transitionHorizontalSlideAnimation()
                ) {
                    ServiceTypeSelectionScreen(
                        onServiceTypeClicked = {
                            viewModel.handleActions(
                                MainScreenActions.OnServiceTypeSelected(it)
                            )
                        }
                    )
                }

                entry<Screen.ClientDetailsScreen>(
                    metadata = transitionHorizontalSlideAnimation()
                ) {
                    ClientDetailsScreen(
                        clientId = it.clientId,
                        onEvent = { event ->
                            when (event) {
                                ClientDetailsEvents.OnCloseScreen -> backStack.removeLastOrNull()
                                ClientDetailsEvents.OpenEditClientScreen -> {
                                    modal = ModalState.ClientForm(it.clientId)
                                }

                                ClientDetailsEvents.AutofillCompleted -> {
                                    scope.launch {
                                        val message =
                                            getString(Res.string.client_details_autofill_completed)
                                        snackbarHostState.showSnackbar(
                                            message = message,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }

                                ClientDetailsEvents.OpenServicesHistory -> {
                                    backStack.add(Screen.ServicesHistory(it.clientId))
                                }
                            }
                        }
                    )
                }

                entry<Screen.ServiceDetailsScreen>(
                    metadata = transitionHorizontalSlideAnimation()
                ) {
                    val serviceId = it.serviceId
                    ServiceDetailsScreen(
                        serviceId = serviceId,
                        onEvent = {
                            when (it) {
                                ServiceDetailsScreenEvent.OnCloseScreen -> backStack.removeLastOrNull()
                                ServiceDetailsScreenEvent.OpenEditServiceScreen -> {
                                    modal = ModalState.ServiceForm(serviceId)
                                }

                                ServiceDetailsScreenEvent.StatusUpdated -> {
                                    scope.launch {
                                        val message =
                                            getString(Res.string.client_details_status_updated)
                                        snackbarHostState.showSnackbar(
                                            message = message,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        }
                    )
                }

                entry<Screen.HomeScreen> {
                    HomeScreen(
                        onEvent = { event ->
                            when (event) {
                                is ServicesListScreenEvent.OpenServiceInfo -> {
                                    backStack.add(Screen.ServiceDetailsScreen(event.serviceId))
                                }

                                ServicesListScreenEvent.ServiceDeleted -> {
                                    scope.launch {
                                        val message =
                                            getString(Res.string.add_edit_service_deleted)
                                        snackbarHostState.showSnackbar(
                                            message = message,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }

                                ServicesListScreenEvent.StatusUpdated -> {
                                    scope.launch {
                                        val message =
                                            getString(Res.string.client_details_status_updated)
                                        snackbarHostState.showSnackbar(
                                            message = message,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }

                                ServicesListScreenEvent.AddService -> {
                                    modal = ModalState.ServiceForm(serviceId = null)
                                }
                            }
                        }
                    )
                }

                entry<Screen.ServicesHistory>(
                    metadata = transitionHorizontalSlideAnimation()
                ) {
                    ServicesHistoryScreen(
                        clientId = it.clientId,
                        onEvent = { event ->
                            when (event) {
                                is ServicesHistoryScreenEvent.OpenServiceInfo -> {
                                    backStack.add(Screen.ServiceDetailsScreen(event.serviceId))
                                }

                                ServicesHistoryScreenEvent.CloseScreen -> backStack.removeLastOrNull()
                                ServicesHistoryScreenEvent.ServiceDeleted -> {
                                    scope.launch {
                                        val message =
                                            getString(Res.string.add_edit_service_deleted)
                                        snackbarHostState.showSnackbar(
                                            message = message,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }

                                ServicesHistoryScreenEvent.StatusUpdated -> {
                                    scope.launch {
                                        val message =
                                            getString(Res.string.add_edit_service_deleted)
                                        snackbarHostState.showSnackbar(
                                            message = message,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        }
                    )
                }

                entry<Screen.NoClientsScreen> {
                    NoClientsScreen(
                        onAddClientCLicked = {
                            modal = ModalState.ClientForm(clientId = null)
                        },
                        onChangeServiceTypeCLicked = {
                            backStack.add(Screen.ServiceTypeSelectionScreen)
                        }
                    )
                }

                entry<Screen.ClientsScreen> {
                    ClientsListScreen(
                        showFab = !isWideScreen,
                        onEvent = { event ->
                            when (event) {
                                is ClientsListScreenEvent.OpenClientInfo -> {
                                    backStack.add(Screen.ClientDetailsScreen(event.clientId))
                                }

                                ClientsListScreenEvent.AddClient -> {
                                    modal = ModalState.ClientForm(clientId = null)
                                }
                            }
                        }
                    )
                }

                entry<Screen.StatisticsScreen> {
                    StatisticsScreen(
                        onOpenPayServices = {
                            modal = ModalState.Prepay
                        }
                    )
                }

                entry<Screen.SettingsScreen> {
                    SettingsScreen(
                        onEvent = { event ->
                            when (event) {
                                SettingsScreenEvent.ResetRequested -> {
                                    modal = ModalState.ResetApp
                                }

                                SettingsScreenEvent.AllDataCleared -> {
                                    backStack.clear()
                                    backStack.add(Screen.ServiceTypeSelectionScreen)
                                }
                            }
                        }
                    )
                }
            }
        )
    }
}


private fun transitionHorizontalSlideAnimation(): Map<String, Any> {
    return NavDisplay.transitionSpec {
        slideInHorizontally(initialOffsetX = { it }) togetherWith
                slideOutHorizontally(targetOffsetX = { -it })
    } + NavDisplay.popTransitionSpec {
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
    } + NavDisplay.predictivePopTransitionSpec {
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
    }
}
