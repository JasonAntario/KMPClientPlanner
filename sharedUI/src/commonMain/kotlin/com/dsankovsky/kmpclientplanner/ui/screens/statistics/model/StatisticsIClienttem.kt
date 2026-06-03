package com.dsankovsky.kmpclientplanner.ui.screens.statistics.model

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient

@Immutable
data class StatisticsClientItem(
    val client: BaseClient,
    val income: List<StatisticsPaymentItem>,
    val mustBePaid: List<StatisticsPaymentItem>
) {
    data class StatisticsPaymentItem(
        val money: Float,
        val currency: CurrencyItem
    )
}
