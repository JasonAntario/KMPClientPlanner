package com.dsankovsky.kmpclientplanner.ui.screens.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dsankovsky.kmpclientplanner.navigation.Screen
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientEvent
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientScreen
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceEvent
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceScreen
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsEvents
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsScreen
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsListScreen
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsListScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.screens.main.empty.NoClientsScreen
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServicesScreen
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreen
import com.dsankovsky.kmpclientplanner.ui.screens.service_type_selection.ServiceTypeSelectionScreen
import com.dsankovsky.kmpclientplanner.ui.screens.services.HomeScreen
import com.dsankovsky.kmpclientplanner.ui.screens.services_history.ServicesHistoryScreen
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsScreen
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.StatisticsScreen
import com.dsankovsky.kmpclientplanner.ui.screens.welcome.WelcomeScreen
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServiceScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.services.HomeScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.services_history.ServicesHistoryScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsScreenEvent
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.add_edit_client_created
import kmpclientplanner.sharedui.generated.resources.add_edit_client_deleted
import kmpclientplanner.sharedui.generated.resources.add_edit_client_updated
import kmpclientplanner.sharedui.generated.resources.add_edit_service_created
import kmpclientplanner.sharedui.generated.resources.add_edit_service_deleted
import kmpclientplanner.sharedui.generated.resources.add_edit_service_updated
import kmpclientplanner.sharedui.generated.resources.client_details_autofill_completed
import kmpclientplanner.sharedui.generated.resources.client_details_data_updated
import kmpclientplanner.sharedui.generated.resources.client_details_status_updated
import kmpclientplanner.sharedui.generated.resources.services_paid
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {
    val viewModel: MainScreenViewModel = koinViewModel()
    val backStack = remember { mutableStateListOf<Screen>(Screen.LoadingScreen) }
    val screensWithNavBar = listOf(
        Screen.ClientsScreen,
        Screen.HomeScreen,
        Screen.StatisticsScreen,
        Screen.SettingsScreen
    )

    val showNavigationBar = backStack.lastOrNull() in screensWithNavBar

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    data,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (backStack.lastOrNull() in screensWithNavBar) 100.dp else 0.dp)
                )
            }
        }
    ) { innerPadding ->
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

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            NavDisplay(
                backStack = backStack,
                modifier = Modifier.padding(innerPadding),
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<Screen.WelcomeScreen> {
                        WelcomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            onStartClick = {
                                backStack.add(Screen.ServiceTypeSelectionScreen)
                            }
                        )
                    }

                    entry<Screen.LoadingScreen> {
                        LoadingScreen()
                    }

                    entry<Screen.ServiceTypeSelectionScreen>(
                        metadata = transitionHorizontalSlideAnimation()
                    ) {
                        ServiceTypeSelectionScreen(
                            onServiceTypeClicked = {
                                viewModel.handleActions(
                                    MainScreenActions.OnServiceTypeSelected(
                                        it
                                    )
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
                                    ClientDetailsEvents.OpenEditClientScreen -> backStack.add(
                                        Screen.AddEditClientScreen(
                                            it.clientId
                                        )
                                    )

                                    ClientDetailsEvents.ClientsDataUpdated -> {
                                        scope.launch {
                                            val message =
                                                getString(Res.string.client_details_data_updated)

                                            snackbarHostState.showSnackbar(
                                                message = message,
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                        backStack.removeLastOrNull()
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

                    entry<Screen.AddEditClientScreen>(
                        metadata = transitionHorizontalSlideAnimation()
                    ) {
                        AddEditClientScreen(
                            onEvent = { event ->
                                when (event) {
                                    AddEditClientEvent.OnClientSaved -> {
                                        scope.launch {
                                            val message = if (it.clientId != null) {
                                                getString(Res.string.add_edit_client_updated)
                                            } else {
                                                getString(Res.string.add_edit_client_created)
                                            }

                                            snackbarHostState.showSnackbar(
                                                message = message,
                                                duration = SnackbarDuration.Short
                                            )
                                        }

                                        if (backStack.any { it::class == Screen.ClientDetailsScreen::class }) {
                                            backStack.removeLastOrNull()
                                        } else {
                                            backStack.clear()
                                            backStack.add(Screen.ClientsScreen)
                                        }
                                    }

                                    AddEditClientEvent.OnDismissClicked -> {
                                        backStack.removeLastOrNull()
                                    }

                                    is AddEditClientEvent.OnClientDeleted -> {
                                        scope.launch {
                                            val message =
                                                getString(Res.string.add_edit_client_deleted)
                                            snackbarHostState.showSnackbar(
                                                message = message,
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                        if (event.noClients) {
                                            viewModel.handleActions(MainScreenActions.GetStartDestination)
                                        } else {
                                            backStack.clear()
                                            backStack.add(Screen.ClientsScreen)
                                        }
                                    }

                                    AddEditClientEvent.AutofillCompleted -> {
                                        scope.launch {
                                            val message =
                                                getString(Res.string.client_details_autofill_completed)
                                            snackbarHostState.showSnackbar(
                                                message = message,
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
                            },
                            clientId = it.clientId
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
                                    ServiceDetailsScreenEvent.OpenEditServiceScreen -> backStack.add(
                                        Screen.AddEditServiceScreen(serviceId)
                                    )

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

                    entry<Screen.AddEditServiceScreen>(
                        metadata = transitionHorizontalSlideAnimation()
                    ) {
                        val serviceId = it.serviceId
                        AddEditServiceScreen(
                            onEvent = { event ->
                                when (event) {
                                    AddEditServiceEvent.OnDismissClicked -> {
                                        backStack.removeLastOrNull()
                                    }

                                    AddEditServiceEvent.OnServiceDeleted -> {
                                        backStack.clear()
                                        backStack.add(Screen.ClientsScreen)
                                        scope.launch {
                                            val message =
                                                getString(Res.string.add_edit_service_deleted)
                                            snackbarHostState.showSnackbar(
                                                message = message,
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }

                                    AddEditServiceEvent.OnServiceSaved -> {
                                        scope.launch {
                                            val message = if (it.serviceId != null) {
                                                getString(Res.string.add_edit_service_updated)
                                            } else {
                                                getString(Res.string.add_edit_service_created)
                                            }
                                            snackbarHostState.showSnackbar(
                                                message = message,
                                                duration = SnackbarDuration.Short
                                            )
                                        }

                                        if (backStack.any { it::class == Screen.ServiceDetailsScreen::class }) {
                                            backStack.removeLastOrNull()
                                        } else {
                                            backStack.clear()
                                            backStack.add(Screen.HomeScreen)
                                        }
                                    }
                                }
                            },
                            serviceId = serviceId
                        )
                    }

                    entry<Screen.HomeScreen> {
                        HomeScreen(
                            onEvent = { event ->
                                when (event) {
                                    is HomeScreenEvent.OpenServiceInfo -> {
                                        backStack.add(Screen.ServiceDetailsScreen(event.serviceId))
                                    }

                                    HomeScreenEvent.ServiceDeleted -> {
                                        scope.launch {
                                            val message =
                                                getString(Res.string.add_edit_service_deleted)
                                            snackbarHostState.showSnackbar(
                                                message = message,
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }

                                    HomeScreenEvent.StatusUpdated -> {
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
                                backStack.add(Screen.AddEditClientScreen())
                            },
                            onChangeServiceTypeCLicked = {
                                backStack.add(Screen.ServiceTypeSelectionScreen)
                            }
                        )
                    }

                    entry<Screen.ClientsScreen> {
                        ClientsListScreen(
                            onEvent = { event ->
                                when (event) {
                                    is ClientsListScreenEvent.OpenClientInfo -> {
                                        backStack.add(Screen.ClientDetailsScreen(event.clientId))
                                    }
                                }
                            }
                        )
                    }

                    entry<Screen.StatisticsScreen> {
                        StatisticsScreen()
                    }

                    entry<Screen.PayServicesScreen>(
                        metadata = transitionHorizontalSlideAnimation()
                    ) {
                        PayServicesScreen(
                            onEvent = {
                                when (it) {
                                    PayServiceScreenEvent.OnDismissClicked -> {
                                        backStack.removeLastOrNull()
                                    }

                                    PayServiceScreenEvent.OnSuccess -> {
                                        scope.launch {
                                            val message = getString(Res.string.services_paid)
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

                    entry<Screen.SettingsScreen> {
                        SettingsScreen(
                            onEvent = { event ->
                                when (event) {
                                    SettingsScreenEvent.AllDataCleared -> {
                                        backStack.clear()
                                        backStack.add(Screen.WelcomeScreen)
                                    }
                                }
                            }
                        )
                    }
                }
            )
        }
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