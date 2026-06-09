package com.dsankovsky.kmpclientplanner.ui.screens.statistics

import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import kotlinx.datetime.LocalDate

sealed interface StatisticsScreenAction {

    data object LoadData : StatisticsScreenAction
    data class OnFilterClicked(val filter: ServicesFilter) : StatisticsScreenAction

    data object CloseDatePickerClicked : StatisticsScreenAction
    data class SetCustomInterval(val startDate: LocalDate, val endDate: LocalDate) : StatisticsScreenAction
}
