package com.dsankovsky.kmpclientplanner.ui.screens.pay_services

sealed interface PayServiceScreenAction {
    /** @param clientId предвыбранный клиент; задан при открытии из карточки клиента. */
    data class LoadData(val clientId: Long? = null) : PayServiceScreenAction
    data object OnCloseClicked : PayServiceScreenAction
    data class OnClientSelected(val clientId: Long) : PayServiceScreenAction

    /** Степпер: сколько самых ранних неоплаченных занятий закрыть. */
    data class OnAmountChanged(val amount: Int) : PayServiceScreenAction
    data object OnPayClicked : PayServiceScreenAction
}

sealed interface PayServiceScreenEvent {
    data object OnDismissClicked : PayServiceScreenEvent
    data object OnSuccess : PayServiceScreenEvent
}
