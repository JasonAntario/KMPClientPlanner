package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client

import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

sealed interface AddEditClientAction {

    data class LoadClientData(val clientId: Long?) : AddEditClientAction
    data class OnNameChanged(val name: String) : AddEditClientAction
    data class OnSurnameChanged(val surname: String) : AddEditClientAction
    data class OnAddressChanged(val address: String) : AddEditClientAction
    data class OnPhoneChanged(val phone: String) : AddEditClientAction
    data class OnPriceChanged(val price: String) : AddEditClientAction
    data class OnCurrencyChanged(val currency: CurrencyItem) : AddEditClientAction
    data class OnCurrencyMenuExpandedChange(val isExpanded: Boolean) : AddEditClientAction
    data class OnCommentChanged(val comment: String) : AddEditClientAction
    data object OnDeleteClient : AddEditClientAction
    data object OnDeleteClientConfirmed : AddEditClientAction
    data class OnClientSaveClicked(
        val name: String,
        val surname: String,
        val comment: String,
        val address: String,
        val phone: String,
        val price: String
    ) : AddEditClientAction

    data object OnCloseScreenClicked : AddEditClientAction

    data object CloseClientDialog : AddEditClientAction

    data object OnAutofillConfirmClicked : AddEditClientAction
    data object OnAutofillWithCrossingConfirmClicked : AddEditClientAction
    data object OnAutofillDismissClicked : AddEditClientAction

    data object EducationClientAction {
        data class OnLevelChanged(val level: String) : AddEditClientAction
        data class OnFormatChanged(val isOnline: Boolean) : AddEditClientAction
        data object OnAddNewServiceTime : AddEditClientAction
        data class OnDayOfWeekChanged(
            val itemIndex: Int,
            val dayOfWeek: DayOfWeek
        ) : AddEditClientAction

        data class OnTimeChanged(val itemIndex: Int, val time: LocalTime) : AddEditClientAction

        data class OnDurationChanged(val itemIndex: Int, val duration: String) : AddEditClientAction
        data class OnDeleteLessonClicked(val lessonIndex: Int) : AddEditClientAction
    }

    data object SportClientAction {
        data class OnWeightChanged(val weight: String) : AddEditClientAction
        data class OnFormatChanged(val isOnline: Boolean) : AddEditClientAction
        data object OnAddNewServiceTime : AddEditClientAction
        data class OnDayOfWeekChanged(
            val itemIndex: Int,
            val dayOfWeek: DayOfWeek
        ) : AddEditClientAction

        data class OnTimeChanged(val itemIndex: Int, val time: LocalTime) : AddEditClientAction
        data class OnDurationChanged(val itemIndex: Int, val duration: String) : AddEditClientAction
        data class OnDeleteTrainingClicked(val trainingIndex: Int) : AddEditClientAction
    }
}

sealed interface AddEditClientEvent {
    data object OnDismissClicked : AddEditClientEvent
    data class OnClientDeleted(val noClients: Boolean) : AddEditClientEvent
    data object OnClientSaved : AddEditClientEvent
    data object AutofillCompleted : AddEditClientEvent
}