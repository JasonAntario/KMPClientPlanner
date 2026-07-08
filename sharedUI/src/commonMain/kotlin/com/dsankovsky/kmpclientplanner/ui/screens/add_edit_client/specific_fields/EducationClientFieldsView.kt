@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.specific_fields

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.EventRepeat
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.ui.components.BooleanSelectorView
import com.dsankovsky.kmpclientplanner.ui.components.ServiceDateTimeSelectorView
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientAction
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_add_lesson
import kmpclientplanner.sharedui.generated.resources.client_choose_format_education
import kmpclientplanner.sharedui.generated.resources.client_level
import kmpclientplanner.sharedui.generated.resources.client_offline
import kmpclientplanner.sharedui.generated.resources.client_online
import kmpclientplanner.sharedui.generated.resources.service_training_delete_lesson
import org.jetbrains.compose.resources.stringResource


@Composable
fun AddEditEducationClientFieldsView(
    fields: ClientSpecificFields.EducationClientSpecificFields,
    level: TextFieldState,
    onAction: (AddEditClientAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            state = level,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(stringResource(Res.string.client_level))
            }
        )

        Text(
            text = stringResource(Res.string.client_choose_format_education),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BooleanSelectorView(
            value = fields.isOnline,
            falseLabel = stringResource(Res.string.client_offline),
            trueLabel = stringResource(Res.string.client_online),
            modifier = Modifier.fillMaxWidth(),
            onChange = { onAction(AddEditClientAction.EducationClientAction.OnFormatChanged(it)) }
        )

        ScheduleSlotsSection(
            slots = fields.lessonDateTimeList,
            deleteLabel = stringResource(Res.string.service_training_delete_lesson),
            addLabel = stringResource(Res.string.client_add_lesson),
            onDayOfWeekChanged = { index, day ->
                onAction(AddEditClientAction.EducationClientAction.OnDayOfWeekChanged(index, day))
            },
            onTimeChanged = { index, time ->
                onAction(AddEditClientAction.EducationClientAction.OnTimeChanged(index, time))
            },
            onDurationChanged = { index, duration ->
                onAction(AddEditClientAction.EducationClientAction.OnDurationChanged(index, duration))
            },
            onDeleteClicked = { index ->
                onAction(AddEditClientAction.EducationClientAction.OnDeleteLessonClicked(index))
            },
            onAddClicked = {
                onAction(AddEditClientAction.EducationClientAction.OnAddNewServiceTime)
            }
        )
    }
}

@Composable
internal fun ScheduleSlotsSection(
    slots: List<ServiceDateTime>,
    deleteLabel: String,
    addLabel: String,
    onDayOfWeekChanged: (Int, kotlinx.datetime.DayOfWeek) -> Unit,
    onTimeChanged: (Int, kotlinx.datetime.LocalTime) -> Unit,
    onDurationChanged: (Int, String) -> Unit,
    onDeleteClicked: (Int) -> Unit,
    onAddClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.EventRepeat,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Начало и длительность услуги",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        slots.forEachIndexed { index, dateTimeItem ->
            ServiceDateTimeSelectorView(
                serviceDateTime = dateTimeItem,
                deleteLabel = deleteLabel,
                onDayOfWeekChanged = { onDayOfWeekChanged(index, it) },
                onTimeChanged = { time -> onTimeChanged(index, time) },
                onDurationChanged = { duration -> onDurationChanged(index, duration) },
                onDeleteClicked = { onDeleteClicked(index) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedButton(
            onClick = onAddClicked,
            shape = CircleShape,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = addLabel,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}


@PreviewLightDark
@Composable
private fun PreviewAddEditClientScreenEducation() {
    ClientPlannerTheme {
        AddEditEducationClientFieldsView(
            fields = ClientSpecificFields.EducationClientSpecificFields(
                level = "C29",
                isOnline = true,
                lessonDateTimeList = listOf(ServiceDateTime())
            ),
            level = rememberTextFieldState("C29"),
            {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
