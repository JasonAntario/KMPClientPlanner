package com.dsankovsky.kmpclientplanner.domain.models.additional

import com.dsankovsky.kmpclientplanner.extensions.getCurrentDateTime
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

enum class ServicesFilter {
    TODAY,
    TOMORROW,
    CURRENT_WEEK,
    NEXT_WEEK,
    CURRENT_MONTH,
    NEXT_MONTH,
    CUSTOM_INTERVAL
}

fun getStatisticsScreenFilters() = listOf(
    ServicesFilter.TODAY,
    ServicesFilter.TOMORROW,
    ServicesFilter.CURRENT_WEEK,
    ServicesFilter.NEXT_WEEK,
    ServicesFilter.CURRENT_MONTH,
    ServicesFilter.NEXT_MONTH,
    ServicesFilter.CUSTOM_INTERVAL
)

fun getHomeScreenFilters() = listOf(
    ServicesFilter.TODAY,
    ServicesFilter.TOMORROW,
    ServicesFilter.CURRENT_WEEK,
    ServicesFilter.NEXT_WEEK,
    ServicesFilter.CURRENT_MONTH,
    ServicesFilter.NEXT_MONTH
)

fun ServicesFilter.getDateInterval(): Pair<LocalDate, LocalDate>? {
    return when (this) {
        ServicesFilter.TODAY -> {
            val today = getCurrentDateTime().date
            Pair(today, today)
        }

        ServicesFilter.TOMORROW -> {
            val tomorrow = getCurrentDateTime().date.plus(DatePeriod(days = 1))
            Pair(tomorrow, tomorrow)
        }

        ServicesFilter.CURRENT_WEEK -> {
            val today = getCurrentDateTime().date
            val dayOfWeek = today.dayOfWeek.isoDayNumber // 1=Mon, 7=Sun
            val monday = today.minus(DatePeriod(days = dayOfWeek - 1))
            val sunday = monday.plus(DatePeriod(days = 6))
            Pair(monday, sunday)
        }

        ServicesFilter.NEXT_WEEK -> {
            val today = getCurrentDateTime().date
            val dayOfWeek = today.dayOfWeek.isoDayNumber // 1=Mon, 7=Sun
            val nextMonday = today.plus(DatePeriod(days = 8 - dayOfWeek))
            val nextSunday = nextMonday.plus(DatePeriod(days = 6))
            Pair(nextMonday, nextSunday)
        }

        ServicesFilter.CURRENT_MONTH -> {
            val today = getCurrentDateTime().date
            val firstDay = LocalDate(today.year, today.month, 1)
            val lastDay = firstDay.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
            Pair(firstDay, lastDay)
        }

        ServicesFilter.NEXT_MONTH -> {
            val today = getCurrentDateTime().date
            val firstDay = LocalDate(today.year, today.month, 1).plus(DatePeriod(months = 1))
            val lastDay = firstDay.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
            Pair(firstDay, lastDay)
        }

        ServicesFilter.CUSTOM_INTERVAL -> null
    }
}
