package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import kotlin.math.absoluteValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row

/**
 * Кружок с инициалами: 38–40 в списке клиентов, 48 в настройках, 76 в деталях.
 *
 * Фон выбирается детерминированно по имени из трёх «мягких» шагов рамп —
 * так у клиента всегда один и тот же цвет, а список остаётся разноцветным.
 */
@Composable
fun Avatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    fontSize: TextUnit = (size.value * 0.375f).sp,
    seed: String = initials,
) {
    val colors = OrganicTheme.colors
    val palette = listOf(
        colors.accentRamp.s200 to colors.accentRamp.s800,
        colors.accent2Ramp.s200 to colors.accent2Ramp.s800,
        colors.neutralRamp.s300 to colors.neutralRamp.s800,
    )
    val (background, content) = palette[seed.hashCode().absoluteValue % palette.size]
    Avatar(
        initials = initials,
        background = background,
        content = content,
        modifier = modifier,
        size = size,
        fontSize = fontSize,
    )
}

/** Тот же кружок, но с явными цветами — для логотипа на приветственном экране и заглушек. */
@Composable
fun Avatar(
    initials: String,
    background: Color,
    content: Color,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    fontSize: TextUnit = (size.value * 0.375f).sp,
) {
    Box(
        modifier = modifier.size(size).background(background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        OrganicText(
            text = initials,
            style = OrganicTheme.typography.cardTitle.copy(fontSize = fontSize, lineHeight = fontSize * 1.2f),
            color = content,
        )
    }
}

@Preview
@Composable
private fun AvatarPreview() {
    PreviewSurface {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar("МП", size = 38.dp)
            Avatar("ИК", size = 40.dp)
            Avatar("АС", size = 48.dp)
            Avatar("ЕВ", size = 76.dp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Avatar(
                initials = "КБ",
                background = OrganicTheme.colors.accent,
                content = OrganicTheme.colors.bg,
                size = 64.dp,
            )
        }
    }
}
