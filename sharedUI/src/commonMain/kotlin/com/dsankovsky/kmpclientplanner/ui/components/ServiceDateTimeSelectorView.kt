@file:OptIn(ExperimentalMaterial3Api::class)

package com.dsankovsky.kmpclientplanner.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.extensions.toUITime
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.confirm
import kmpclientplanner.sharedui.generated.resources.datetime_duration
import kmpclientplanner.sharedui.generated.resources.datetime_friday
import kmpclientplanner.sharedui.generated.resources.datetime_monday
import kmpclientplanner.sharedui.generated.resources.datetime_saturday
import kmpclientplanner.sharedui.generated.resources.datetime_sunday
import kmpclientplanner.sharedui.generated.resources.datetime_thursday
import kmpclientplanner.sharedui.generated.resources.datetime_tuesday
import kmpclientplanner.sharedui.generated.resources.datetime_wednesday
import kmpclientplanner.sharedui.generated.resources.service_start_time
import kmpclientplanner.sharedui.generated.resources.service_training_delete_lesson
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun ServiceDateTimeSelectorView(
    serviceDateTime: ServiceDateTime,
    deleteLabel: String,
    onDayOfWeekChanged: (DayOfWeek) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    onDurationChanged: (String) -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDayDropdownExpanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val days = remember { DayOfWeek.entries }

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = isDayDropdownExpanded,
                onExpandedChange = { isDayDropdownExpanded = !isDayDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = serviceDateTime.dayOfWeek.displayName(),
                    readOnly = true,
                    onValueChange = {},
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = isDayDropdownExpanded,
                    onDismissRequest = { isDayDropdownExpanded = false }
                ) {
                    days.forEach { day ->
                        DropdownMenuItem(
                            text = { Text(day.displayName()) },
                            onClick = {
                                onDayOfWeekChanged(day)
                                isDayDropdownExpanded = false
                            },
                            trailingIcon = {
                                if (day == serviceDateTime.dayOfWeek) {
                                    Icon(
                                        Icons.Default.Done,
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val timeInteractionSource = remember { MutableInteractionSource() }
                LaunchedEffect(timeInteractionSource) {
                    timeInteractionSource.interactions.collect { interaction ->
                        if (interaction is PressInteraction.Press) {
                            showTimePicker = true
                        }
                    }
                }

                OutlinedTextField(
                    value = serviceDateTime.time.toUITime(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(Res.string.service_start_time)) },
                    interactionSource = timeInteractionSource,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = serviceDateTime.duration,
                    onValueChange = onDurationChanged,
                    label = { Text(stringResource(Res.string.datetime_duration)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedButton(
                onClick = onDeleteClicked,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Text(deleteLabel)
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = serviceDateTime.time.hour,
            initialMinute = serviceDateTime.time.minute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChanged(LocalTime(timePickerState.hour, timePickerState.minute))
                    showTimePicker = false
                }) {
                    Text(stringResource(Res.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
private fun DayOfWeek.displayName(): String = when (this) {
    DayOfWeek.MONDAY -> stringResource(Res.string.datetime_monday)
    DayOfWeek.TUESDAY -> stringResource(Res.string.datetime_tuesday)
    DayOfWeek.WEDNESDAY -> stringResource(Res.string.datetime_wednesday)
    DayOfWeek.THURSDAY -> stringResource(Res.string.datetime_thursday)
    DayOfWeek.FRIDAY -> stringResource(Res.string.datetime_friday)
    DayOfWeek.SATURDAY -> stringResource(Res.string.datetime_saturday)
    DayOfWeek.SUNDAY -> stringResource(Res.string.datetime_sunday)
    else -> name
}

@PreviewLightDark
@Composable
private fun PreviewKufarDayOfWeekTimeSelectorView() {
    ClientPlannerTheme {
        ServiceDateTimeSelectorView(
            serviceDateTime = ServiceDateTime(),
            deleteLabel = stringResource(Res.string.service_training_delete_lesson),
            onDayOfWeekChanged = {},
            onTimeChanged = {},
            onDurationChanged = {},
            onDeleteClicked = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
