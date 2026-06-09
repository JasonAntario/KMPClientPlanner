package com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenAction
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_repeats
import kmpclientplanner.sharedui.generated.resources.service_title
import kmpclientplanner.sharedui.generated.resources.service_training_add_set
import kmpclientplanner.sharedui.generated.resources.service_training_delete_exercise
import kmpclientplanner.sharedui.generated.resources.service_weight
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExerciseItemView(
    exercise: ServiceSpecificFields.SportServiceSpecificFields.Exercise,
    exerciseIndex: Int,
    modifier: Modifier = Modifier,
    onAction: (ServiceDetailsScreenAction) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = exercise.title,
                onValueChange = {
                    onAction(
                        ServiceDetailsScreenAction.SportServiceAction.OnExerciseTitleChanged(
                            exerciseIndex = exerciseIndex,
                            title = it,
                            isSelectable = false
                        )
                    )
                },
                label = {
                    Text(stringResource(Res.string.service_title))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )

            exercise.sets.forEachIndexed { setIndex, set ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${(setIndex + 1)}.",
                        modifier = Modifier.padding(top = 42.dp),
                    )

                    OutlinedTextField(
                        value = set.repeats,
                        onValueChange = {
                            onAction(
                                ServiceDetailsScreenAction.SportServiceAction.OnSetRepeatsChanged(
                                    exerciseIndex = exerciseIndex,
                                    setIndex = setIndex,
                                    repeats = it
                                )
                            )
                        },
                        label = {
                            Text(stringResource(Res.string.service_repeats))
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )

                    OutlinedTextField(
                        value = set.weight,
                        onValueChange = {
                            onAction(
                                ServiceDetailsScreenAction.SportServiceAction.OnSetWeightChanged(
                                    exerciseIndex = exerciseIndex,
                                    setIndex = setIndex,
                                    weight = it
                                )
                            )
                        },
                        label = {
                            Text(stringResource(Res.string.service_weight))
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        )
                    )

                    IconButton(
                        modifier = Modifier
                            .padding(top = 36.dp)
                            .size(30.dp),
                        onClick = {
                            onAction(
                                ServiceDetailsScreenAction.SportServiceAction.OnDeleteSetClicked(
                                    exerciseIndex = exerciseIndex,
                                    setIndex = setIndex
                                )
                            )
                        }
                    ) {
                        Image(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onAction(
                        ServiceDetailsScreenAction.SportServiceAction.OnAddSetClicked(
                            exerciseIndex = exerciseIndex
                        )
                    )
                }
            ) {
                Text(stringResource(Res.string.service_training_add_set))
            }

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onAction(
                        ServiceDetailsScreenAction.SportServiceAction.OnDeleteExerciseClicked(
                            exerciseIndex = exerciseIndex
                        )
                    )
                }
            ) {
                Text(stringResource(Res.string.service_training_delete_exercise))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewExerciseItemView() {
    ClientPlannerTheme {
        ExerciseItemView(
            exercise = ServiceSpecificFields.SportServiceSpecificFields.Exercise(),
            exerciseIndex = 1,
            onAction = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}