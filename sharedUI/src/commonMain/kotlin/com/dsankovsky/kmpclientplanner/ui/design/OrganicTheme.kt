package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalOrganicColors: ProvidableCompositionLocal<OrganicColors> =
    staticCompositionLocalOf { OrganicColors() }

val LocalOrganicSpacing: ProvidableCompositionLocal<OrganicSpacing> =
    staticCompositionLocalOf { OrganicSpacing() }

val LocalOrganicShapes: ProvidableCompositionLocal<OrganicShapes> =
    staticCompositionLocalOf { OrganicShapes() }

val LocalOrganicElevation: ProvidableCompositionLocal<OrganicElevation> =
    staticCompositionLocalOf { OrganicElevation() }

val LocalOrganicTypography: ProvidableCompositionLocal<OrganicTypography> =
    staticCompositionLocalOf { organicTypography() }

/** Доступ к токенам: `OrganicTheme.colors.accent`, `OrganicTheme.spacing.space3` и т.д. */
object OrganicTheme {
    val colors: OrganicColors
        @Composable @ReadOnlyComposable get() = LocalOrganicColors.current

    val spacing: OrganicSpacing
        @Composable @ReadOnlyComposable get() = LocalOrganicSpacing.current

    val shapes: OrganicShapes
        @Composable @ReadOnlyComposable get() = LocalOrganicShapes.current

    val elevation: OrganicElevation
        @Composable @ReadOnlyComposable get() = LocalOrganicElevation.current

    val typography: OrganicTypography
        @Composable @ReadOnlyComposable get() = LocalOrganicTypography.current
}

/**
 * Корневая тема приложения.
 *
 * Тема тёмная не поддерживается: Organic — светлая система (кремовая земля, тёплые тени),
 * тёмных токенов в хендоффе нет.
 *
 * Пока миграция не закончена, внутрь дополнительно ставится [MaterialTheme] со схемой,
 * смапленной из Organic-токенов, — чтобы ещё не переписанные экраны и сервисные M3-виджеты
 * (DatePicker/TimePicker, Snackbar) не выглядели чужеродно. После переписывания экранов
 * маппинг останется нужен только пикерам.
 */
@Composable
fun OrganicTheme(
    colors: OrganicColors = OrganicColors(),
    spacing: OrganicSpacing = OrganicSpacing(),
    shapes: OrganicShapes = OrganicShapes(),
    elevation: OrganicElevation = OrganicElevation(),
    typography: OrganicTypography = LocalOrganicTypography.current,
    content: @Composable () -> Unit,
) {
    val selectionColors = remember(colors) {
        TextSelectionColors(handleColor = colors.accent, backgroundColor = colors.selection)
    }
    val materialScheme = remember(colors) { colors.toMaterialColorScheme() }

    CompositionLocalProvider(
        LocalOrganicColors provides colors,
        LocalOrganicSpacing provides spacing,
        LocalOrganicShapes provides shapes,
        LocalOrganicElevation provides elevation,
        LocalOrganicTypography provides typography,
        LocalContentColor provides colors.text,
        LocalTextSelectionColors provides selectionColors,
        LocalIndication provides OrganicIndication,
    ) {
        MaterialTheme(colorScheme = materialScheme) {
            // MaterialTheme перекрывает LocalContentColor/LocalTextStyle своими значениями,
            // поэтому Organic-дефолты ставим уже внутри него.
            CompositionLocalProvider(
                LocalContentColor provides colors.text,
                LocalTextStyle provides typography.body,
                content = content,
            )
        }
    }
}

/**
 * Маппинг Organic → M3 `ColorScheme`. Роли M3 не выражают систему (две девятишаговые
 * рампы, статусные тройки, альфа-производные), поэтому это переходная совместимость,
 * а не источник истины.
 */
private fun OrganicColors.toMaterialColorScheme() = lightColorScheme(
    primary = accent,
    onPrimary = bg,
    primaryContainer = accentRamp.s200,
    onPrimaryContainer = accentRamp.s800,
    inversePrimary = accentRamp.s300,
    secondary = accent2,
    onSecondary = bg,
    secondaryContainer = accent2Ramp.s200,
    onSecondaryContainer = accent2Ramp.s800,
    tertiary = accent2Ramp.s600,
    onTertiary = bg,
    tertiaryContainer = accent2Ramp.s100,
    onTertiaryContainer = accent2Ramp.s900,
    background = bg,
    onBackground = text,
    surface = bg,
    onSurface = text,
    surfaceVariant = surface,
    onSurfaceVariant = neutralRamp.s700,
    surfaceTint = accent,
    surfaceDim = neutralRamp.s300,
    surfaceBright = neutralRamp.s100,
    surfaceContainerLowest = neutralRamp.s100,
    surfaceContainerLow = bg,
    surfaceContainer = surface,
    surfaceContainerHigh = neutralRamp.s300,
    surfaceContainerHighest = neutralRamp.s400,
    inverseSurface = neutralRamp.s900,
    inverseOnSurface = bg,
    outline = neutralRamp.s500,
    outlineVariant = neutralRamp.s300,
    scrim = neutralRamp.s900,
    error = accentRamp.s700,
    onError = bg,
    errorContainer = accentRamp.s200,
    onErrorContainer = accentRamp.s900,
)
