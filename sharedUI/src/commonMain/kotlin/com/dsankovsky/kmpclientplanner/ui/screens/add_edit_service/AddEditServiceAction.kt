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
    data class OnAddressChanged(val address: String) : AddEditServiceAction
    data class OnPriceChanged(val price: String) : AddEditServiceAction
    data class OnCurrencyChanged(val currency: CurrencyItem) : AddEditServiceAction

    /** Дата и время приходят из пикеров, поэтому уже разобранные. */
    data class OnDateChanged(val date: LocalDate) : AddEditServiceAction
    data class OnTimeChanged(val time: LocalTime) : AddEditServiceAction

    /** Длительность в минутах — обычное числовое поле, поэтому текстом. */
    data class OnDurationChanged(val minutes: String) : AddEditServiceAction

    /** Значения полей уже в состоянии, поэтому сохранению нечего передавать. */
    data object OnSaveServiceClicked : AddEditServiceAction
    data object OnSaveServiceConfirmed : AddEditServiceAction

    /** «Перенести все» в вопросе про остальные занятия клиента. */
    data object OnShiftFutureServicesConfirmed : AddEditServiceAction

    /** «Только это занятие»: сохраняем правку, остальные занятия не трогаем. */
    data object OnShiftFutureServicesDeclined : AddEditServiceAction

    data class OnPaidStatusChanged(val isPaid: Boolean) : AddEditServiceAction
    data class OnFinishedStatusChanged(val isFinished: Boolean) : AddEditServiceAction

    data object OnDeleteService : AddEditServiceAction
    data object OnDeleteServiceConfirmed : AddEditServiceAction

    /** Крестик, «Отмена», Esc и клик мимо: с правками сначала спросит М9. */
    data object OnCloseRequested : AddEditServiceAction
    data object OnCloseScreenClicked : AddEditServiceAction

    data object OnDialogDismissed : AddEditServiceAction

    data object EducationServiceAction {
        data class OnFormatChanged(val isOnline: Boolean) : AddEditServiceAction
        data class OnHomeworkChanged(val homework: String) : AddEditServiceAction
    }

    data object SportServiceAction {
        data class OnFormatChanged(val isOnline: Boolean) : AddEditServiceAction
    }
}

sealed interface AddEditServiceEvent {
    data object OnDismissClicked : AddEditServiceEvent
    data object OnServiceDeleted : AddEditServiceEvent
    data object OnServiceSaved : AddEditServiceEvent

    /** Занятие сохранено, и вместе с ним перенесены остальные занятия слота. */
    data class OnFutureServicesShifted(val count: Int) : AddEditServiceEvent

    /** Занятие сохранено, а перенести остальные не удалось. */
    data object OnFutureServicesShiftFailed : AddEditServiceEvent
}
