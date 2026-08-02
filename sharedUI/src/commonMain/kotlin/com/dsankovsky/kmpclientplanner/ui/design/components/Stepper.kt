package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Степпер «− значение +» из модалки предоплаты: значение — heading 30,
 * ширина не меньше 44, чтобы кнопки не прыгали при смене разрядности.
 */
@Composable
fun OrganicStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = 1..Int.MAX_VALUE,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrganicIconButton(
            icon = OrganicIcons.Minus,
            onClick = { onValueChange(value - 1) },
            contentDescription = null,
            enabled = enabled && value > range.first,
        )
        OrganicText(
            text = value.toString(),
            style = OrganicTheme.typography.h3,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 44.dp),
        )
        OrganicIconButton(
            icon = OrganicIcons.Plus,
            onClick = { onValueChange(value + 1) },
            contentDescription = null,
            enabled = enabled && value < range.last,
        )
    }
}

@Preview
@Composable
private fun OrganicStepperPreview() {
    PreviewSurface {
        var count by remember { mutableStateOf(3) }
        OrganicStepper(value = count, onValueChange = { count = it }, range = 1..8)
        OrganicText(
            "от 1 до 8",
            style = OrganicTheme.typography.meta,
            color = OrganicTheme.colors.muted,
        )
        OrganicStepper(value = 1, onValueChange = {}, range = 1..1)
    }
}
