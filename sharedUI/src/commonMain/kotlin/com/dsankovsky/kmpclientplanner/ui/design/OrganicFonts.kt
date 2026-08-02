package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.nunito_variable
import org.jetbrains.compose.resources.Font

/**
 * Шрифт приложения — Nunito (Google Fonts, OFL-1.1, текст лицензии в `sharedUI/licenses/`).
 *
 * Хендофф рисовался на Caprasimo + Figtree, но в обеих гарнитурах нет кириллицы, а весь
 * интерфейс русский — они бы работали только на латинице и цифрах. Nunito закрывает
 * кириллицу и держит ту же скруглённую «тёплую» пластику, поэтому одна гарнитура на всё:
 * дисплейную роль отыгрывает вес [FontWeight.Black], текстовую — 400/600/700.
 *
 * Файл вариативный: `Font(...)` в Compose Resources 1.11 по умолчанию передаёт ось `wght`
 * из [FontWeight], так что все веса берутся из одного файла.
 */
@Composable
fun organicFontFamily(): FontFamily = FontFamily(
    Font(Res.font.nunito_variable, FontWeight.Normal),
    Font(Res.font.nunito_variable, FontWeight.Medium),
    Font(Res.font.nunito_variable, FontWeight.SemiBold),
    Font(Res.font.nunito_variable, FontWeight.Bold),
    Font(Res.font.nunito_variable, FontWeight.ExtraBold),
    Font(Res.font.nunito_variable, FontWeight.Black),
)

/** Шкала [organicTypography] на фирменном шрифте — значение по умолчанию для [OrganicTheme]. */
@Composable
fun rememberOrganicTypography(): OrganicTypography {
    val family = organicFontFamily()
    return remember(family) { organicTypography(heading = family, body = family) }
}
