package com.dsankovsky.kmpclientplanner.ui.screens.pay_services

import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient

sealed interface PayServiceScreenAction {
    data object LoadData : PayServiceScreenAction
    data object OnBackClicked : PayServiceScreenAction
    data class OnChangeClientCLicked(val client: BaseClient) : PayServiceScreenAction
    data class OnServicesAmountChanged(val amount: String) : PayServiceScreenAction
    data object OnPayClicked : PayServiceScreenAction
}

sealed interface PayServiceScreenEvent {
    data object OnDismissClicked : PayServiceScreenEvent
    data object OnSuccess : PayServiceScreenEvent
}