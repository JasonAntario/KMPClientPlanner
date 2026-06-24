package com.dsankovsky.kmpclientplanner.uinew.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.navigation.Screen
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.screens.main.MainScreenActions
import com.dsankovsky.kmpclientplanner.ui.screens.main.MainScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.main.MainScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsScreenAction
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsViewModel
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.NavRail
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.NavSection
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.RailAction
import com.dsankovsky.kmpclientplanner.uinew.desktop.modals.AddEditClientModal
import com.dsankovsky.kmpclientplanner.uinew.desktop.modals.AddEditServiceModal
import com.dsankovsky.kmpclientplanner.uinew.desktop.modals.PrepayModal
import com.dsankovsky.kmpclientplanner.uinew.desktop.screens.DesktopClientsScreen
import com.dsankovsky.kmpclientplanner.uinew.desktop.screens.DesktopHomeScreen
import com.dsankovsky.kmpclientplanner.uinew.desktop.screens.DesktopSettingsScreen
import com.dsankovsky.kmpclientplanner.uinew.desktop.screens.DesktopStatisticsScreen
import com.dsankovsky.kmpclientplanner.uinew.desktop.screens.ServiceTypeSelectionScreen
import com.dsankovsky.kmpclientplanner.uinew.desktop.screens.WelcomeScreen
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import org.koin.compose.viewmodel.koinViewModel

sealed interface DesktopModal {
    data class AddEditService(val serviceId: Long?) : DesktopModal
    data class AddEditClient(val clientId: Long?) : DesktopModal
    data object Prepay : DesktopModal
}

@Composable
fun DesktopApp() {
    val mainViewModel: MainScreenViewModel = koinViewModel()
    var startScreen by remember { mutableStateOf<Screen?>(null) }

    mainViewModel.event.collectWithLifecycle { event ->
        when (event) {
            is MainScreenEvent.Navigate -> startScreen = event.screen
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.handleActions(MainScreenActions.GetStartDestination)
    }

    when (startScreen) {
        null -> LoadingBox()

        Screen.WelcomeScreen -> OnboardingFlow(
            onServiceTypeSelected = { type ->
                mainViewModel.handleActions(MainScreenActions.OnServiceTypeSelected(type))
            }
        )

        else -> MainShell(
            onDataCleared = {
                // Wiping all data clears the saved service type, so re-resolving the
                // start destination sends the user back to the welcome/onboarding flow.
                mainViewModel.handleActions(MainScreenActions.GetStartDestination)
            }
        )
    }
}

/**
 * Two-step onboarding: the greeting [WelcomeScreen] followed by the
 * [ServiceTypeSelectionScreen] profession picker. Selecting a type is reported
 * upward so the host can persist it and recompute the start destination.
 */
@Composable
private fun OnboardingFlow(onServiceTypeSelected: (ServiceType) -> Unit) {
    var showServiceTypes by remember { mutableStateOf(false) }

    if (showServiceTypes) {
        ServiceTypeSelectionScreen(
            onServiceTypeSelected = onServiceTypeSelected,
            onBack = { showServiceTypes = false },
        )
    } else {
        WelcomeScreen(onStart = { showServiceTypes = true })
    }
}

@Composable
private fun MainShell(onDataCleared: () -> Unit) {
    // SettingsViewModel exposes the selected profession (service type) for the rail header.
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { settingsViewModel.handleActions(SettingsScreenAction.LoadData) }
    val professionLabel = settingsState.serviceType.toUIName()

    var section by remember { mutableStateOf(NavSection.Home) }
    var modal by remember { mutableStateOf<DesktopModal?>(null) }
    var refreshKey by remember { mutableStateOf(0) }

    val railAction: RailAction? = when (section) {
        NavSection.Home -> RailAction("Добавить занятие", Icons.Filled.Add) {
            modal = DesktopModal.AddEditService(null)
        }
        NavSection.Clients -> RailAction("Добавить клиента", Icons.Filled.Add) {
            modal = DesktopModal.AddEditClient(null)
        }
        NavSection.Statistics -> RailAction("Предоплатить", Icons.Filled.AccountBalanceWallet) {
            modal = DesktopModal.Prepay
        }
        NavSection.Settings -> null
    }

    val rail: @Composable () -> Unit = {
        NavRail(
            selected = section,
            onSelect = { section = it },
            professionLabel = professionLabel,
            action = railAction,
        )
    }

    Box(Modifier.fillMaxSize()) {
        key(refreshKey) {
            when (section) {
                NavSection.Home -> DesktopHomeScreen(
                    rail = rail,
                    onAddService = { modal = DesktopModal.AddEditService(null) },
                    onEditService = { id -> modal = DesktopModal.AddEditService(id) },
                )

                NavSection.Clients -> DesktopClientsScreen(
                    rail = rail,
                    onAddClient = { modal = DesktopModal.AddEditClient(null) },
                    onEditClient = { id -> modal = DesktopModal.AddEditClient(id) },
                )

                NavSection.Statistics -> DesktopStatisticsScreen(rail = rail)

                NavSection.Settings -> DesktopSettingsScreen(
                    rail = rail,
                    onDataCleared = onDataCleared,
                )
            }
        }

        when (val m = modal) {
            is DesktopModal.AddEditService -> AddEditServiceModal(
                serviceId = m.serviceId,
                onDismiss = { modal = null },
                onSaved = { modal = null; refreshKey++ },
            )

            is DesktopModal.AddEditClient -> AddEditClientModal(
                clientId = m.clientId,
                onDismiss = { modal = null },
                onSaved = { modal = null; refreshKey++ },
            )

            DesktopModal.Prepay -> PrepayModal(
                onDismiss = { modal = null },
                onSuccess = { modal = null; refreshKey++ },
            )

            null -> Unit
        }
    }
}

@Composable
private fun LoadingBox() {
    Box(
        modifier = Modifier.fillMaxSize().background(LessonsColors.PageBackground),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = LessonsColors.Primary)
    }
}
