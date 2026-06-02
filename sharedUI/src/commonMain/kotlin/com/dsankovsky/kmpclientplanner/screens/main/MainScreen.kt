package com.dsankovsky.kmpclientplanner.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.screens.settings.SettingsScreen

private data class NavTab(val label: String, val icon: ImageVector)

private val tabs = listOf(
    NavTab("Главная", Icons.Default.Home),
    NavTab("Клиенты", Icons.Default.Person),
    NavTab("Статистика", Icons.Default.DateRange),
    NavTab("Настройки", Icons.Default.Settings),
)

@Composable
fun MainScreen(onNavigateToWelcome: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompact = maxWidth < 600.dp

        if (isCompact) {
            CompactLayout(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateToWelcome = onNavigateToWelcome
            )
        } else {
            ExpandedLayout(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateToWelcome = onNavigateToWelcome
            )
        }
    }
}

@Composable
private fun CompactLayout(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateToWelcome: () -> Unit
) {
    Scaffold(
        topBar = {
            if (selectedTab != 3) {
                TopAppBar(title = { Text(tabs[selectedTab].label) })
            }
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { onTabSelected(index) },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabContent(
                selectedTab = selectedTab,
                onNavigateToWelcome = onNavigateToWelcome
            )
        }
    }
}

@Composable
private fun ExpandedLayout(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateToWelcome: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail {
            Spacer(modifier = Modifier.height(16.dp))
            tabs.forEachIndexed { index, tab ->
                NavigationRailItem(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    icon = { Icon(tab.icon, contentDescription = tab.label) },
                    label = { Text(tab.label) }
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            if (selectedTab != 3) {
                TopAppBar(title = { Text(tabs[selectedTab].label) })
            }
            Box(modifier = Modifier.fillMaxSize()) {
                TabContent(
                    selectedTab = selectedTab,
                    onNavigateToWelcome = onNavigateToWelcome
                )
            }
        }
    }
}

@Composable
private fun TabContent(
    selectedTab: Int,
    onNavigateToWelcome: () -> Unit
) {
    when (selectedTab) {
        0 -> StubScreen(label = "Главная")
        1 -> StubScreen(label = "Клиенты")
        2 -> StubScreen(label = "Статистика")
        3 -> SettingsScreen(onNavigateToWelcome = onNavigateToWelcome)
    }
}

@Composable
private fun StubScreen(label: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
