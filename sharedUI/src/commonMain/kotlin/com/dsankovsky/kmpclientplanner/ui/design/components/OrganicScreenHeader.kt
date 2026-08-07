package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme

/**
 * Шапка экрана: заголовок 34, подпись 13 muted и действия справа (сегмент-контрол,
 * primary-кнопка) — как на 04, 09 и 10.
 *
 * Раскладка зависит от доступной ширины, а не от размера окна: рейл забирает 248, поэтому
 * окно ещё «широкое», а места в контенте уже нет. В одну строку заголовок и действия
 * ставятся только когда действия влезают целиком — иначе `weight(1f)` сжимал текстовую
 * колонку до нуля и заголовок переносился по буквам в вертикальную полоску.
 * Ниже порога заголовок занимает всю ширину, а действия уезжают на свою строку
 * с горизонтальной прокруткой: сегмент-контрол из шести периодов сам по себе шире
 * узкого окна.
 */
@Composable
fun OrganicScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    /** Порог подбирается под самые широкие действия экрана (04 — сегмент-контрол + кнопка). */
    singleRowMinWidth: Dp = 960.dp,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val singleRow = actions == null || maxWidth >= singleRowMinWidth
        if (singleRow) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                TitleBlock(title, subtitle, Modifier.weight(1f))
                actions?.invoke(this)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                TitleBlock(title, subtitle, Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    actions.invoke(this)
                }
            }
        }
    }
}

@Composable
private fun TitleBlock(title: String, subtitle: String?, modifier: Modifier) {
    Column(modifier) {
        OrganicText(
            text = title,
            style = OrganicTheme.typography.h2.copy(fontSize = 34.sp, lineHeight = 38.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (subtitle != null) {
            OrganicText(
                text = subtitle,
                style = OrganicTheme.typography.bodyXs,
                color = OrganicTheme.colors.muted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun OrganicScreenHeaderPreview() {
    PreviewSurface(width = 1120.dp) {
        OrganicScreenHeader(
            title = "Занятия",
            subtitle = "Пятница, 7 августа · 5 занятий, 2 не оплачены",
            actions = {
                OrganicButton("Добавить услугу", {})
            },
        )
    }
}

@Preview
@Composable
private fun OrganicScreenHeaderNarrowPreview() {
    PreviewSurface(width = 420.dp) {
        OrganicScreenHeader(
            title = "Занятия",
            subtitle = "Пятница, 7 августа · 5 занятий, 2 не оплачены",
            actions = {
                OrganicButton("Добавить услугу", {})
            },
        )
    }
}
