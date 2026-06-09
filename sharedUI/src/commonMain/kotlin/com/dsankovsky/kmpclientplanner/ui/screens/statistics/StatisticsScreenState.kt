package com.dsankovsky.kmpclientplanner.ui.screens.statistics

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.additional.getStatisticsScreenFilters
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.model.StatisticsClientItem
import kotlinx.datetime.LocalDate

@Immutable
data class StatisticsScreenState(
    val isLoading: Boolean = true,
    val paidPercentage: Float = 0f,
    val receivedTotal: Float = 0f,
    val expectedTotal: Float = 0f,
    val receivedTotalByCurrency: List<StatisticsClientItem.StatisticsPaymentItem> = emptyList(),
    val expectedTotalByCurrency: List<StatisticsClientItem.StatisticsPaymentItem> = emptyList(),
    val itemsByClients: List<StatisticsClientItem> = emptyList(),
    val filters: List<ServicesFilter> = getStatisticsScreenFilters(),
    val currentFilter: ServicesFilter = ServicesFilter.TODAY,
    val showDatePicker: Boolean = false,
    val dateInterval: Pair<LocalDate, LocalDate>? = null,
    val customIntervalStart: LocalDate? = null,
    val customIntervalEnd: LocalDate? = null
)
