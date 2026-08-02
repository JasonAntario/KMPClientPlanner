package com.dsankovsky.kmpclientplanner.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.nav_bar_clients
import kmpclientplanner.sharedui.generated.resources.nav_bar_main
import kmpclientplanner.sharedui.generated.resources.nav_bar_settings
import kmpclientplanner.sharedui.generated.resources.nav_bar_statistycs
import org.jetbrains.compose.resources.StringResource

sealed class NavigationItem(
    val icon: ImageVector,
    val screen: Screen,
    val title: StringResource
) {
    data object Home :
        NavigationItem(
            icon = OrganicIcons.Home,
            screen = Screen.HomeScreen,
            title = Res.string.nav_bar_main
        )

    data object ClientsList :
        NavigationItem(
            icon = OrganicIcons.Users,
            screen = Screen.ClientsScreen,
            title = Res.string.nav_bar_clients
        )

    data object Statistics :
        NavigationItem(
            icon = OrganicIcons.BarChart,
            screen = Screen.StatisticsScreen,
            title = Res.string.nav_bar_statistycs
        )

    data object Settings :
        NavigationItem(
            icon = OrganicIcons.Settings,
            screen = Screen.SettingsScreen,
            title = Res.string.nav_bar_settings
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
