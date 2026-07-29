package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Клавиатурный фокус по DS: `:focus-visible { outline: 2px solid accent; outline-offset: 2px }`.
 * Рисуется снаружи геометрии, поэтому вешать до `clip`/`background` не обязательно, но
 * родитель не должен обрезать содержимое.
 *
 * Состояние фокуса передаётся снаружи — атомы и так собирают `InteractionSource`,
 * так что модификатор остаётся чистым и не требует композиции.
 */
fun Modifier.organicFocusRing(
    focused: Boolean,
    shape: Shape,
    color: Color,
    width: Dp = 2.dp,
    offset: Dp = 2.dp,
): Modifier = drawWithContent {
    drawContent()
    if (!focused) return@drawWithContent
    val inset = (offset + width / 2).toPx()
    val ringSize = Size(size.width + inset * 2, size.height + inset * 2)
    if (ringSize.isEmpty()) return@drawWithContent
    val stroke = Stroke(width.toPx())
    translate(-inset, -inset) {
        when (val outline = shape.createOutline(ringSize, layoutDirection, this@drawWithContent)) {
            is Outline.Rectangle -> drawRect(
                color = color,
                topLeft = outline.rect.topLeft,
                size = outline.rect.size,
                style = stroke,
            )

            is Outline.Rounded -> drawPath(
                path = Path().apply { addRoundRect(outline.roundRect) },
                color = color,
                style = stroke,
            )

            is Outline.Generic -> drawPath(path = outline.path, color = color, style = stroke)
        }
    }
}

/** Тот же фокус-ринг, но берёт состояние из `InteractionSource` компонента. */
@Composable
fun Modifier.organicFocusRing(
    interactionSource: InteractionSource,
    shape: Shape,
    color: Color = OrganicTheme.colors.accent,
    width: Dp = 2.dp,
    offset: Dp = 2.dp,
): Modifier {
    val focused by interactionSource.collectIsFocusedAsState()
    return organicFocusRing(focused, shape, color, width, offset)
}

/**
 * Organic не использует M3-ripple: hover/pressed — это тонирование из рампы,
 * которое каждый атом рисует сам. Поэтому дефолтная индикация отключается.
 */
object NoIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = NoIndicationNode()

    override fun hashCode(): Int = NoIndicationHashCode

    override fun equals(other: Any?): Boolean = other === this
}

private const val NoIndicationHashCode = -1

private class NoIndicationNode : androidx.compose.ui.Modifier.Node()

/** Явная точка расширения, если где-то понадобится штатная индикация. */
val OrganicIndication: Indication = NoIndication
