package com.dsankovsky.kmpclientplanner.ui.screens.client_details


sealed interface ClientDetailsActions {
    data class LoadData(val clientId: Long) : ClientDetailsActions
    data object OnCloseScreenClicked : ClientDetailsActions
    data object OnEditClientClicked : ClientDetailsActions
    data object FillServicesClicked : ClientDetailsActions
    data object OnAutofillConfirmClicked : ClientDetailsActions
    data object OnAutofillWithCrossingConfirmClicked : ClientDetailsActions
    data object OnAutofillDismissClicked : ClientDetailsActions
    data object CloseClientDialog : ClientDetailsActions

    data object ShowServicesHistory : ClientDetailsActions

    /** Кнопка «Предоплата»: та же М5, но клиент уже выбран. */
    data object OnPrepayClicked : ClientDetailsActions

    /** Корзина в шапке карточки: сначала подтверждение (М8), потом удаление. */
    data object OnDeleteClientClicked : ClientDetailsActions
    data object OnDeleteClientConfirmed : ClientDetailsActions

    /**
     * Фото проектов тату-клиента. UI для них живёт на экране деталей услуги (07) —
     * он ещё не переписан, поэтому пока эти действия только меняют состояние.
     */
    data object TattooClientAction {
        data class OnImagesAdded(val imageUris: List<String>) : ClientDetailsActions
        data class OnImagesAddedInFinishedProject(
            val imageUris: List<String>,
            val projectIndex: Int
        ) : ClientDetailsActions

        data class OnImageDeleteClicked(val imageListIndex: Int) : ClientDetailsActions
        data class OnImageDeleteClickedInFinishedProject(
            val imageListIndex: Int,
            val projectIndex: Int
        ) : ClientDetailsActions

        data object OnFinishProjectClicked : ClientDetailsActions
    }

}

sealed interface ClientDetailsEvents {
    data object OnCloseScreen : ClientDetailsEvents
    data object OpenEditClientScreen : ClientDetailsEvents
    data object AutofillCompleted : ClientDetailsEvents
    data object OpenServicesHistory : ClientDetailsEvents
    data object OpenPrepay : ClientDetailsEvents
    data object ClientDeleted : ClientDetailsEvents
}
