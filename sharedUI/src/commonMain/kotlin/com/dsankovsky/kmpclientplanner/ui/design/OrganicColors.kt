package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Тональная рампа 100..900 из `design-handoff/_ds/organic-.../styles.css`.
 *
 * Рампы сгенерированы в OKLCH на одной шкале светлоты, поэтому одинаковый шаг разных
 * рамп имеет одинаковый визуальный вес. Использование по DS-гайду:
 * - 100–300 — тонированные заливки, hover'ы, мягкие бордеры;
 * - 500 — база роли;
 * - 700–900 — текст на тонированных заливках и pressed-состояния.
 */
@Immutable
data class ColorRamp(
    val s100: Color,
    val s200: Color,
    val s300: Color,
    val s400: Color,
    val s500: Color,
    val s600: Color,
    val s700: Color,
    val s800: Color,
    val s900: Color,
)

/** Тройка цветов для статусной сущности: заливка, контент на ней, бордер. */
@Immutable
data class StatusColors(
    val fill: Color,
    val content: Color,
    val border: Color,
)

private val NeutralRamp = ColorRamp(
    s100 = Color(0xFFF9F4ED),
    s200 = Color(0xFFEEE7DB),
    s300 = Color(0xFFDCD3C4),
    s400 = Color(0xFFC0B6A5),
    s500 = Color(0xFFA19786),
    s600 = Color(0xFF82796A),
    s700 = Color(0xFF645C50),
    s800 = Color(0xFF474238),
    s900 = Color(0xFF2E2B25),
)

private val AccentRamp = ColorRamp(
    s100 = Color(0xFFFFF2EB),
    s200 = Color(0xFFFFE1D0),
    s300 = Color(0xFFFFC6A5),
    s400 = Color(0xFFF6A06B),
    s500 = Color(0xFFD67F48),
    s600 = Color(0xFFB2622D),
    s700 = Color(0xFF8C491A),
    s800 = Color(0xFF643312),
    s900 = Color(0xFF402310),
)

private val Accent2Ramp = ColorRamp(
    s100 = Color(0xFFF0FAE1),
    s200 = Color(0xFFE1EECC),
    s300 = Color(0xFFCCDBB2),
    s400 = Color(0xFFAEBF92),
    s500 = Color(0xFF8FA073),
    s600 = Color(0xFF728157),
    s700 = Color(0xFF56633F),
    s800 = Color(0xFF3D472B),
    s900 = Color(0xFF272E1B),
)

/**
 * Цветовые токены Organic. Роли + три рампы; всё производное (muted, divider, hover,
 * pressed, статусы) вычисляется здесь, чтобы в компонентах не было ad-hoc альф.
 */
@Immutable
data class OrganicColors(
    val bg: Color = Color(0xFFF5EAD8),
    val surface: Color = Color(0xFFEBDDC5),
    val text: Color = Color(0xFF201E1D),
    val accent: Color = Color(0xFFC67139),
    val accent2: Color = Color(0xFF7A8A5E),
    val neutralRamp: ColorRamp = NeutralRamp,
    val accentRamp: ColorRamp = AccentRamp,
    val accent2Ramp: ColorRamp = Accent2Ramp,
) {
    /** `--color-divider`: границы полей, шапка таблицы, бордер сегмент-контрола. */
    val divider: Color get() = text.copy(alpha = 0.16f)

    /** Линия между строками таблицы (`.table td`). */
    val rowLine: Color get() = text.copy(alpha = 0.08f)

    /** `.text-muted`, `figcaption` — второстепенный текст. */
    val muted: Color get() = text.copy(alpha = 0.55f)

    /** Подписи полей формы (`.field > label`). */
    val label: Color get() = text.copy(alpha = 0.70f)

    /** Шапки таблиц (`.table th`). */
    val tableHeader: Color get() = text.copy(alpha = 0.60f)

    /** hover строки таблицы / списка (`tbody tr:hover`). */
    val hoverSubtle: Color get() = text.copy(alpha = 0.04f)

    /** hover secondary-кнопки и сегмент-опции. */
    val hover: Color get() = text.copy(alpha = 0.07f)

    /** pressed secondary-кнопки и сегмент-опции. */
    val pressed: Color get() = text.copy(alpha = 0.14f)

    /** hover ghost-кнопки — тонирование акцентом. */
    val ghostHover: Color get() = accent.copy(alpha = 0.10f)

    /** pressed ghost-кнопки. */
    val ghostPressed: Color get() = accent.copy(alpha = 0.18f)

    /** Бордер поля под курсором (`.input:hover`). */
    val inputBorderHover: Color get() = text.copy(alpha = 0.45f)

    /** Подложка модального окна (`.dialog-backdrop`). */
    val scrim: Color get() = neutralRamp.s900.copy(alpha = 0.50f)

    /** Выделение текста (`::selection`). */
    val selection: Color get() = accent.copy(alpha = 0.30f)

    /**
     * Акцент для текста в размере абзаца: пара accent-к-фону даёт только 3:1,
     * этого хватает иконкам и крупному тексту, но не body-размеру.
     */
    val accentText: Color get() = accentRamp.s700

    /** Оплачено / проведено. */
    val positive: StatusColors
        get() = StatusColors(accent2Ramp.s100, accent2Ramp.s800, accent2Ramp.s400)

    /** Не оплачено / запланировано. */
    val negative: StatusColors
        get() = StatusColors(accentRamp.s100, accentRamp.s800, accentRamp.s400)

    /** Удаление, сброс данных. */
    val destructive: StatusColors
        get() = StatusColors(accentRamp.s700, bg, accentRamp.s700)

    /** Нейтральный тег. */
    val neutralStatus: StatusColors
        get() = StatusColors(neutralRamp.s100, neutralRamp.s800, neutralRamp.s300)
}
