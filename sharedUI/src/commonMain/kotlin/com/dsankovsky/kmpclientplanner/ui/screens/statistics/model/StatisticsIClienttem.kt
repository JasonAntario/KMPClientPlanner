package com.dsankovsky.kmpclientplanner.ui.screens.statistics.model

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient

@Immutable
data class StatisticsClientItem(
    val client: BaseClient,
    val income: List<StatisticsPaymentItem>,
    val mustBePaid: List<StatisticsPaymentItem>,
    /** Сколько занятий клиента оплачено — колонка «Занятий оплачено» на экране 09. */
    val paidServicesCount: Int = 0
) {
    @Immutable
    data class StatisticsPaymentItem(
        val money: Float,
        val currency: CurrencyItem
    )
}
