package com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenAction
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields.components.ExerciseItemView
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_repeats
import kmpclientplanner.sharedui.generated.resources.service_title
import kmpclientplanner.sharedui.generated.resources.service_training
import kmpclientplanner.sharedui.generated.resources.service_training_add_exercise
import kmpclientplanner.sharedui.generated.resources.service_weight
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.FitnessCenter,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = stringResource(Res.string.service_training),
                modifier = Modifier.weight(1f),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedButton(
                onClick = {
                    onAction(ServiceDetailsScreenAction.SportServiceAction.OnAddExerciseClicked)
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 12.dp,
                    vertical = 6.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(Res.string.service_training_add_exercise),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                TableHeaderCell(stringResource(Res.string.service_title), weight = 2f)
                TableHeaderCell(stringResource(Res.string.service_repeats), weight = 1f, textAlign = TextAlign.Center)
                TableHeaderCell(stringResource(Res.string.service_weight), weight = 1f, textAlign = TextAlign.End)
            }

            fields.exercises.forEachIndexed { exerciseIndex, exercise ->
                if (exerciseIndex > 0) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                ExerciseItemView(
                    exercise = exercise,
                    exerciseIndex = exerciseIndex,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TableHeaderCell(
    text: String,
    weight: Float,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text = text.uppercase(),
        modifier = Modifier.weight(weight),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = textAlign
    )
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
