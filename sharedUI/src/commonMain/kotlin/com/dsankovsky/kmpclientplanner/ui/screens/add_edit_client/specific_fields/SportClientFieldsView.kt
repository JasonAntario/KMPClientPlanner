package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.specific_fields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientAction
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_add_training
import kmpclientplanner.sharedui.generated.resources.client_weight
import org.jetbrains.compose.resources.stringResource


@Composable
fun AddEditSportClientFieldsView(
    fields: ClientSpecificFields.SportClientSpecificFields,
    onAction: (AddEditClientAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val weight = rememberTextFieldState(fields.weight.orEmpty())

        OutlinedTextField(
            state = weight,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(stringResource(Res.string.client_weight))
            }
        )

//        KufarOnlineSelector(
//            modifier = Modifier
//                .fillMaxWidth(),
//            serviceType = ServiceType.SPORT,
//            isOnline = fields.isOnline,
//            onChanged = {
//                onAction(AddEditClientAction.SportClientAction.OnFormatChanged(it))
//            }
//        )

//        fields.lessonDateTimeList.forEachIndexed { index, dateTime ->
//            KufarDayOfWeekTimeSelectorView(
//                serviceType = ServiceType.SPORT,
//                serviceTime = dateTime.time,
//                dayOfWeek = dateTime.dayOfWeek,
//                duration = dateTime.duration,
//                onDayOfWeekChanged = {
//                    onAction(AddEditClientAction.SportClientAction.OnDayOfWeekChanged(index, it))
//                },
//                onTimeChanged = { time ->
//                    onAction(AddEditClientAction.SportClientAction.OnTimeChanged(index, time))
//                },
//                onDeleteClicked = {
//                    onAction(AddEditClientAction.SportClientAction.OnDeleteTrainingClicked(index))
//                },
//                onDurationChanged = { duration, isCorrect ->
//                    onAction(
//                        AddEditClientAction.SportClientAction.OnDurationChanged(
//                            index,
//                            duration
//                        )
//                    )
//                }
//            )
//        }

        TextButton(
            onClick = {
                onAction(AddEditClientAction.SportClientAction.OnAddNewServiceTime)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.client_add_training))
        }
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
            {},
            modifier = Modifier.padding(16.dp)
        )
    }
}