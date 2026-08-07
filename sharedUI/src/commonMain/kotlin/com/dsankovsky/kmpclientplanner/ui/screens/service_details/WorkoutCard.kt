package com.dsankovsky.kmpclientplanner.ui.screens.service_details

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonDefaults
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicCard
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicField
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicIconButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalActions
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHeader
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalPanel
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalWidth
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableCell
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableHeader
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableHeaderCell
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableRow
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTextField
import com.dsankovsky.kmpclientplanner.ui.design.components.PreviewSurface
import com.dsankovsky.kmpclientplanner.ui.design.components.Tag
import com.dsankovsky.kmpclientplanner.ui.design.components.TagDefaults
import com.dsankovsky.kmpclientplanner.ui.design.elevationSm
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIAmount
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.client_weight_kg
import kmpclientplanner.sharedui.generated.resources.service_exercise
import kmpclientplanner.sharedui.generated.resources.service_exercise_add
import kmpclientplanner.sharedui.generated.resources.service_exercise_delete
import kmpclientplanner.sharedui.generated.resources.service_exercise_last_time
import kmpclientplanner.sharedui.generated.resources.service_exercise_last_value
import kmpclientplanner.sharedui.generated.resources.service_exercise_new_hint
import kmpclientplanner.sharedui.generated.resources.service_exercise_pick_empty
import kmpclientplanner.sharedui.generated.resources.service_exercise_pick_title
import kmpclientplanner.sharedui.generated.resources.service_exercise_repeats
import kmpclientplanner.sharedui.generated.resources.service_exercise_sets
import kmpclientplanner.sharedui.generated.resources.service_exercise_weight
import kmpclientplanner.sharedui.generated.resources.service_workout
import kmpclientplanner.sharedui.generated.resources.service_workout_empty
import kmpclientplanner.sharedui.generated.resources.service_workout_new
import kmpclientplanner.sharedui.generated.resources.service_workout_note
import kmpclientplanner.sharedui.generated.resources.service_workout_pick
import kmpclientplanner.sharedui.generated.resources.statistics_empty_value
import org.jetbrains.compose.resources.stringResource

/**
 * Карточка «Тренировка» (экран 06): таблица упражнений с колонкой «Прошлый раз»
 * и дельта-тегом веса, плюс два действия в шапке.
 */
@Composable
internal fun WorkoutCard(
    state: ServiceDetailsScreenState,
    onAction: (ServiceDetailsScreenAction) -> Unit,
) {
    OrganicCard(
        modifier = Modifier
            .fillMaxWidth()
            .elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
        verticalGap = 14.dp,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrganicText(
                text = stringResource(Res.string.service_workout).uppercase(),
                style = OrganicTheme.typography.kicker,
                color = OrganicTheme.colors.accent,
                modifier = Modifier.weight(1f),
            )
            OrganicButton(
                text = stringResource(Res.string.service_workout_pick),
                onClick = {
                    onAction(ServiceDetailsScreenAction.SportServiceAction.OnPickExerciseClicked)
                },
                colors = OrganicButtonDefaults.secondary(),
                textStyle = OrganicTheme.typography.button.copy(fontSize = 13.sp),
                enabled = state.knownExercises.isNotEmpty(),
            )
            OrganicButton(
                text = stringResource(Res.string.service_workout_new),
                onClick = {
                    onAction(ServiceDetailsScreenAction.SportServiceAction.OnNewExerciseClicked)
                },
                icon = OrganicIcons.Plus,
                textStyle = OrganicTheme.typography.button.copy(fontSize = 13.sp),
            )
        }

        if (state.exerciseRows.isEmpty()) {
            OrganicText(
                text = stringResource(Res.string.service_workout_empty),
                style = OrganicTheme.typography.bodySm,
                color = OrganicTheme.colors.muted,
            )
            return@OrganicCard
        }

        // Шесть колонок: на узкой панели таблица прокручивается, а не сжимается.
        BoxWithConstraints {
            val available = maxWidth
            Column(Modifier.horizontalScroll(rememberScrollState())) {
                Column(
                    modifier = Modifier.width(maxOf(available, ExercisesTableMinWidth)),
                    verticalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2),
                ) {
                    OrganicTableHeader {
                        OrganicTableHeaderCell(
                            text = stringResource(Res.string.service_exercise),
                            modifier = Modifier.weight(ExerciseColumn),
                        )
                        OrganicTableHeaderCell(
                            text = stringResource(Res.string.service_exercise_sets),
                            modifier = Modifier.weight(SetsColumn),
                        )
                        OrganicTableHeaderCell(
                            text = stringResource(Res.string.service_exercise_repeats),
                            modifier = Modifier.weight(RepeatsColumn),
                        )
                        OrganicTableHeaderCell(
                            text = stringResource(Res.string.service_exercise_weight),
                            modifier = Modifier.weight(WeightColumn),
                        )
                        OrganicTableHeaderCell(
                            text = stringResource(Res.string.service_exercise_last_time),
                            modifier = Modifier.weight(LastTimeColumn),
                        )
                        OrganicTableHeaderCell(text = "", modifier = Modifier.weight(ActionsColumn))
                    }
                    state.exerciseRows.forEachIndexed { index, row ->
                        ExerciseTableRow(
                            row = row,
                            onDelete = {
                                onAction(
                                    ServiceDetailsScreenAction.SportServiceAction
                                        .OnDeleteExerciseClicked(index),
                                )
                            },
                        )
                    }
                }
            }
        }

        OrganicText(
            text = stringResource(Res.string.service_workout_note),
            style = OrganicTheme.typography.label,
            color = OrganicTheme.colors.muted,
        )
    }
}

