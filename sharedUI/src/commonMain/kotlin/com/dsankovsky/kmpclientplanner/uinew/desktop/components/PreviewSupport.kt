package com.dsankovsky.kmpclientplanner.uinew.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsDesktopTheme

/**
 * Shared wrapper for the `@Preview` functions in this package: applies
 * [LessonsDesktopTheme] and a padded design-canvas background so each component
 * is rendered with its real tokens (fonts, colors) in the IDE preview pane.
 */
@Composable
internal fun ComponentPreview(
    padding: Dp = 20.dp,
    background: Color = LessonsColors.PageBackground,
    content: @Composable () -> Unit,
) {
    LessonsDesktopTheme {
        Box(Modifier.background(background).padding(padding)) {
            content()
        }
    }
}
