package com.dsankovsky.kmpclientplanner.ui.screens.pay_services

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient

@Immutable
data class PayServiceScreenState(
    val clientsList: List<BaseClient> = emptyList(),
    val isPaymentReady: Boolean = false,
    val client: BaseClient? = null,
    val availableServices: Int = 0,
    val servicesAmount: String = "0"
)
