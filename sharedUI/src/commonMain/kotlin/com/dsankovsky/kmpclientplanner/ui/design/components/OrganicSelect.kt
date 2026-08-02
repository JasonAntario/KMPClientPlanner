package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.elevationMd
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcon
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.design.organicFocusRing
import androidx.compose.ui.tooling.preview.Preview

/**
 * Селект: поле в оформлении `.input` плюс выпадающий список.
 *
 * Своя реализация вместо `ExposedDropdownMenuBox`: тот приносит оформление M3 — свои
 * радиусы, elevation и ripple, — которое пришлось бы переопределять целиком. Здесь
 * `Popup` из `ui` и `LazyColumn` из foundation.
 */
@Composable
fun <T> OrganicSelect(
    value: T?,
    items: List<T>,
    onSelect: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    maxPopupHeight: Dp = 280.dp,
    itemContent: (@Composable (T) -> Unit)? = null,
) {
    val colors = OrganicTheme.colors
    val shapes = OrganicTheme.shapes
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    var expanded by remember { mutableStateOf(false) }
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }

    val border = when {
        !enabled -> colors.divider
        expanded || focused -> colors.accent
        hovered -> colors.inputBorderHover
        else -> colors.divider
    }

    Box(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (enabled) 1f else OrganicButtonDefaults.DisabledAlpha)
                .onGloballyPositioned { fieldSize = it.size }
                .organicFocusRing(focused, shapes.pill, colors.accent, offset = 0.dp)
                .background(colors.surface, shapes.pill)
                .border(1.dp, border, shapes.pill)
                .defaultMinSize(minHeight = 36.dp)
                .hoverable(interactionSource, enabled = enabled)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                ) { expanded = true }
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val label = value?.let(itemLabel)
            OrganicText(
                text = label ?: placeholder.orEmpty(),
                style = OrganicTheme.typography.bodySm,
                color = if (label != null) colors.text else colors.muted,
                modifier = Modifier.weight(1f),
            )
            OrganicIcon(
                imageVector = OrganicIcons.ChevronRight,
                contentDescription = null,
                size = 16.dp,
                tint = colors.muted,
                // Стрелки «вниз» в наборе макета нет — поворачиваем шеврон.
                modifier = Modifier.graphicsLayer { rotationZ = if (expanded) -90f else 90f },
            )
        }

        if (expanded) {
            Popup(
                offset = IntOffset(0, fieldSize.height + with(density) { 4.dp.roundToPx() }),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                Box(
                    Modifier
                        .width(with(density) { fieldSize.width.toDp() })
                        .elevationMd(shapes.md, OrganicTheme.elevation)
                        .background(colors.surface, shapes.md)
                        .border(1.dp, colors.divider, shapes.md)
                        .heightIn(max = maxPopupHeight),
                ) {
                    LazyColumn(Modifier.padding(vertical = OrganicTheme.spacing.space1)) {
                        items(items) { item ->
                            OrganicSelectItem(
                                selected = item == value,
                                onClick = {
                                    onSelect(item)
                                    expanded = false
                                },
                            ) {
                                if (itemContent != null) {
                                    itemContent(item)
                                } else {
                                    OrganicText(itemLabel(item), style = OrganicTheme.typography.bodySm)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrganicSelectItem(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val colors = OrganicTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when {
                    selected -> colors.accentRamp.s100
                    hovered -> colors.hoverSubtle
                    else -> Color.Transparent
                },
            )
            .hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        content()
    }
}

@Preview
@Composable
private fun OrganicSelectPreview() {
    PreviewSurface(width = 460.dp) {
        val addresses = listOf("Немига, 12", "Сурганова, 5", "Онлайн")
        var address by remember { mutableStateOf<String?>(addresses.first()) }
        OrganicField("Адрес проведения") {
            OrganicSelect(
                value = address,
                items = addresses,
                onSelect = { address = it },
                itemLabel = { it },
            )
        }
        OrganicField("Клиент") {
            OrganicSelect(
                value = null,
                items = listOf("Мария Петрова", "Игорь Ковалёв"),
                onSelect = {},
                itemLabel = { it },
                placeholder = "Выберите клиента",
            )
        }
    }
}
