package com.dsankovsky.kmpclientplanner.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import com.dsankovsky.kmpclientplanner.screens.main.MainScreen
import com.dsankovsky.kmpclientplanner.screens.welcome.WelcomeScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val appSettings: AppSettings = koinInject()
    var startRoute by remember { mutableStateOf<Any?>(null) }

    LaunchedEffect(Unit) {
        startRoute = if (appSettings.getServiceType() != null) MainRoute else WelcomeRoute
    }

    startRoute?.let { route ->
        val backStack = rememberNavBackStack(route)

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<WelcomeRoute> {
                    WelcomeScreen(
                        onContinue = {
                            backStack.removeLastOrNull()
                            backStack.add(MainRoute)
                        }
                    )
                }
                entry<MainRoute> {
                    MainScreen(
                        onNavigateToWelcome = {
                            backStack.clear()
                            backStack.add(WelcomeRoute)
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
