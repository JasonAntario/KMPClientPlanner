package com.dsankovsky.kmpclientplanner.ui.screens.statistics

import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter

sealed interface StatisticsScreenAction {

    data object LoadData : StatisticsScreenAction
    data class OnFilterClicked(val filter: ServicesFilter) : StatisticsScreenAction

    data object OpenDatePickerClicked : StatisticsScreenAction
    data object CloseDatePickerClicked : StatisticsScreenAction
}