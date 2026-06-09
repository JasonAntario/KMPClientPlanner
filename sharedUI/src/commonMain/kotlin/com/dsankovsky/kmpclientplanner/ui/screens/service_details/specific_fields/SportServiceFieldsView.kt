package com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields.components.ExerciseItemView
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenAction
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_training
import kmpclientplanner.sharedui.generated.resources.service_training_add_exercise
import org.jetbrains.compose.resources.stringResource


@Composable
fun ServiceSportFieldsView(
    fields: ServiceSpecificFields.SportServiceSpecificFields,
    onAction: (ServiceDetailsScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(stringResource(Res.string.service_training))
        fields.exercises.forEachIndexed { exerciseIndex, exercise ->
            ExerciseItemView(
                exercise = exercise,
                exerciseIndex = exerciseIndex,
                onAction = onAction,
                modifier = Modifier
            )
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onAction(ServiceDetailsScreenAction.SportServiceAction.OnAddExerciseClicked)
            }
        ) {
            Text(stringResource(Res.string.service_training_add_exercise))
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewAddEditServiceSportFieldsView() {
    ClientPlannerTheme {
        ServiceSportFieldsView(
            modifier = Modifier.padding(16.dp),
            fields = ServiceSpecificFields.SportServiceSpecificFields(
                exercises = listOf(
                    ServiceSpecificFields.SportServiceSpecificFields.Exercise(
                        title = "Жим лежа",
                        sets = listOf(
                            ServiceSpecificFields.SportServiceSpecificFields.Exercise.ExerciseSet(
                                repeats = "15",
                                weight = "25"
                            )
                        )
                    )
                )
            ),
            onAction = {}
        )
    }
}