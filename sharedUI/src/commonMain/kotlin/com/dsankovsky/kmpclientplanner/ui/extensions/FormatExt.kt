package com.dsankovsky.kmpclientplanner.ui.extensions

import androidx.compose.runtime.Composable
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.datetime_friday
import kmpclientplanner.sharedui.generated.resources.datetime_friday_short
import kmpclientplanner.sharedui.generated.resources.datetime_monday
import kmpclientplanner.sharedui.generated.resources.datetime_monday_short
import kmpclientplanner.sharedui.generated.resources.datetime_saturday
import kmpclientplanner.sharedui.generated.resources.datetime_saturday_short
import kmpclientplanner.sharedui.generated.resources.datetime_sunday
import kmpclientplanner.sharedui.generated.resources.datetime_sunday_short
import kmpclientplanner.sharedui.generated.resources.datetime_thursday
import kmpclientplanner.sharedui.generated.resources.datetime_thursday_short
import kmpclientplanner.sharedui.generated.resources.datetime_tuesday
import kmpclientplanner.sharedui.generated.resources.datetime_tuesday_short
import kmpclientplanner.sharedui.generated.resources.datetime_wednesday
import kmpclientplanner.sharedui.generated.resources.datetime_wednesday_short
import kmpclientplanner.sharedui.generated.resources.months_april_relative
import kmpclientplanner.sharedui.generated.resources.months_august_relative
import kmpclientplanner.sharedui.generated.resources.months_december_relative
import kmpclientplanner.sharedui.generated.resources.months_february_relative
import kmpclientplanner.sharedui.generated.resources.months_january_relative
import kmpclientplanner.sharedui.generated.resources.months_july_relative
import kmpclientplanner.sharedui.generated.resources.months_june_relative
import kmpclientplanner.sharedui.generated.resources.months_march_relative
import kmpclientplanner.sharedui.generated.resources.months_may_relative
import kmpclientplanner.sharedui.generated.resources.months_november_relative
import kmpclientplanner.sharedui.generated.resources.months_october_relative
import kmpclientplanner.sharedui.generated.resources.months_september_relative
import kotlin.math.roundToLong
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.stringResource

/** «Среда» — раньше в шапке дня печаталось имя enum'а (`WEDNESDAY`). */
@Composable
fun DayOfWeek.toUIName(): String = stringResource(
    when (this) {
        DayOfWeek.MONDAY -> Res.string.datetime_monday
        DayOfWeek.TUESDAY -> Res.string.datetime_tuesday
        DayOfWeek.WEDNESDAY -> Res.string.datetime_wednesday
        DayOfWeek.THURSDAY -> Res.string.datetime_thursday
        DayOfWeek.FRIDAY -> Res.string.datetime_friday
        DayOfWeek.SATURDAY -> Res.string.datetime_saturday
        else -> Res.string.datetime_sunday
    },
)

/** «Пн» — расписание клиента в теге, где полное название не поместится. */
@Composable
fun DayOfWeek.toUIShortName(): String = stringResource(
    when (this) {
        DayOfWeek.MONDAY -> Res.string.datetime_monday_short
        DayOfWeek.TUESDAY -> Res.string.datetime_tuesday_short
        DayOfWeek.WEDNESDAY -> Res.string.datetime_wednesday_short
        DayOfWeek.THURSDAY -> Res.string.datetime_thursday_short
        DayOfWeek.FRIDAY -> Res.string.datetime_friday_short
        DayOfWeek.SATURDAY -> Res.string.datetime_saturday_short
        else -> Res.string.datetime_sunday_short
    },
)

/** «июля» — родительный падеж для дат вида «29 июля». */
@Composable
fun Month.toUIRelativeName(): String = stringResource(
    when (this) {
        Month.JANUARY -> Res.string.months_january_relative
        Month.FEBRUARY -> Res.string.months_february_relative
        Month.MARCH -> Res.string.months_march_relative
        Month.APRIL -> Res.string.months_april_relative
        Month.MAY -> Res.string.months_may_relative
        Month.JUNE -> Res.string.months_june_relative
        Month.JULY -> Res.string.months_july_relative
        Month.AUGUST -> Res.string.months_august_relative
        Month.SEPTEMBER -> Res.string.months_september_relative
        Month.OCTOBER -> Res.string.months_october_relative
        Month.NOVEMBER -> Res.string.months_november_relative
        else -> Res.string.months_december_relative
    },
).lowercase()

/** «29 июля» — так подписаны дни в ленте занятий и в деталях. */
@Composable
fun LocalDate.toUIDayAndMonth(): String = "$day ${month.toUIRelativeName()}"

/** «Среда, 29 июля» — шапка группы в ленте. */
@Composable
fun LocalDate.toUIWeekdayAndDate(): String = "${dayOfWeek.toUIName()}, ${toUIDayAndMonth()}"

/**
 * «40,00 BYN». Своё форматирование, потому что в common-коде нет `String.format`,
 * а суммы в макете всегда с двумя знаками и запятой.
 */
fun Float?.toUIMoney(currency: CurrencyItem): String = "${toUIAmount()} ${currency.code}"

/** «40,00» — сумма без кода валюты: в таблице статистики валюта вынесена в шапку колонки. */
fun Float?.toUIAmount(): String {
    val cents = ((this ?: 0f) * 100).roundToLong()
    val whole = cents / 100
    val fraction = (cents % 100).toInt().toTwoNumberString()
    return "$whole,$fraction"
}