@Composable
private fun ExerciseTableRow(row: ExerciseRow, onDelete: () -> Unit) {
    val dash = stringResource(Res.string.statistics_empty_value)
    val unit = stringResource(Res.string.client_weight_kg)
    OrganicTableRow {
        OrganicTableCell(row.title, Modifier.weight(ExerciseColumn))
        OrganicTableCell(row.setsCount.toString(), Modifier.weight(SetsColumn), numeric = true)
        OrganicTableCell(row.repeats.ifBlank { dash }, Modifier.weight(RepeatsColumn), numeric = true)
        OrganicTableCell(
            text = row.weight.withWeightUnit(unit) ?: dash,
            modifier = Modifier.weight(WeightColumn),
            numeric = true,
        )
        OrganicTableCell(
            text = row.previousValue() ?: dash,
            modifier = Modifier.weight(LastTimeColumn),
            color = OrganicTheme.colors.muted,
        )
        Row(
            modifier = Modifier.weight(ActionsColumn),
            horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            row.weightDelta?.let { delta ->
                Tag(
                    text = delta.toDeltaLabel(stringResource(Res.string.client_weight_kg)),
                    colors = if (delta > 0f) TagDefaults.accent2() else TagDefaults.accent(),
                )
            }
            Box(Modifier.weight(1f))
            OrganicIconButton(
                icon = OrganicIcons.Trash,
                onClick = onDelete,
                contentDescription = stringResource(Res.string.service_exercise_delete),
                size = 28.dp,
                iconSize = 14.dp,
                colors = OrganicButtonDefaults.ghost(),
            )
        }
    }
}

/** «4 × 10 · 55 кг» — значения прошлого выполнения. */
@Composable
private fun ExerciseRow.previousValue(): String? {
    val sets = previousSetsCount ?: return null
    val unit = stringResource(Res.string.client_weight_kg)
    val weight = previousWeight.withWeightUnit(unit)
        ?: return "$sets × ${previousRepeats.orEmpty()}"
    return stringResource(
        Res.string.service_exercise_last_value,
        sets,
        previousRepeats.orEmpty(),
        weight,
    )
}

/**
 * «60» → «60 кг», «60 кг» и «60 lb» остаются как есть: вес вводится свободным текстом,
 * поэтому единицу дописываем только к чистому числу.
 */
private fun String?.withWeightUnit(unit: String): String? {
    val value = this?.trim()?.takeIf { it.isNotBlank() } ?: return null
    val numeric = value.all { it.isDigit() || it == ',' || it == '.' }
    return if (numeric) "$value $unit" else value
}

/** «+5 кг» / «−2,5 кг»: дробная часть показывается только когда она есть. */
private fun Float.toDeltaLabel(unit: String): String {
    val sign = if (this > 0f) "+" else "−"
    val value = kotlin.math.abs(this)
    val text = if (value % 1f == 0f) value.toInt().toString() else value.toUIAmount()
    return "$sign$text $unit"
}

