package com.dsankovsky.kmpclientplanner.ui.screens.client_details


sealed interface ClientDetailsActions {
    data class LoadData(val clientId: Long) : ClientDetailsActions
    data object OnCloseScreenClicked : ClientDetailsActions
    data object OnEditClientClicked : ClientDetailsActions
    data object OnUpdateDataClicked : ClientDetailsActions
    data object FillServicesClicked : ClientDetailsActions

    data object OnAutofillConfirmClicked : ClientDetailsActions
    data object OnAutofillWithCrossingConfirmClicked : ClientDetailsActions
    data object OnAutofillDismissClicked : ClientDetailsActions
    data object CloseClientDialog : ClientDetailsActions

    data object ShowServicesHistory : ClientDetailsActions

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
    data object ClientsDataUpdated : ClientDetailsEvents
    data object AutofillCompleted : ClientDetailsEvents
    data object OpenServicesHistory : ClientDetailsEvents
}