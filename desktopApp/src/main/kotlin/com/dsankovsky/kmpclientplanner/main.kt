package com.dsankovsky.kmpclientplanner

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.dsankovsky.kmpclientplanner.di.platformModule
import com.dsankovsky.kmpclientplanner.di.repositoryModule
import com.dsankovsky.kmpclientplanner.di.uiModule
import com.dsankovsky.kmpclientplanner.di.useCasesModule
import org.koin.core.context.startKoin
import java.awt.Dimension

fun main() {
    startKoin {
        modules(platformModule, useCasesModule, repositoryModule, uiModule)
    }
    application {
        // Макет свёрстан под окно 1360; минимум — ширина, при которой рейл 248
        // и двухпанельные экраны ещё помещаются (expanded по гайдлайну Adaptive).
        val windowState = rememberWindowState(width = 1360.dp, height = 900.dp)
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "Client Planner",
        ) {
            LaunchedEffect(Unit) { window.minimumSize = Dimension(940, 640) }
            App()
        }
    }
}
