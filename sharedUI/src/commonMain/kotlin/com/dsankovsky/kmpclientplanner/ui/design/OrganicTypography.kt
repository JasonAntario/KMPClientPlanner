package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Типографика Organic: Caprasimo (display) над Figtree (body).
 *
 * Сами файлы шрифтов подключаются на этапе 2 — до этого семейства подставляются
 * системные, чтобы шкала уже работала. Размеры/интерлиньяж/трекинг — из `styles.css`
 * и разметки макета.
 */
@Immutable
data class OrganicTypography(
    /** 42 — заголовок приветственного экрана. */
    val h1: TextStyle,
    /** 32 — шапки экранов, заголовки пустых состояний. */
    val h2: TextStyle,
    /** 25 — заголовки в деталях. */
    val h3: TextStyle,
    /** 20 — заголовок модального окна (`.dialog-title`). */
    val h4: TextStyle,
    /** 17 — заголовок карточки (`.card-title`). */
    val cardTitle: TextStyle,
    /** 19 — бренд в шапке навигационного рейла (`.nav-brand`). */
    val brand: TextStyle,
    /** 26 — время в строке услуги. */
    val numeric: TextStyle,
    /** 38 — метрики на экране статистики. */
    val numericLarge: TextStyle,
    /** 15 — основной текст. */
    val body: TextStyle,
    /** 14 — плотный текст, значения полей, строки таблиц. */
    val bodySm: TextStyle,
    /** 13 — текст карточки, опции сегмент-контрола. */
    val bodyXs: TextStyle,
    /** 14 heading-семейством — надпись на кнопке (`.btn`). */
    val button: TextStyle,
    /** 12 — подпись поля формы (`.field > label`). */
    val label: TextStyle,
    /** 11 — мета-информация (`.card-meta`, `figcaption`). */
    val meta: TextStyle,
    /** 10–11 uppercase с трекингом — киккеры и шапки таблиц. */
    val kicker: TextStyle,
    /** 11 uppercase — шапка таблицы (`.table th`). */
    val tableHeader: TextStyle,
    /** 11 — текст тега (`.tag`). */
    val tag: TextStyle,
    /** 14 — ссылка. */
    val link: TextStyle,
)

/**
 * Собирает шкалу на переданных семействах. На этапе 2 сюда придут
 * `Res.font.caprasimo_regular` / `Res.font.figtree_*`.
 */
fun organicTypography(
    heading: FontFamily = FontFamily.Default,
    body: FontFamily = FontFamily.Default,
): OrganicTypography {
    val display = TextStyle(
        fontFamily = heading,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.015).em,
    )
    val text = TextStyle(fontFamily = body, fontWeight = FontWeight.Normal)
    return OrganicTypography(
        h1 = display.copy(fontSize = 42.sp, lineHeight = 47.sp),
        h2 = display.copy(fontSize = 32.sp, lineHeight = 36.sp),
        h3 = display.copy(fontSize = 25.sp, lineHeight = 28.sp),
        h4 = display.copy(fontSize = 20.sp, lineHeight = 24.sp),
        cardTitle = display.copy(fontSize = 17.sp, lineHeight = 20.sp),
        brand = display.copy(fontSize = 19.sp, lineHeight = 22.sp),
        // Денежные суммы и время выравниваем по колонкам: моноширинные цифры.
        numeric = display.copy(
            fontSize = 26.sp,
            lineHeight = 29.sp,
            fontFeatureSettings = TabularNumbers,
        ),
        numericLarge = display.copy(
            fontSize = 38.sp,
            lineHeight = 42.sp,
            fontFeatureSettings = TabularNumbers,
        ),
        body = text.copy(fontSize = 15.sp, lineHeight = 23.sp),
        bodySm = text.copy(fontSize = 14.sp, lineHeight = 22.sp),
        bodyXs = text.copy(fontSize = 13.sp, lineHeight = 20.sp),
        button = display.copy(fontSize = 14.sp, lineHeight = 17.sp, letterSpacing = 0.sp),
        label = text.copy(fontSize = 12.sp, lineHeight = 17.sp),
        meta = text.copy(fontSize = 11.sp, lineHeight = 16.sp),
        kicker = text.copy(
            fontSize = 10.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.1.em,
        ),
        tableHeader = text.copy(
            fontSize = 11.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.08.em,
        ),
        tag = text.copy(fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.02.em),
        link = text.copy(fontSize = 14.sp, lineHeight = 22.sp, textDecoration = TextDecoration.Underline),
    )
}

/** `font-variant-numeric: tabular-nums` — суммы и время не «прыгают» в колонках. */
const val TabularNumbers: String = "tnum"
