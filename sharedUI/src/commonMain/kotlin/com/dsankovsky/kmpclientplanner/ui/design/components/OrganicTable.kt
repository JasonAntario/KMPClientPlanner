package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.TabularNumbers
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column

/**
 * `.table` — таблицы в системе короткие (упражнения тренировки, клиенты по выплатам),
 * поэтому это не `LazyColumn`, а набор примитивов: шапка, строка, ячейка.
 * Колонки задаёт вызывающий через `Modifier.weight`/`width` — как в CSS-гриде макета.
 */
@Composable
fun OrganicTableHeader(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = OrganicTheme.colors
    Box(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(OrganicTheme.spacing.space2),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.divider),
        )
    }
}

/** Строка таблицы: линия text@8% снизу, hover text@4%. */
@Composable
fun OrganicTableRow(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = OrganicTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    Box(
        modifier
            .fillMaxWidth()
            .background(if (hovered) colors.hoverSubtle else Color.Transparent)
            .hoverable(interactionSource)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(OrganicTheme.spacing.space2),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.rowLine),
        )
    }
}

/** Ячейка шапки: 11px uppercase с трекингом, цвет text@60%. */
@Composable
fun OrganicTableHeaderCell(text: String, modifier: Modifier = Modifier) {
    OrganicText(
        text = text.uppercase(),
        style = OrganicTheme.typography.tableHeader,
        color = OrganicTheme.colors.tableHeader,
        modifier = modifier,
    )
}

/** Ячейка данных: 14px. */
@Composable
fun OrganicTableCell(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    numeric: Boolean = false,
) {
    OrganicText(
        text = text,
        style = OrganicTheme.typography.bodySm.let {
            if (numeric) it.copy(fontFeatureSettings = TabularNumbers) else it
        },
        color = color,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun OrganicTablePreview() {
    PreviewSurface(width = 560.dp) {
        Column {
            OrganicTableHeader {
                OrganicTableHeaderCell("Клиент", Modifier.weight(1.4f))
                OrganicTableHeaderCell("Занятий оплачено", Modifier.weight(1f))
                OrganicTableHeaderCell("BYN", Modifier.weight(1f))
            }
            listOf(
                Triple("Мария Петрова", "12", "1 080,00"),
                Triple("Игорь Ковалёв", "8", "760,00"),
                Triple("Анна Савчук", "5", "450,00"),
            ).forEach { (name, count, sum) ->
                OrganicTableRow(onClick = {}) {
                    OrganicTableCell(name, Modifier.weight(1.4f))
                    OrganicTableCell(count, Modifier.weight(1f), numeric = true)
                    OrganicTableCell(sum, Modifier.weight(1f), numeric = true)
                }
            }
        }
    }
}