/** М11 — новое упражнение: название, подходы, повторы, вес. */
@Composable
internal fun NewExerciseModal(
    prefill: KnownExercise?,
    onAdd: (title: String, sets: Int, repeats: String, weight: String) -> Unit,
    onDismiss: () -> Unit,
    fullScreen: Boolean = false,
) {
    var title by remember { mutableStateOf(prefill?.title.orEmpty()) }
    var sets by remember { mutableStateOf(prefill?.setsCount?.toString() ?: "3") }
    var repeats by remember { mutableStateOf(prefill?.repeats.orEmpty()) }
    var weight by remember { mutableStateOf(prefill?.weight.orEmpty()) }

    OrganicModal(width = OrganicModalWidth.Medium, fullScreen = fullScreen) {
        OrganicModalHeader(
            title = stringResource(Res.string.service_workout_new),
            subtitle = stringResource(Res.string.service_exercise_new_hint),
            onClose = onDismiss,
        )
        OrganicField(label = stringResource(Res.string.service_exercise)) {
            OrganicTextField(value = title, onValueChange = { title = it })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space3)) {
            OrganicField(
                label = stringResource(Res.string.service_exercise_sets),
                modifier = Modifier.weight(1f),
            ) {
                OrganicTextField(
                    value = sets,
                    onValueChange = { sets = it.filter(Char::isDigit) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            OrganicField(
                label = stringResource(Res.string.service_exercise_repeats),
                modifier = Modifier.weight(1f),
            ) {
                OrganicTextField(value = repeats, onValueChange = { repeats = it })
            }
            OrganicField(
                label = stringResource(Res.string.service_exercise_weight),
                modifier = Modifier.weight(1f),
            ) {
                OrganicTextField(value = weight, onValueChange = { weight = it })
            }
        }
        OrganicModalActions {
            OrganicButton(
                text = stringResource(Res.string.cancel),
                onClick = onDismiss,
                colors = OrganicButtonDefaults.secondary(),
            )
            OrganicButton(
                text = stringResource(Res.string.service_exercise_add),
                onClick = { onAdd(title.trim(), sets.toIntOrNull() ?: 1, repeats.trim(), weight.trim()) },
                enabled = title.isNotBlank(),
            )
        }
    }
}

/** «Выбрать из созданных»: упражнения клиента с их последними значениями. */
@Composable
internal fun PickExerciseModal(
    exercises: List<KnownExercise>,
    onPick: (KnownExercise) -> Unit,
    onDismiss: () -> Unit,
    fullScreen: Boolean = false,
) {
    OrganicModal(width = OrganicModalWidth.Medium, fullScreen = fullScreen) {
        OrganicModalHeader(
            title = stringResource(Res.string.service_exercise_pick_title),
            onClose = onDismiss,
        )
        if (exercises.isEmpty()) {
            OrganicText(
                text = stringResource(Res.string.service_exercise_pick_empty),
                style = OrganicTheme.typography.bodySm,
                color = OrganicTheme.colors.muted,
            )
        } else {
            OrganicModalPanel {
                exercises.forEach { exercise ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            OrganicText(exercise.title, style = OrganicTheme.typography.bodySm)
                            OrganicText(
                                text = "${exercise.setsCount} × ${exercise.repeats} · ${exercise.weight}",
                                style = OrganicTheme.typography.label,
                                color = OrganicTheme.colors.muted,
                            )
                        }
                        OrganicButton(
                            text = stringResource(Res.string.service_exercise_add),
                            onClick = { onPick(exercise) },
                            colors = OrganicButtonDefaults.secondary(),
                            textStyle = OrganicTheme.typography.button.copy(fontSize = 13.sp),
                        )
                    }
                }
            }
        }
        OrganicModalActions {
            OrganicButton(
                text = stringResource(Res.string.cancel),
                onClick = onDismiss,
                colors = OrganicButtonDefaults.secondary(),
            )
        }
    }
}

// Доли колонок таблицы упражнений. Считаны по самому длинному содержимому: заголовку
// «ПОДХОДЫ» нужно ~75 (иначе он переносился по слогам), «Прошлый раз» — ~110 под
// «4 × 10 · 55 кг», действиям — ~105 под дельта-тег с корзиной.
private const val ExerciseColumn = 1.85f
private const val SetsColumn = 0.85f
private const val RepeatsColumn = 0.75f
private const val WeightColumn = 0.6f
private const val LastTimeColumn = 1.25f
private const val ActionsColumn = 1.2f

/** Ниже этой ширины таблица прокручивается вбок, а не сжимает колонки в кашу. */
private val ExercisesTableMinWidth = 560.dp

@Preview
@Composable
private fun NewExerciseModalPreview() {
    PreviewSurface(width = 580.dp) {
        Box(Modifier.padding(OrganicTheme.spacing.space4)) {
            NewExerciseModal(
                prefill = KnownExercise("Приседания со штангой", 4, "10", "55"),
                onAdd = { _, _, _, _ -> },
                onDismiss = {},
            )
        }
    }
}
