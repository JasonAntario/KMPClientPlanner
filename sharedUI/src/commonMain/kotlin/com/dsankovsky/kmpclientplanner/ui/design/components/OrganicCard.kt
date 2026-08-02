package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme

/**
 * `.card` — фон surface, radius 32, паддинг 13.2, gap 8.8.
 *
 * Тень по умолчанию не ставится: в макете карточки лежат на кремовой земле плоско,
 * shadow-md появляется только у выделенной строки и у поповеров.
 */
@Composable
fun OrganicCard(
    modifier: Modifier = Modifier,
    kicker: String? = null,
    title: String? = null,
    background: Color = OrganicTheme.colors.surface,
    shape: Shape = OrganicTheme.shapes.card,
    border: Color = Color.Unspecified,
    shadow: Shadow? = null,
    contentPadding: Dp = OrganicTheme.spacing.space3,
    verticalGap: Dp = OrganicTheme.spacing.space2,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .then(if (shadow != null) Modifier.dropShadow(shape, shadow) else Modifier)
            .background(background, shape)
            .then(if (border.isSpecified) Modifier.border(1.dp, border, shape) else Modifier)
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(verticalGap),
    ) {
        if (kicker != null) {
            OrganicText(
                text = kicker.uppercase(),
                style = OrganicTheme.typography.kicker,
                color = OrganicTheme.colors.accent,
            )
        }
        if (title != null) {
            OrganicText(text = title, style = OrganicTheme.typography.cardTitle)
        }
        content()
    }
}

@Preview
@Composable
private fun OrganicCardPreview() {
    PreviewSurface(width = 460.dp) {
        OrganicCard(kicker = "услуга · 1 ч", title = "Заметка о занятии") {
            OrganicText(
                "Разобрали Present Perfect, к следующему разу — упражнения 4–7.",
                style = OrganicTheme.typography.bodySm,
            )
        }
        OrganicCard(
            title = "Не оплачено",
            shadow = OrganicTheme.elevation.md,
        ) {
            OrganicText(
                "180,00 BYN",
                style = OrganicTheme.typography.numeric,
                color = OrganicTheme.colors.accentText
            )
        }
        OrganicCard(
            title = "Опасная зона",
            background = OrganicTheme.colors.accentRamp.s100,
        ) {
            OrganicText(
                "Сбросить приложение и удалить все данные.",
                style = OrganicTheme.typography.bodySm
            )
            OrganicButton("Сбросить", {}, colors = OrganicButtonDefaults.dangerOutlined())
        }
    }
}
