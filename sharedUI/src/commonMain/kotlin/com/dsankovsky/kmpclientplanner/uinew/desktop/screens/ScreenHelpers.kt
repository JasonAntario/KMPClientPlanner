package com.dsankovsky.kmpclientplanner.uinew.desktop.screens

import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.isoDayNumber

fun ServicesFilter.toLabel(): String = when (this) {
    ServicesFilter.TODAY -> "Сегодня"
    ServicesFilter.TOMORROW -> "Завтра"
    ServicesFilter.CURRENT_WEEK -> "Неделя"
    ServicesFilter.NEXT_WEEK -> "След. неделя"
    ServicesFilter.CURRENT_MONTH -> "Месяц"
    ServicesFilter.NEXT_MONTH -> "След. месяц"
    ServicesFilter.CUSTOM_INTERVAL -> "Период"
}

/** Two-digit start time, e.g. "12:30". */
fun LocalDateTime.startLabel(): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

/** Duration between start and end as "90м" / "1ч 30м". */
fun durationLabel(start: LocalDateTime, end: LocalDateTime): String {
    val minutes = (end.hour * 60 + end.minute) - (start.hour * 60 + start.minute)
    val safe = if (minutes < 0) minutes + 24 * 60 else minutes
    return when {
        safe < 60 -> "${safe}м"
        safe % 60 == 0 -> "${safe / 60}ч"
        else -> "${safe / 60}ч ${safe % 60}м"
    }
}

/** Up-to-two-letter uppercase initials derived from a full name. */
fun initialsOf(fullName: String): String {
    val parts = fullName.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}

private val ruWeekdaysShort = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
private val ruMonthsGenitive = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)

/** Russian short date label like "Чт, 19 июня". */
fun ruDateLabel(date: LocalDate): String {
    val weekday = ruWeekdaysShort[(date.dayOfWeek.isoDayNumber - 1).coerceIn(0, 6)]
    val month = ruMonthsGenitive[(date.monthNumber - 1).coerceIn(0, 11)]
    return "$weekday, ${date.dayOfMonth} $month"
}

/** Correct Russian plural for "занятие": 1 занятие, 2 занятия, 5 занятий. */
fun lessonsPlural(count: Int): String {
    val mod100 = count % 100
    val mod10 = count % 10
    val word = when {
        mod100 in 11..14 -> "занятий"
        mod10 == 1 -> "занятие"
        mod10 in 2..4 -> "занятия"
        else -> "занятий"
    }
    return "$count $word"
}

fun BaseService.priceLabel(): String? = price?.let { p ->
    val rounded = if (p % 1f == 0f) p.toInt().toString() else p.toString()
    "$rounded ${currency.code}"
}
