package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.specific_fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.ui.components.BooleanSelectorView
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientAction
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_add_training
import kmpclientplanner.sharedui.generated.resources.client_choose_format_sport
import kmpclientplanner.sharedui.generated.resources.client_offline
import kmpclientplanner.sharedui.generated.resources.client_online
import kmpclientplanner.sharedui.generated.resources.client_weight
import kmpclientplanner.sharedui.generated.resources.service_training_delete_training
import org.jetbrains.compose.resources.stringResource


@Composable
fun AddEditSportClientFieldsView(
    fields: ClientSpecificFields.SportClientSpecificFields,
    weight: TextFieldState,
    onAction: (AddEditClientAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            state = weight,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.MonitorWeight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(stringResource(Res.string.client_weight))
            }
        )

        Text(
            text = stringResource(Res.string.client_choose_format_sport),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BooleanSelectorView(
            value = fields.isOnline,
            falseLabel = stringResource(Res.string.client_offline),
            trueLabel = stringResource(Res.string.client_online),
            modifier = Modifier.fillMaxWidth(),
            onChange = { onAction(AddEditClientAction.SportClientAction.OnFormatChanged(it)) }
        )

        ScheduleSlotsSection(
            slots = fields.lessonDateTimeList,
            deleteLabel = stringResource(Res.string.service_training_delete_training),
            addLabel = stringResource(Res.string.client_add_training),
            onDayOfWeekChanged = { index, day ->
                onAction(AddEditClientAction.SportClientAction.OnDayOfWeekChanged(index, day))
            },
            onTimeChanged = { index, time ->
                onAction(AddEditClientAction.SportClientAction.OnTimeChanged(index, time))
            },
            onDurationChanged = { index, duration ->
                onAction(AddEditClientAction.SportClientAction.OnDurationChanged(index, duration))
            },
            onDeleteClicked = { index ->
                onAction(AddEditClientAction.SportClientAction.OnDeleteTrainingClicked(index))
            },
            onAddClicked = {
                onAction(AddEditClientAction.SportClientAction.OnAddNewServiceTime)
            }
        )
    }
}


@PreviewLightDark
@Composable
private fun PreviewAddEditClientScreenSport() {
    ClientPlannerTheme {
        AddEditSportClientFieldsView(
            fields = ClientSpecificFields.SportClientSpecificFields(
                weight = "296",
                lessonDateTimeList = listOf(ServiceDateTime())
            ),
            weight = rememberTextFieldState("296"),
            {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
