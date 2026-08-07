package com.dsankovsky.kmpclientplanner.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.dsankovsky.kmpclientplanner.navigation.NavigationItem
import com.dsankovsky.kmpclientplanner.navigation.Screen
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationBar
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationBarItem
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationRail
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationRailItem
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

/**
 * Ширина окна, с которой навигация переезжает вбок.
 *
 * Порог берём у Adaptive от Google, а не «на глаз»: medium (600 dp) и шире — боковая
 * навигация, уже — нижняя панель. Раньше здесь было зашито `maxWidth >= 600.dp`
 * в `BoxWithConstraints`; численно то же самое, но теперь это одна точка правды
 * с остальными адаптивными решениями (двухпанельные экраны появятся на этапе 6).
 */
@Composable
fun isWideWindow(): Boolean =
    currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)

/**
 * Каркас приложения: навигация (рейл или нижняя панель) + хост снекбара вокруг контента.
 *
 * `Scaffold` из M3 остаётся ради снекбара и оконных вставок; сама навигация — своя,
 * из DS ([OrganicNavigationRail] / [OrganicNavigationBar]).
 *
 * @param wideWindow результат [isWideWindow]; передаётся снаружи, потому что тот же флаг
 *   нужен экранам (пока у них свои FAB'ы)
 * @param showNavigation навигация видна не везде — на форме, деталях и приветствии её нет
 * @param railFooter нижняя строка рейла, «<категория> · <версия>» из макета
 * @param modal модальный слой поверх всего окна, включая рейл (см. [ModalState]);
 *   `null` — модалки нет
 */
@Composable
fun AppScaffold(
    wideWindow: Boolean,
    showNavigation: Boolean,
    currentScreen: Screen?,
    onNavigate: (Screen) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    railFooter: String? = null,
    modal: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(modifier.fillMaxSize()) {
        AppFrame(
            wideWindow = wideWindow,
            showNavigation = showNavigation,
            currentScreen = currentScreen,
            onNavigate = onNavigate,
            snackbarHostState = snackbarHostState,
            railFooter = railFooter,
            content = content,
        )
        modal?.invoke()
    }
}

@Composable
private fun AppFrame(
    wideWindow: Boolean,
    showNavigation: Boolean,
    currentScreen: Screen?,
    onNavigate: (Screen) -> Unit,
    snackbarHostState: SnackbarHostState,
    railFooter: String?,
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!wideWindow && showNavigation) {
                OrganicNavigationBar {
                    NavigationItem.items.forEach { item ->
                        OrganicNavigationBarItem(
                            icon = item.icon,
                            label = stringResource(item.title),
                            selected = item.screen == currentScreen,
                            onClick = { onNavigate(item.screen) },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        Row(modifier = Modifier.padding(paddingValues)) {
            if (wideWindow && showNavigation) {
                OrganicNavigationRail(
                    brand = stringResource(Res.string.app_name),
                    footer = railFooter,
                ) {
                    NavigationItem.items.forEach { item ->
                        OrganicNavigationRailItem(
                            icon = item.icon,
                            label = stringResource(item.title),
                            selected = item.screen == currentScreen,
                            onClick = { onNavigate(item.screen) },
                        )
                    }
                }
            }
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                content()
            }
        }
    }
}
