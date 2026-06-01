package com.dsankovsky.kmpclientplanner.domain.models.additional

import com.dsankovsky.kmpclientplanner.extensions.getCurrentDateTime
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

enum class ServicesFilter {
    DAY,
    NEXT_WEEK,
    CURRENT_WEEK,
    CURRENT_MONTH,
    NEXT_MONTH,
    TODAY,
    TOMORROW,
    YEAR,
    ALL_TIME
}

fun getStatisticsScreenFilters() = listOf(
    ServicesFilter.DAY,
    ServicesFilter.CURRENT_WEEK,
    ServicesFilter.CURRENT_MONTH,
    ServicesFilter.YEAR,
    ServicesFilter.ALL_TIME
)

fun getHomeScreenFilters() = listOf(
    ServicesFilter.TODAY,
    ServicesFilter.TOMORROW,
    ServicesFilter.NEXT_WEEK,
    ServicesFilter.NEXT_MONTH
)

fun ServicesFilter.getDateInterval(): Pair<LocalDate, LocalDate>? {
    return when (this) {
        ServicesFilter.DAY, ServicesFilter.TODAY -> {
            val now = getCurrentDateTime().date
            Pair(now, now)
        }

        ServicesFilter.NEXT_WEEK -> {
            val start = getCurrentDateTime().date
            val end = start.plus(DatePeriod(days = 7))
            Pair(start, end)
        }

        ServicesFilter.CURRENT_MONTH -> {
            val now = getCurrentDateTime().date
            Pair(now, now)
        }

        ServicesFilter.NEXT_MONTH -> {
            val start = getCurrentDateTime().date
            val end = start.plus(DatePeriod(months = 1))
            Pair(start, end)
        }


        ServicesFilter.TOMORROW -> {
            val tomorrow = getCurrentDateTime().date.plus(DatePeriod(days = 1))
            Pair(tomorrow, tomorrow)
        }

        ServicesFilter.CURRENT_WEEK -> {
            val now = getCurrentDateTime().date
            val compDayOfWeekNumber = now.dayOfWeek.isoDayNumber
            val endDate = now.plus(DatePeriod(days = (7 - compDayOfWeekNumber)))
            val startDate = now.minus(DatePeriod(days = compDayOfWeekNumber - 1))
            Pair(startDate, endDate)
        }

        ServicesFilter.YEAR -> {
            val now = getCurrentDateTime().date
            Pair(now, now)
        }

        else -> null
    }
}