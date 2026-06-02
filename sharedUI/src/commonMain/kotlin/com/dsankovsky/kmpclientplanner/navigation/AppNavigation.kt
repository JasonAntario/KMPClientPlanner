package com.dsankovsky.kmpclientplanner.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dsankovsky.kmpclientplanner.screens.main.MainScreen
import com.dsankovsky.kmpclientplanner.screens.welcome.WelcomeScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation() {
    val viewModel: AppNavigationViewModel = koinViewModel()
    val startRoute by viewModel.startRoute.collectAsStateWithLifecycle()

    startRoute?.let { route ->
        val backStack = remember { mutableStateListOf(route) }

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<AppRoutes.WelcomeRoute> {
                    WelcomeScreen(
                        onContinue = {
                            backStack.removeLastOrNull()
                            backStack.add(AppRoutes.MainRoute)
                        }
                    )
                }
                entry<AppRoutes.MainRoute> {
                    MainScreen(
                        onNavigateToWelcome = {
                            backStack.clear()
                            backStack.add(AppRoutes.WelcomeRoute)
                        }
                    )
                }
            }
        )
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
