package com.dsankovsky.kmpclientplanner

import androidx.compose.runtime.Composable
import com.dsankovsky.kmpclientplanner.ui.screens.main.MainScreen
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme

@Composable
fun App() {
    ClientPlannerTheme {
        MainScreen()
    }
}
