package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Радиусы Organic. Базовые `--radius-sm/md/lg` плюс финальные переопределения из
 * `styles.css`: карточки и диалоги — `radius-lg * 1.15`, кнопки/теги/инпуты/сегменты —
 * pill (`border-radius: 999px`, что при любой высоте равно 50%).
 */
@Immutable
data class OrganicShapes(
    val sm: Shape = RoundedCornerShape(8.dp),
    val md: Shape = RoundedCornerShape(16.dp),
    val lg: Shape = RoundedCornerShape(28.dp),
    /** `.card` и `.dialog`: 28 * 1.15. */
    val card: Shape = RoundedCornerShape(32.dp),
    /** `.btn`, `.input`, `.seg`, `.tag`-pill. */
    val pill: Shape = RoundedCornerShape(percent = 50),
    /** `.tag`: `radius-md * 0.75`. */
    val tag: Shape = RoundedCornerShape(12.dp),
    /** `textarea.input` — единственное поле, которое не становится pill. */
    val textArea: Shape = RoundedCornerShape(16.dp),
    /** Плитки фотографий 3:4 и превью. */
    val photo: Shape = RoundedCornerShape(20.dp),
    /** Строка списка / таблицы с выделением. */
    val row: Shape = RoundedCornerShape(26.dp),
    /** Компактная строка (список клиентов) и плашка внутри модалки. */
    val rowCompact: Shape = RoundedCornerShape(22.dp),
    /** Строка списка занятий рядом с деталями (05–07). */
    val rowLarge: Shape = RoundedCornerShape(24.dp),
)
