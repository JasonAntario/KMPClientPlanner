package com.dsankovsky.kmpclientplanner.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.nav_bar_clients
import kmpclientplanner.sharedui.generated.resources.nav_bar_main
import kmpclientplanner.sharedui.generated.resources.nav_bar_statistycs
import org.jetbrains.compose.resources.StringResource

sealed class NavigationItem(
    val icon: ImageVector,
    val screen: Screen,
    val title: StringResource
) {
    data object Home :
        NavigationItem(
            icon = Icons.Default.Home,
            screen = Screen.HomeScreen,
            title = Res.string.nav_bar_main
        )

    data object ClientsList :
        NavigationItem(
            icon = Icons.Default.PeopleAlt,
            screen = Screen.ClientsScreen,
            title = Res.string.nav_bar_clients
        )

    data object Statistics :
        NavigationItem(
            icon = Icons.Default.BarChart,
            screen = Screen.StatisticsScreen,
            title = Res.string.nav_bar_statistycs
        )

    data object Settings :
        NavigationItem(
            icon = Icons.Default.Settings,
            screen = Screen.SettingsScreen,
            title = Res.string.nav_bar_statistycs
        )

    companion object {
        val items = listOf(
            Home,
            ClientsList,
            Statistics,
            Settings
        )
    }
}