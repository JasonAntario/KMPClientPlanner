package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.LocalOrganicContentColor
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.StatusColors
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcon
import com.dsankovsky.kmpclientplanner.ui.design.organicFocusRing
import androidx.compose.ui.tooling.preview.Preview
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons

/**
 * Цвета кнопки. Вариантов в системе больше, чем три из `styles.css` (есть ещё
 * деструктивный, статусные и «опасная зона» на экране настроек), поэтому вместо enum —
 * набор фабрик в [OrganicButtonDefaults].
 */
@Immutable
data class OrganicButtonColors(
    val container: Color,
    val content: Color,
    val border: Color = Color.Unspecified,
    val hoverContainer: Color = container,
    val pressedContainer: Color = container,
    /** `.btn-ghost` жмёт горизонтальный паддинг до space-1 — это его единственное отличие в раскладке. */
    val ghost: Boolean = false,
)

object OrganicButtonDefaults {

    /** `.btn-primary` — сплошная заливка акцентом. */
    @Composable
    fun primary(): OrganicButtonColors = with(OrganicTheme.colors) {
        OrganicButtonColors(
            container = accent,
            content = bg,
            hoverContainer = accentRamp.s600,
            pressedContainer = accentRamp.s700,
        )
    }

    /** `.btn-secondary` — бордер divider, тонирование по text. */
    @Composable
    fun secondary(): OrganicButtonColors = with(OrganicTheme.colors) {
        OrganicButtonColors(
            container = Color.Transparent,
            content = text,
            border = divider,
            hoverContainer = hover,
            pressedContainer = pressed,
        )
    }

    /** `.btn-ghost` — только текст акцентом. */
    @Composable
    fun ghost(): OrganicButtonColors = with(OrganicTheme.colors) {
        OrganicButtonColors(
            container = Color.Transparent,
            content = accent,
            hoverContainer = ghostHover,
            pressedContainer = ghostPressed,
            ghost = true,
        )
    }

    /** Удаление и сброс: заливка accent-700. */
    @Composable
    fun destructive(): OrganicButtonColors = with(OrganicTheme.colors) {
        OrganicButtonColors(
            container = this.destructive.fill,
            content = this.destructive.content,
            hoverContainer = accentRamp.s800,
            pressedContainer = accentRamp.s900,
        )
    }

    /** Кнопка-статус: тройка fill / content / border из семантики. */
    @Composable
    fun status(status: StatusColors): OrganicButtonColors = OrganicButtonColors(
        container = status.fill,
        content = status.content,
        border = status.border,
        hoverContainer = status.fill,
        pressedContainer = status.fill,
    )

    /** «Опасная зона» на экране настроек: бордер accent-600, текст accent-800. */
    @Composable
    fun dangerOutlined(): OrganicButtonColors = with(OrganicTheme.colors) {
        OrganicButtonColors(
            container = Color.Transparent,
            content = accentRamp.s800,
            border = accentRamp.s600,
            hoverContainer = accentRamp.s200,
            pressedContainer = accentRamp.s300,
        )
    }

    /** Размер icon-кнопки (`.btn-icon`). */
    val IconButtonSize: Dp = 36.dp

    /** Прозрачность выключенного контрола. */
    const val DisabledAlpha: Float = 0.45f
}

/**
 * `.btn` — кнопка с текстом и опциональной иконкой.
 *
 * Построена на `Box` + `clickable` без M3: ripple в Organic не используется, состояния —
 * тонирование из рампы, фокус — обводка 2px accent с отступом 2.
 */
@Composable
fun OrganicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: OrganicButtonColors = OrganicButtonDefaults.primary(),
    icon: ImageVector? = null,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = false,
    textStyle: TextStyle = OrganicTheme.typography.button,
) {
    val spacing = OrganicTheme.spacing
    val horizontal = if (colors.ghost) spacing.space1 else spacing.space3 * 1.2f
    OrganicClickableSurface(
        onClick = onClick,
        modifier = modifier.then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier),
        colors = colors,
        enabled = enabled,
        shape = OrganicTheme.shapes.pill,
        contentPadding = PaddingValuesOf(horizontal = horizontal, vertical = spacing.space2),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                OrganicIcon(icon, contentDescription = null, size = 16.dp, tint = colors.content)
            }
            // Подпись кнопки не переносится: pill в макете всегда однострочный.
            OrganicText(
                text = text,
                style = textStyle,
                color = colors.content,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

/** `.btn-icon` — квадратная кнопка 36×36 с одной иконкой. */
@Composable
fun OrganicIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    colors: OrganicButtonColors = OrganicButtonDefaults.secondary(),
    enabled: Boolean = true,
    size: Dp = OrganicButtonDefaults.IconButtonSize,
    iconSize: Dp = 18.dp,
) {
    OrganicClickableSurface(
        onClick = onClick,
        modifier = modifier.size(size),
        colors = colors,
        enabled = enabled,
        shape = OrganicTheme.shapes.pill,
        contentPadding = PaddingValuesOf(0.dp, 0.dp),
    ) {
        OrganicIcon(icon, contentDescription = contentDescription, size = iconSize, tint = colors.content)
    }
}

/**
 * Общая «поверхность» кликабельных атомов: заливка по состоянию, бордер, фокус-ринг,
 * disabled. На ней собраны кнопки, статусы, опции сегмент-контрола и строки списков.
 */
@Composable
fun OrganicClickableSurface(
    onClick: () -> Unit,
    colors: OrganicButtonColors,
    shape: Shape,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValuesOf = PaddingValuesOf(0.dp, 0.dp),
    borderWidth: Dp = 1.dp,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    val focused by interactionSource.collectIsFocusedAsState()

    val container = when {
        !enabled -> colors.container
        pressed -> colors.pressedContainer
        hovered -> colors.hoverContainer
        else -> colors.container
    }

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else OrganicButtonDefaults.DisabledAlpha)
            .organicFocusRing(focused, shape, OrganicTheme.colors.accent)
            .background(container, shape)
            .then(
                if (colors.border.isSpecified) Modifier.border(borderWidth, colors.border, shape)
                else Modifier,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = contentPadding.horizontal, vertical = contentPadding.vertical),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalOrganicContentColor provides colors.content, content = content)
    }
}

/** Пара отступов; отдельный тип, чтобы не тащить в атомы `PaddingValues` с четырьмя сторонами. */
@Immutable
data class PaddingValuesOf(val horizontal: Dp, val vertical: Dp)

@Preview
@Composable
private fun OrganicButtonPreview() {
    PreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OrganicButton("Добавить услугу", {}, icon = OrganicIcons.Plus)
            OrganicButton("Отмена", {}, colors = OrganicButtonDefaults.secondary())
            OrganicButton("Удалить услугу", {}, colors = OrganicButtonDefaults.ghost())
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OrganicButton("Сбросить приложение", {}, colors = OrganicButtonDefaults.destructive())
            OrganicButton("Сбросить", {}, colors = OrganicButtonDefaults.dangerOutlined())
            OrganicButton("Оплатить", {}, enabled = false)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OrganicIconButton(OrganicIcons.Pencil, {}, "Редактировать")
            OrganicIconButton(OrganicIcons.Trash, {}, "Удалить")
            OrganicIconButton(OrganicIcons.Plus, {}, "Добавить", colors = OrganicButtonDefaults.primary())
            OrganicIconButton(OrganicIcons.X, {}, "Закрыть", enabled = false)
        }
        OrganicButton("Кнопка во всю ширину", {}, fillMaxWidth = true)
    }
}
