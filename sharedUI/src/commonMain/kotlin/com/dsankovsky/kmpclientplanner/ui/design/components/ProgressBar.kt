package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import kotlin.math.roundToInt
import androidx.compose.ui.tooling.preview.Preview

/**
 * Полоса прогресса с экрана статистики: высота 10, фон neutral-300,
 * заполнение accent, оба конца pill.
 */
@Composable
fun OrganicProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    track: Color = OrganicTheme.colors.neutralRamp.s300,
    fill: Color = OrganicTheme.colors.accent,
) {
    val shape = OrganicTheme.shapes.pill
    val clamped = progress.coerceIn(0f, 1f)
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .background(track, shape),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .layout { measurable, constraints ->
                    val width = (constraints.maxWidth * clamped).roundToInt()
                    val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
                    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
                }
                .background(fill, shape),
        )
    }
}

@Preview
@Composable
private fun OrganicProgressBarPreview() {
    PreviewSurface(width = 360.dp) {
        OrganicText("76%", style = OrganicTheme.typography.numericLarge, color = OrganicTheme.colors.accentText)
        OrganicProgressBar(progress = 0.76f)
        OrganicText("26 из 34", style = OrganicTheme.typography.meta, color = OrganicTheme.colors.muted)
        OrganicProgressBar(progress = 0f)
        OrganicProgressBar(progress = 1f)
    }
}
