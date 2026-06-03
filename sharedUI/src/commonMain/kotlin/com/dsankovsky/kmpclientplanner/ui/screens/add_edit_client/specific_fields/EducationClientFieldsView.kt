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
import kmpclientplanner.sharedui.generated.resources.client_add_lesson
import kmpclientplanner.sharedui.generated.resources.client_level
import org.jetbrains.compose.resources.stringResource


@Composable
fun AddEditEducationClientFieldsView(
    fields: ClientSpecificFields.EducationClientSpecificFields,
    onAction: (AddEditClientAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val level = rememberTextFieldState(fields.level.orEmpty())

        OutlinedTextField(
            state = level,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(stringResource(Res.string.client_level))
            }
        )

//        KufarOnlineSelector(
//            modifier = Modifier.fillMaxWidth(),
//            isOnline = fields.isOnline,
//            serviceType = ServiceType.EDUCATION,
//            onChanged = {
//                onAction(AddEditClientAction.EducationClientAction.OnFormatChanged(it))
//            }
//        )

//        fields.lessonDateTimeList.forEachIndexed { index, dateTimeItem ->
//            KufarDayOfWeekTimeSelectorView(
//                serviceType = ServiceType.EDUCATION,
//                dayOfWeek = dateTimeItem.dayOfWeek,
//                serviceTime = dateTimeItem.time,
//                duration = dateTimeItem.duration,
//                onDayOfWeekChanged = {
//                    onAction(
//                        AddEditClientAction.EducationClientAction.OnDayOfWeekChanged(index, it)
//                    )
//                },
//                onTimeChanged = { time ->
//                    onAction(
//                        AddEditClientAction.EducationClientAction.OnTimeChanged(index, time)
//                    )
//                },
//                onDeleteClicked = {
//                    onAction(AddEditClientAction.EducationClientAction.OnDeleteLessonClicked(index))
//                },
//                onDurationChanged = { duration, isCorrect ->
//                    onAction(
//                        AddEditClientAction.EducationClientAction.OnDurationChanged(
//                            index,
//                            duration
//                        )
//                    )
//                }
//            )
//        }
        TextButton(
            onClick = {
                onAction(AddEditClientAction.EducationClientAction.OnAddNewServiceTime)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(Res.string.client_add_lesson)
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
            {},
            modifier = Modifier.padding(16.dp)
        )
    }
}