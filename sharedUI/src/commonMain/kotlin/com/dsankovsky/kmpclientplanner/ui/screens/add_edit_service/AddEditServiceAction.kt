package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service

import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed interface AddEditServiceAction {

    data class LoadServiceData(val serviceId: Long?) : AddEditServiceAction
    data class OnTitleChanged(val title: String) : AddEditServiceAction
    data class OnClientChanged(val client: BaseClient) : AddEditServiceAction
    data class OnCommentChanged(val comment: String) : AddEditServiceAction
    data class OnTimeChanged(val time: LocalTime, val source: TimeSource) : AddEditServiceAction
    data class OnDateChanged(val date: LocalDate, val source: DateSource) : AddEditServiceAction
    data class OnAddressChanged(val address: String) : AddEditServiceAction
    data class OnSaveServiceClicked(
        val title: String,
        val address: String,
        val price: String,
        val comment: String
    ) : AddEditServiceAction

    data object OnCloseScreenClicked : AddEditServiceAction
    data class OnPaidStatusChanged(val isPaid: Boolean) : AddEditServiceAction
    data class OnFinishedStatusChanged(val isFinished: Boolean) : AddEditServiceAction
    data class OnPriceChanged(val price: String) : AddEditServiceAction
    data class OnCurrencyChanged(val currency: CurrencyItem) : AddEditServiceAction

    data object OnSaveServiceConfirmed : AddEditServiceAction

    data object OnDeleteService : AddEditServiceAction
    data object OnDeleteServiceConfirmed : AddEditServiceAction

    data object OnDialogDismissed : AddEditServiceAction

    data object EducationServiceAction {
        data class OnFormatChanged(val isOnline: Boolean) : AddEditServiceAction
    }

    data object SportServiceAction {
        data class OnFormatChanged(val isOnline: Boolean) : AddEditServiceAction
    }
}

enum class DateSource {
    BASE_START_DATE,
    BASE_END_DATE
}

enum class TimeSource {
    BASE_START_TIME,
    BASE_END_TIME
}

sealed interface AddEditServiceEvent {
    data object OnDismissClicked : AddEditServiceEvent
    data object OnServiceDeleted : AddEditServiceEvent
    data object OnServiceSaved : AddEditServiceEvent
}