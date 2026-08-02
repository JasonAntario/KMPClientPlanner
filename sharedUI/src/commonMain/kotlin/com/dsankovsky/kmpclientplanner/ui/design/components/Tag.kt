package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcon
import androidx.compose.ui.tooling.preview.Preview
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons

/** Цвета тега: заливка + текст, для outline-варианта — бордер вместо заливки. */
@Immutable
data class TagColors(
    val container: Color,
    val content: Color,
    val border: Color = Color.Unspecified,
)

object TagDefaults {
    @Composable
    fun accent(): TagColors = with(OrganicTheme.colors) { TagColors(accentRamp.s100, accentRamp.s800) }

    @Composable
    fun accent2(): TagColors = with(OrganicTheme.colors) { TagColors(accent2Ramp.s100, accent2Ramp.s800) }

    @Composable
    fun neutral(): TagColors = with(OrganicTheme.colors) { TagColors(neutralRamp.s100, neutralRamp.s800) }

    @Composable
    fun outline(): TagColors = with(OrganicTheme.colors) { TagColors(Color.Transparent, accent, accent) }
}

/**
 * `.tag` — 11px, паддинг 3/10, radius 12 (`radius-md * 0.75`).
 *
 * В макете это статусы в компактных строках занятий, «Проект: …», дельта веса «+5 кг»
 * и метки предоплат в карточке клиента.
 */
@Composable
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    colors: TagColors = TagDefaults.neutral(),
    icon: ImageVector? = null,
) {
    val shape = OrganicTheme.shapes.tag
    Row(
        modifier = modifier
            .background(colors.container, shape)
            .then(if (colors.border.isSpecified) Modifier.border(1.dp, colors.border, shape) else Modifier)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            OrganicIcon(icon, contentDescription = null, size = 12.dp, tint = colors.content)
        }
        OrganicText(
            text = text,
            style = OrganicTheme.typography.tag,
            color = colors.content,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Preview
@Composable
private fun TagPreview() {
    PreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Tag("Проект: рукав", colors = TagDefaults.accent())
            Tag("+5 кг", colors = TagDefaults.accent2())
            Tag("Предоплата 4", colors = TagDefaults.neutral())
            Tag("Онлайн", colors = TagDefaults.outline())
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Tag("Оплачено", colors = TagDefaults.accent2(), icon = OrganicIcons.Banknote)
            Tag("Запланировано", colors = TagDefaults.accent(), icon = OrganicIcons.Calendar)
        }
    }
}
