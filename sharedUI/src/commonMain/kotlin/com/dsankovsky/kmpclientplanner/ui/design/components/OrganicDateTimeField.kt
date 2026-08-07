@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcon
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.design.organicFocusRing
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIDate
import com.dsankovsky.kmpclientplanner.ui.extensions.toUITime
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.confirm
import kmpclientplanner.sharedui.generated.resources.datetime_pick_date
import kmpclientplanner.sharedui.generated.resources.datetime_pick_time
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Поле даты: выглядит как `.input` из макета, но это кнопка — по клику открывается календарь.
 *
 * Набирать дату руками негде и не нужно: значение всегда приходит из пикера, поэтому наружу
 * уходит готовый [LocalDate], а не текст, который пришлось бы разбирать.
 *
 * Сам пикер — `DatePicker` из material3: он тонируется маппингом токенов в `OrganicTheme`
 * (тема держит `MaterialTheme` именно ради пикеров) и всплывает поверх модалки формы, а не
 * внутри её панели.
 */
@Composable
fun OrganicDateField(
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var showPicker by remember { mutableStateOf(false) }

    PickerField(
        text = date.toUIDate(),
        icon = OrganicIcons.Calendar,
        contentDescription = stringResource(Res.string.datetime_pick_date),
        modifier = modifier,
        enabled = enabled,
        onClick = { showPicker = true },
    )

    if (!showPicker) return

    val state = rememberDatePickerState(
        initialSelectedDateMillis = LocalDateTime(date, LocalTime(0, 0))
            .toInstant(TimeZone.UTC)
            .toEpochMilliseconds(),
    )
    DatePickerDialog(
        onDismissRequest = { showPicker = false },
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let { millis ->
                        onDateSelected(
                            Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.UTC)
                                .date,
                        )
                    }
                    showPicker = false
                },
            ) {
                Text(stringResource(Res.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = { showPicker = false }) {
                Text(stringResource(Res.string.cancel))
            }
        },
    ) {
        DatePicker(state = state)
    }
}

/** То же для времени: кнопка с текущим временем, по клику — `TimePicker` из material3. */
@Composable
fun OrganicTimeField(
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var showPicker by remember { mutableStateOf(false) }

    PickerField(
        text = time.toUITime(),
        icon = OrganicIcons.Clock,
        contentDescription = stringResource(Res.string.datetime_pick_time),
        modifier = modifier,
        enabled = enabled,
        onClick = { showPicker = true },
    )

    if (!showPicker) return

    val state = rememberTimePickerState(
        initialHour = time.hour,
        initialMinute = time.minute,
        is24Hour = true,
    )
    AlertDialog(
        onDismissRequest = { showPicker = false },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(LocalTime(state.hour, state.minute))
                    showPicker = false
                },
            ) {
                Text(stringResource(Res.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = { showPicker = false }) {
                Text(stringResource(Res.string.cancel))
            }
        },
        text = { TimePicker(state = state) },
    )
}

/**
 * Визуально это `.input`: pill, surface, бордер divider → text@45% на hover → accent в фокусе.
 * Поведение — кнопки: клик и Enter/Space открывают пикер, каретки и клавиатурного ввода нет.
 */
@Composable
private fun PickerField(
    text: String,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = OrganicTheme.colors
    val shape = OrganicTheme.shapes.pill
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()

    val border = when {
        !enabled -> colors.divider
        focused -> colors.accent
        hovered -> colors.inputBorderHover
        else -> colors.divider
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .hoverable(interactionSource, enabled = enabled)
            // Кольцо фокуса вплотную к бордеру — как у поля ввода.
            .organicFocusRing(focused, shape, colors.accent, offset = 0.dp)
            .background(colors.surface, shape)
            .border(1.dp, border, shape)
            .defaultMinSize(minHeight = FieldMinHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OrganicText(
            text = text,
            style = OrganicTheme.typography.bodySm,
            color = if (enabled) colors.text else colors.muted,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        OrganicIcon(
            imageVector = icon,
            contentDescription = contentDescription,
            size = IconSize,
            tint = colors.muted,
        )
    }
}

private val FieldMinHeight = 36.dp
private val IconSize = 16.dp
