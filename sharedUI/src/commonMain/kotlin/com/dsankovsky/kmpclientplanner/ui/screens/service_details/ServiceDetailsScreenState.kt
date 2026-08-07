package com.dsankovsky.kmpclientplanner.ui.screens.service_details

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * Состояние деталей услуги (экраны 05–07 — правая панель главного экрана).
 *
 * @param exerciseRows строки таблицы тренировки: текущие значения плюс прошлое выполнение
 *   того же упражнения (колонка «Прошлый раз» и дельта веса на 06)
 * @param knownExercises упражнения клиента из прошлых занятий — «Выбрать из созданных» в М11
 * @param referenceImages изображения активного проекта клиента: на 07 они подставляются
 *   в карусель референсов автоматически
 */
@Immutable
data class ServiceDetailsScreenState(
    val isLoading: Boolean = true,
    val title: String = "",
    val date: LocalDate = getCurrentDateTime().date,
    val startDateTime: LocalDateTime = getCurrentDateTime(),
    val endDateTime: LocalDateTime = getCurrentDateTime(),
    val time: String = "",
    val clientName: String = "",
    val address: String? = null,
    val isOnline: Boolean = false,
    val isPaid: Boolean = false,
    val isFinished: Boolean = false,
    val comment: String? = null,
    val service: BaseService = BaseService(),
    val serviceSpecificFields: ServiceSpecificFields? = null,
    val exerciseRows: List<ExerciseRow> = emptyList(),
    val knownExercises: List<KnownExercise> = emptyList(),
    val referenceImages: List<String> = emptyList(),
    val dialog: ServiceDetailsDialog? = null,
)

/** Строка таблицы «Тренировка» на 06. */
@Immutable
data class ExerciseRow(
    val title: String,
    val setsCount: Int,
    val repeats: String,
    val weight: String,
    val previousSetsCount: Int? = null,
    val previousRepeats: String? = null,
    val previousWeight: String? = null,
    /** Разница веса с прошлым выполнением; `null` — сравнивать нечего. */
    val weightDelta: Float? = null,
)

/** Упражнение из прошлых занятий клиента с последними значениями — для подстановки в М11. */
@Immutable
data class KnownExercise(
    val title: String,
    val setsCount: Int,
    val repeats: String,
    val weight: String,
)

sealed interface ServiceDetailsDialog {
    /** М8 — удаление услуги. */
    data object ConfirmDeleting : ServiceDetailsDialog

    /** М11 — новое упражнение; `prefill` приходит из «Выбрать из созданных». */
    data class NewExercise(val prefill: KnownExercise? = null) : ServiceDetailsDialog

    /** Выбор упражнения из созданных ранее. */
    data object PickExercise : ServiceDetailsDialog
}
