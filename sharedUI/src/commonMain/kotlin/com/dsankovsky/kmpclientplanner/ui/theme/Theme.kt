package com.dsankovsky.kmpclientplanner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.dsankovsky.clientmanagement.ui.theme.backgroundDark
import com.dsankovsky.clientmanagement.ui.theme.backgroundLight
import com.dsankovsky.clientmanagement.ui.theme.errorContainerDark
import com.dsankovsky.clientmanagement.ui.theme.errorContainerLight
import com.dsankovsky.clientmanagement.ui.theme.errorDark
import com.dsankovsky.clientmanagement.ui.theme.errorLight
import com.dsankovsky.clientmanagement.ui.theme.inverseOnSurfaceDark
import com.dsankovsky.clientmanagement.ui.theme.inverseOnSurfaceLight
import com.dsankovsky.clientmanagement.ui.theme.inversePrimaryDark
import com.dsankovsky.clientmanagement.ui.theme.inversePrimaryLight
import com.dsankovsky.clientmanagement.ui.theme.inverseSurfaceDark
import com.dsankovsky.clientmanagement.ui.theme.inverseSurfaceLight
import com.dsankovsky.clientmanagement.ui.theme.onBackgroundDark
import com.dsankovsky.clientmanagement.ui.theme.onBackgroundLight
import com.dsankovsky.clientmanagement.ui.theme.onErrorContainerDark
import com.dsankovsky.clientmanagement.ui.theme.onErrorContainerLight
import com.dsankovsky.clientmanagement.ui.theme.onErrorDark
import com.dsankovsky.clientmanagement.ui.theme.onErrorLight
import com.dsankovsky.clientmanagement.ui.theme.onPrimaryContainerDark
import com.dsankovsky.clientmanagement.ui.theme.onPrimaryContainerLight
import com.dsankovsky.clientmanagement.ui.theme.onPrimaryDark
import com.dsankovsky.clientmanagement.ui.theme.onPrimaryLight
import com.dsankovsky.clientmanagement.ui.theme.onSecondaryContainerDark
import com.dsankovsky.clientmanagement.ui.theme.onSecondaryContainerLight
import com.dsankovsky.clientmanagement.ui.theme.onSecondaryDark
import com.dsankovsky.clientmanagement.ui.theme.onSecondaryLight
import com.dsankovsky.clientmanagement.ui.theme.onSurfaceDark
import com.dsankovsky.clientmanagement.ui.theme.onSurfaceLight
import com.dsankovsky.clientmanagement.ui.theme.onSurfaceVariantDark
import com.dsankovsky.clientmanagement.ui.theme.onSurfaceVariantLight
import com.dsankovsky.clientmanagement.ui.theme.onTertiaryContainerDark
import com.dsankovsky.clientmanagement.ui.theme.onTertiaryContainerLight
import com.dsankovsky.clientmanagement.ui.theme.onTertiaryDark
import com.dsankovsky.clientmanagement.ui.theme.onTertiaryLight
import com.dsankovsky.clientmanagement.ui.theme.outlineDark
import com.dsankovsky.clientmanagement.ui.theme.outlineLight
import com.dsankovsky.clientmanagement.ui.theme.outlineVariantDark
import com.dsankovsky.clientmanagement.ui.theme.outlineVariantLight
import com.dsankovsky.clientmanagement.ui.theme.primaryContainerDark
import com.dsankovsky.clientmanagement.ui.theme.primaryContainerLight
import com.dsankovsky.clientmanagement.ui.theme.primaryDark
import com.dsankovsky.clientmanagement.ui.theme.primaryLight
import com.dsankovsky.clientmanagement.ui.theme.scrimDark
import com.dsankovsky.clientmanagement.ui.theme.scrimLight
import com.dsankovsky.clientmanagement.ui.theme.secondaryContainerDark
import com.dsankovsky.clientmanagement.ui.theme.secondaryContainerLight
import com.dsankovsky.clientmanagement.ui.theme.secondaryDark
import com.dsankovsky.clientmanagement.ui.theme.secondaryLight
import com.dsankovsky.clientmanagement.ui.theme.surfaceBrightDark
import com.dsankovsky.clientmanagement.ui.theme.surfaceBrightLight
import com.dsankovsky.clientmanagement.ui.theme.surfaceContainerDark
import com.dsankovsky.clientmanagement.ui.theme.surfaceContainerHighDark
import com.dsankovsky.clientmanagement.ui.theme.surfaceContainerHighLight
import com.dsankovsky.clientmanagement.ui.theme.surfaceContainerHighestDark
import com.dsankovsky.clientmanagement.ui.theme.surfaceContainerHighestLight
import com.dsankovsky.clientmanagement.ui.theme.surfaceContainerLight
import com.dsankovsky.clientmanagement.ui.theme.surfaceContainerLowDark
import com.dsankovsky.clientmanagement.ui.theme.surfaceContainerLowLight
import com.dsankovsky.clientmanagement.ui.theme.surfaceContainerLowestDark
import com.dsankovsky.clientmanagement.ui.theme.surfaceContainerLowestLight
import com.dsankovsky.clientmanagement.ui.theme.surfaceDark
import com.dsankovsky.clientmanagement.ui.theme.surfaceDimDark
import com.dsankovsky.clientmanagement.ui.theme.surfaceDimLight
import com.dsankovsky.clientmanagement.ui.theme.surfaceLight
import com.dsankovsky.clientmanagement.ui.theme.surfaceVariantDark
import com.dsankovsky.clientmanagement.ui.theme.surfaceVariantLight
import com.dsankovsky.clientmanagement.ui.theme.tertiaryContainerDark
import com.dsankovsky.clientmanagement.ui.theme.tertiaryContainerLight
import com.dsankovsky.clientmanagement.ui.theme.tertiaryDark
import com.dsankovsky.clientmanagement.ui.theme.tertiaryLight

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun ClientPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
