package com.dsankovsky.kmpclientplanner

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.dsankovsky.kmpclientplanner.di.platformModule
import com.dsankovsky.kmpclientplanner.di.repositoryModule
import com.dsankovsky.kmpclientplanner.di.uiModule
import com.dsankovsky.kmpclientplanner.di.useCasesModule
import com.dsankovsky.kmpclientplanner.uinew.desktop.DesktopApp
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsDesktopTheme
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(platformModule, useCasesModule, repositoryModule, uiModule)
    }
    application {
        val windowState = rememberWindowState(width = 1180.dp, height = 760.dp)
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "Lessons",
        ) {
            LessonsDesktopTheme {
                DesktopApp()
            }
        }
    }
}
