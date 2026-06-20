package com.dsankovsky.kmpclientplanner.uinew.desktop.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private fun lessonsColorScheme() = lightColorScheme(
    primary = LessonsColors.Primary,
    onPrimary = LessonsColors.CardBackground,
    primaryContainer = LessonsColors.PrimaryTint,
    onPrimaryContainer = LessonsColors.Primary,
    secondary = LessonsColors.TextSecondary,
    onSecondary = LessonsColors.CardBackground,
    secondaryContainer = LessonsColors.PrimaryTint,
    onSecondaryContainer = LessonsColors.Primary,
    tertiary = LessonsColors.Success,
    onTertiary = LessonsColors.CardBackground,
    tertiaryContainer = LessonsColors.SuccessTint,
    onTertiaryContainer = LessonsColors.Success,
    background = LessonsColors.PageBackground,
    onBackground = LessonsColors.TextPrimary,
    surface = LessonsColors.CardBackground,
    onSurface = LessonsColors.TextPrimary,
    surfaceVariant = LessonsColors.PanelBackground,
    onSurfaceVariant = LessonsColors.TextSecondary,
    outline = LessonsColors.Border,
    outlineVariant = LessonsColors.BorderSoft,
    error = LessonsColors.Danger,
    onError = LessonsColors.CardBackground,
    errorContainer = LessonsColors.DangerTint,
    onErrorContainer = LessonsColors.Danger,
)

/**
 * Standalone theme for the new desktop UI. Independent of [com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme]
 * so the existing UI is untouched.
 */
@Composable
fun LessonsDesktopTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lessonsColorScheme(),
        typography = lessonsTypography(),
        content = content,
    )
}
