package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.LocalOrganicContentColor
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import androidx.compose.ui.tooling.preview.Preview

/**
 * Текст DS: `BasicText` со стилем из шкалы Organic и цветом из [LocalOrganicContentColor].
 *
 * Стиль передаётся явно почти всегда — в макете у каждого текста своя роль,
 * «наследуемого» размера в системе нет.
 */
@Composable
fun OrganicText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = OrganicTheme.typography.body,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val resolved = when {
        color.isSpecified -> color
        style.color.isSpecified -> style.color
        else -> LocalOrganicContentColor.current
    }
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = resolved, textAlign = textAlign ?: style.textAlign),
        softWrap = softWrap,
        maxLines = maxLines,
        overflow = overflow,
    )
}

/**
 * `.hr` — линия-разделитель. В системе она редкая: Organic предпочитает воздух,
 * но в модалках между основными и категорийными полями разделитель есть.
 */
@Composable
fun OrganicDivider(modifier: Modifier = Modifier, color: Color = OrganicTheme.colors.divider) {
    Box(modifier.fillMaxWidth().height(1.dp).background(color))
}

@Preview
@Composable
private fun OrganicTextPreview() {
    PreviewSurface(width = 460.dp) {
        OrganicText("Заголовок H1", style = OrganicTheme.typography.h1)
        OrganicText("Заголовок H2", style = OrganicTheme.typography.h2)
        OrganicText("Заголовок H3", style = OrganicTheme.typography.h3)
        OrganicText("Заголовок карточки", style = OrganicTheme.typography.cardTitle)
        OrganicText("1 234,00 BYN", style = OrganicTheme.typography.numeric)
        OrganicText("Основной текст 15 — база системы", style = OrganicTheme.typography.body)
        OrganicText("Плотный текст 14", style = OrganicTheme.typography.bodySm)
        OrganicText("Подпись поля 12", style = OrganicTheme.typography.label, color = OrganicTheme.colors.label)
        OrganicText("Метаданные 11", style = OrganicTheme.typography.meta, color = OrganicTheme.colors.muted)
        OrganicText("КИККЕР", style = OrganicTheme.typography.kicker, color = OrganicTheme.colors.accent)
        OrganicDivider()
    }
}
