package com.dsankovsky.kmpclientplanner.ui.screens.service_details


sealed interface ServiceDetailsScreenAction {

    data class LoadData(val serviceId: Long) : ServiceDetailsScreenAction

    data object OnCloseScreenClicked : ServiceDetailsScreenAction
    data object OnEditServiceClicked : ServiceDetailsScreenAction

    data object OnUpdateDataClicked : ServiceDetailsScreenAction

    data object OnPaidStatusChanged : ServiceDetailsScreenAction
    data object OnFinishStatusChanged : ServiceDetailsScreenAction

    data object EducationServiceAction {
        data class OnHomeworkChanged(val homework: String) : ServiceDetailsScreenAction
    }

    data object BeautyServiceAction {
        data class OnImagesAdded(val imageUris: List<String>) : ServiceDetailsScreenAction
        data class OnImageDeleteClicked(val imageListIndex: Int) : ServiceDetailsScreenAction
    }

    data object TattooServiceAction {
        data class OnImagesAdded(val imageUris: List<String>) : ServiceDetailsScreenAction
        data class OnImageDeleteClicked(val imageListIndex: Int) : ServiceDetailsScreenAction
    }

    data object SportServiceAction {
        data object OnAddExerciseClicked : ServiceDetailsScreenAction
        data class OnDeleteExerciseClicked(val exerciseIndex: Int) : ServiceDetailsScreenAction
        data class OnExerciseTitleChanged(
            val exerciseIndex: Int,
            val title: String,
            val isSelectable: Boolean
        ) : ServiceDetailsScreenAction

        data class OnAddSetClicked(val exerciseIndex: Int) : ServiceDetailsScreenAction
        data class OnDeleteSetClicked(
            val exerciseIndex: Int,
            val setIndex: Int
        ) : ServiceDetailsScreenAction

        data class OnSetRepeatsChanged(
            val exerciseIndex: Int,
            val setIndex: Int,
            val repeats: String
        ) : ServiceDetailsScreenAction

        data class OnSetWeightChanged(
            val exerciseIndex: Int,
            val setIndex: Int,
            val weight: String
        ) : ServiceDetailsScreenAction
    }
}

sealed interface ServiceDetailsScreenEvent {

    data object OnCloseScreen : ServiceDetailsScreenEvent
    data object OpenEditServiceScreen : ServiceDetailsScreenEvent
    data object StatusUpdated : ServiceDetailsScreenEvent
}