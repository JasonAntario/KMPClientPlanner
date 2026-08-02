package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * `.seg` — pill-контейнер с бордером divider; опции 13px, паддинг 7/12,
 * выбранная — фон accent и текст bg, между опциями — линия divider.
 *
 * Используется фильтрами периода на главной (Сегодня/Завтра/Неделя/Месяц)
 * и в статистике (День/Неделя/Месяц/Всё время).
 */
@Composable
fun <T> SegmentedControl(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier,
) {
    val colors = OrganicTheme.colors
    val shape = OrganicTheme.shapes.pill
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clip(shape)
            .border(1.dp, colors.divider, shape),
    ) {
        options.forEachIndexed { index, option ->
            if (index > 0) {
                Box(Modifier.width(1.dp).fillMaxHeight().background(colors.divider))
            }
            SegmentOption(
                label = optionLabel(option),
                selected = option == selected,
                onClick = { onSelect(option) },
            )
        }
    }
}

@Composable
private fun SegmentOption(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = OrganicTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    Box(
        modifier = Modifier
            .background(
                when {
                    selected -> colors.accent
                    hovered -> colors.hover
                    else -> Color.Transparent
                },
            )
            .hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        OrganicText(
            text = label,
            style = OrganicTheme.typography.bodyXs,
            color = if (selected) colors.bg else colors.text,
        )
    }
}

@Preview
@Composable
private fun SegmentedControlPreview() {
    PreviewSurface {
        var period by remember { mutableStateOf("Неделя") }
        SegmentedControl(
            options = listOf("Сегодня", "Завтра", "Неделя", "Месяц"),
            selected = period,
            onSelect = { period = it },
            optionLabel = { it },
        )
        SegmentedControl(
            options = listOf("День", "Неделя", "Месяц", "Всё время"),
            selected = "Всё время",
            onSelect = {},
            optionLabel = { it },
        )
    }
}
