package com.dsankovsky.kmpclientplanner.ui.screens.service_details


sealed interface ServiceDetailsScreenAction {

    data class LoadData(val serviceId: Long) : ServiceDetailsScreenAction

    data object OnEditServiceClicked : ServiceDetailsScreenAction
    data object OnDeleteServiceClicked : ServiceDetailsScreenAction
    data object OnDeleteServiceConfirmed : ServiceDetailsScreenAction
    data object CloseDialog : ServiceDetailsScreenAction

    data object OnPaidStatusChanged : ServiceDetailsScreenAction
    data object OnFinishStatusChanged : ServiceDetailsScreenAction

    data object EducationServiceAction {
        /** Домашнее задание сохраняется само: кнопки «Обновить данные» в новом дизайне нет. */
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
        /** «Новое упражнение» (М11): подходы одинаковые, значения из формы. */
        data class OnExerciseAdded(
            val title: String,
            val setsCount: Int,
            val repeats: String,
            val weight: String,
        ) : ServiceDetailsScreenAction

        data class OnDeleteExerciseClicked(val exerciseIndex: Int) : ServiceDetailsScreenAction

        data object OnNewExerciseClicked : ServiceDetailsScreenAction
        data object OnPickExerciseClicked : ServiceDetailsScreenAction
        data class OnKnownExercisePicked(val exercise: KnownExercise) : ServiceDetailsScreenAction
    }
}

sealed interface ServiceDetailsScreenEvent {

    data object OnCloseScreen : ServiceDetailsScreenEvent
    data object OpenEditServiceScreen : ServiceDetailsScreenEvent
    data object StatusUpdated : ServiceDetailsScreenEvent
    data object ServiceDeleted : ServiceDetailsScreenEvent
}
