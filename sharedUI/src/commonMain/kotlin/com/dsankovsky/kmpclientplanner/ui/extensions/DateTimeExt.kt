@file:OptIn(ExperimentalTime::class)

package com.dsankovsky.kmpclientplanner.ui.extensions

import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun Long.toLocalDateTime() = Instant.fromEpochMilliseconds(this)
    .toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDateTime.toEpochMilliseconds() =
    this.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

fun LocalDateTime.toTime(): String {
    val formattedMinutes = minute.toTwoNumberString()
    return "$hour:$formattedMinutes"
}

fun getCurrentDateTime(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun getStartDateTime(): LocalDateTime {
    val now = getCurrentDateTime()
    return LocalDateTime(date = now.date, time = LocalTime(12, 0))
}

fun LocalDateTime.addHours(hours: Int): LocalDateTime {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    return instant.plus(hours, DateTimeUnit.HOUR).toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDateTime.addHours(hours: Float): LocalDateTime {
    val minutes = (60 * hours).roundToInt()
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    return instant.plus(minutes, DateTimeUnit.MINUTE)
        .toLocalDateTime(TimeZone.currentSystemDefault())
}

fun getDaysOfWeekList() = DayOfWeek.entries
fun getDayOfWeek(date: LocalDate): DayOfWeek =
    DayOfWeek.entries.first { it.isoDayNumber == date.dayOfWeek.isoDayNumber }

fun getDayOfWeek(isoIndex: Int): DayOfWeek =
    getDaysOfWeekList().first { it.isoDayNumber == isoIndex }

fun getCurrentDayOfWeek(): DayOfWeek {
    val date = getCurrentDateTime().date
    return getDayOfWeek(date)
}

fun getMonth(date: LocalDate): Month =
    Month.entries.first { it.number == date.month.number }

fun LocalDate.getNextClosestDate(dayOfWeekIndex: Int): LocalDate {
    val currentDayOfWeek = this.dayOfWeek.isoDayNumber
    val days = abs(currentDayOfWeek - dayOfWeekIndex)
    return if (days == 0) {
        this.plus(7, DateTimeUnit.DAY)
    } else {
        this.plus(days, DateTimeUnit.DAY)
    }
}

fun List<ServiceDateTime>.calculateNextDateTime(
    startDateTime: LocalDateTime = getCurrentDateTime(),
    repeat: Int = 4
): List<Pair<LocalDateTime, LocalDateTime>> {
    val serviceDateTimeList = this
    val daysList = mutableListOf<Pair<LocalDateTime, LocalDateTime>>()

    serviceDateTimeList.forEach { sdt ->
        val closestNextDate = startDateTime.date.getNextClosestDate(sdt.dayOfWeek.isoDayNumber)
        repeat(repeat) {
            val startDateTime = LocalDateTime(
                date = closestNextDate.plus(7 * it, DateTimeUnit.DAY),
                time = sdt.time
            )
            val endDateTime = startDateTime.addHours(sdt.duration.toFloat())
            daysList.add(Pair(startDateTime, endDateTime))
        }
    }
    return daysList
}

fun LocalTime.toUITime(): String {
    val hours = this.hour.toTwoNumberString()
    val minutes = this.minute.toTwoNumberString()
    return "$hours:$minutes"
}

fun Int.toTwoNumberString(): String {
    return if (this < 10) "0$this" else this.toString()
}

fun LocalDate.toUIDate(): String {
    val day = day.toTwoNumberString()
    val month = month.number.toTwoNumberString()
    val year = this.year
    return "$day.$month.$year"
}