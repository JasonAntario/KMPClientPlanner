package com.dsankovsky.kmpclientplanner.uinew.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors

/**
 * The rail | master | detail layout used by Home and Clients.
 */
@Composable
fun MasterDetailScaffold(
    rail: @Composable () -> Unit,
    master: @Composable () -> Unit,
    detail: @Composable () -> Unit,
    masterWidth: Dp = 400.dp,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxSize().background(LessonsColors.CardBackground)) {
        rail()
        VerticalDivider(color = LessonsColors.BorderSoft)
        Column(
            modifier = Modifier
                .width(masterWidth)
                .fillMaxHeight()
                .background(LessonsColors.PanelBackground),
        ) { master() }
        VerticalDivider(color = LessonsColors.BorderSoft)
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) { detail() }
    }
}

/** rail | single content pane (Statistics, Settings). */
@Composable
fun RailContentScaffold(
    rail: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxSize().background(LessonsColors.CardBackground)) {
        rail()
        VerticalDivider(color = LessonsColors.BorderSoft)
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) { content() }
    }
}

/**
 * Dimmed full-screen scrim with a centered rounded card. Used for the create/edit
 * and prepay dialogs from the design.
 */
@Composable
fun ModalScaffold(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 448.dp,
    content: @Composable () -> Unit,
) {
    val noInteraction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x73000000))
            .clickable(interactionSource = noInteraction, indication = null, onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = cardWidth)
                .fillMaxWidth()
                .heightIn(max = 720.dp)
                .padding(24.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(LessonsColors.RailBackground)
                // swallow clicks so they don't dismiss
                .clickable(interactionSource = noInteraction, indication = null, onClick = {}),
        ) { content() }
    }
}

/** A row of [FilterChip]s. */
@Composable
fun ChipRow(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEachIndexed { index, label ->
            FilterChip(
                label = label,
                selected = index == selectedIndex,
                onClick = { onSelect(index) })
        }
    }
}

/** Filled placeholder pane used only by the scaffold previews. */
@Composable
private fun PreviewPane(color: Color) {
    Box(Modifier.fillMaxSize().background(color))
}

@Preview
@Composable
private fun MasterDetailScaffoldPreview() = ComponentPreview(padding = 0.dp) {
    Box(Modifier.size(width = 760.dp, height = 440.dp)) {
        MasterDetailScaffold(
            rail = { PreviewPane(LessonsColors.RailBackground) },
            master = { PreviewPane(LessonsColors.PanelBackground) },
            detail = { PreviewPane(LessonsColors.CardBackground) },
        )
    }
}

@Preview
@Composable
private fun RailContentScaffoldPreview() = ComponentPreview(padding = 0.dp) {
    Box(Modifier.size(width = 760.dp, height = 440.dp)) {
        RailContentScaffold(
            rail = { PreviewPane(LessonsColors.RailBackground) },
            content = { PreviewPane(LessonsColors.CardBackground) },
        )
    }
}

@Preview
@Composable
private fun ModalScaffoldPreview() = ComponentPreview(padding = 0.dp) {
    Box(Modifier.size(width = 640.dp, height = 480.dp)) {
        ModalScaffold(onDismiss = {}) {
            Box(Modifier.fillMaxWidth().height(220.dp))
        }
    }
}

@Preview
@Composable
private fun ChipRowPreview() = ComponentPreview {
    var selected by remember { mutableStateOf(0) }
    ChipRow(
        labels = listOf("Сегодня", "Неделя", "Месяц"),
        selectedIndex = selected,
        onSelect = { selected = it },
    )
}
