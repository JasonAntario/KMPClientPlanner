package com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenAction
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme

@Composable
fun ExerciseItemView(
    exercise: ServiceSpecificFields.SportServiceSpecificFields.Exercise,
    exerciseIndex: Int,
    modifier: Modifier = Modifier,
    onAction: (ServiceDetailsScreenAction) -> Unit
) {
    val firstSet = exercise.sets.firstOrNull()
    val reps = firstSet?.repeats.orEmpty()
    val weight = firstSet?.weight.orEmpty()

    val setsText = when {
        exercise.sets.isEmpty() -> "—"
        reps.isNotBlank() -> "${exercise.sets.size} × $reps"
        else -> exercise.sets.size.toString()
    }
    val weightText = if (weight.isNotBlank()) "$weight кг" else "—"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = exercise.title.ifBlank { "—" },
            modifier = Modifier.weight(2f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = setsText,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = weightText,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewExerciseItemView() {
    ClientPlannerTheme {
        ExerciseItemView(
            exercise = ServiceSpecificFields.SportServiceSpecificFields.Exercise(
                title = "Жим лежа",
                sets = listOf(
                    ServiceSpecificFields.SportServiceSpecificFields.Exercise.ExerciseSet(
                        repeats = "12",
                        weight = "40"
                    ),
                    ServiceSpecificFields.SportServiceSpecificFields.Exercise.ExerciseSet(
                        repeats = "12",
                        weight = "40"
                    )
                )
            ),
            exerciseIndex = 1,
            onAction = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
