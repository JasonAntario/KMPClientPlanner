package com.dsankovsky.kmpclientplanner

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.dsankovsky.kmpclientplanner.di.platformModule
import com.dsankovsky.kmpclientplanner.di.repositoryModule
import com.dsankovsky.kmpclientplanner.di.uiModule
import com.dsankovsky.kmpclientplanner.di.useCasesModule
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(platformModule, useCasesModule, repositoryModule, uiModule)
    }
    application {
        Window(
            state = rememberWindowState(placement = WindowPlacement.Maximized),
            onCloseRequest = ::exitApplication,
            title = "KMPClientPlanner",
        ) {
            App()
        }
    }
}
