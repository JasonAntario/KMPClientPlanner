@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.dsankovsky.kmpclientplanner.uinew.desktop.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.ui.extensions.toUITime
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.FieldLabel
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Per-type chip shown next to the modal title (icon + profession). */
data class ServiceTypeBadge(val label: String, val icon: ImageVector)

/** Header of a modal: title, optional profession pill and a close button. */
@Composable
fun ModalHeader(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    badge: ServiceTypeBadge? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 22.dp, end = 22.dp, top = 18.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                title,
                color = LessonsColors.TextPrimary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
            )
            if (badge != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LessonsColors.PrimaryTint)
                        .border(1.dp, LessonsColors.PrimaryTintBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(
                        badge.icon,
                        null,
                        tint = LessonsColors.Primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        badge.label,
                        color = LessonsColors.Primary,
                        fontFamily = nunitoFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(LessonsColors.CardBackground)
                .border(1.dp, LessonsColors.BorderNeutral, RoundedCornerShape(10.dp))
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Close,
                null,
                tint = LessonsColors.TextSecondary,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

/** Field label + content laid out vertically, matching the modal field spec. */
@Composable
fun ModalField(label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(label)
        content()
    }
}

/** Thin divider used between the base fields and the per-type section. */
@Composable
fun ModalDivider(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(1.dp).background(LessonsColors.Border))
}

/** Terracotta uppercase header for the per-type fields section. */
@Composable
fun TypeSectionHeader(label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon, null, tint = LessonsColors.Primary, modifier = Modifier.size(14.dp))
        Text(
            label.uppercase(),
            color = LessonsColors.Primary,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            letterSpacing = 0.6.sp,
        )
    }
}

/**
 * A read-only field that looks like an input but opens something on click
 * (used for dropdowns, date and time pickers). Optional [leading] slot for an avatar.
 */
@Composable
fun SelectorField(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = Icons.Filled.KeyboardArrowDown,
    leading: (@Composable () -> Unit)? = null,
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(LessonsColors.CardBackground)
            .border(1.dp, LessonsColors.Border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (leading != null) leading()
        Text(
            text,
            modifier = Modifier.weight(1f),
            color = LessonsColors.TextPrimary,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
        if (trailingIcon != null) {
            Icon(
                trailingIcon,
                null,
                tint = LessonsColors.TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/** A [SelectorField] wired to a [DropdownMenu] for picking one item from [items]. */
@Composable
fun <T> DropdownField(
    current: T?,
    items: List<T>,
    itemText: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leading: (@Composable () -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        SelectorField(
            text = current?.let(itemText) ?: placeholder,
            onClick = { expanded = true },
            modifier = modifier,
            leading = leading,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                val selected = item == current
                DropdownMenuItem(
                    text = {
                        Text(
                            itemText(item),
                            fontFamily = nunitoFamily(),
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (selected) LessonsColors.Primary else LessonsColors.TextPrimary,
                        )
                    },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    },
                )
            }
        }
    }
}

/** Styled date trigger that opens a Material date picker dialog. */
@Composable
fun DateField(date: LocalDate, onDateChanged: (LocalDate) -> Unit, modifier: Modifier = Modifier) {
    var show by remember { mutableStateOf(false) }
    SelectorField(
        text = "${date.dayOfMonth} ${ruMonthGenitive(date.monthNumber)}",
        onClick = { show = true },
        modifier = modifier,
        trailingIcon = Icons.Filled.DateRange,
    )
    if (show) {
        val initialMillis =
            LocalDateTime(date, LocalTime(0, 0)).toInstant(TimeZone.UTC).toEpochMilliseconds()
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { show = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onDateChanged(
                            Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
                        )
                    }
                    show = false
                }) { Text("OK", color = LessonsColors.Primary) }
            },
            dismissButton = {
                TextButton(onClick = { show = false }) {
                    Text(
                        "Отмена",
                        color = LessonsColors.TextSecondary
                    )
                }
            },
        ) { DatePicker(state = pickerState) }
    }
}

/** Styled time trigger that opens a Material time picker dialog. */
@Composable
fun TimeField(time: LocalTime, onTimeChanged: (LocalTime) -> Unit, modifier: Modifier = Modifier) {
    var show by remember { mutableStateOf(false) }
    SelectorField(
        text = time.toUITime(),
        onClick = { show = true },
        modifier = modifier,
        trailingIcon = null,
    )
    if (show) {
        val pickerState = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { show = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChanged(LocalTime(pickerState.hour, pickerState.minute))
                    show = false
                }) { Text("OK", color = LessonsColors.Primary) }
            },
            dismissButton = {
                TextButton(onClick = { show = false }) {
                    Text(
                        "Отмена",
                        color = LessonsColors.TextSecondary
                    )
                }
            },
            text = { TimePicker(state = pickerState) },
        )
    }
}

/** Cancel / Save footer with an optional leading delete button (edit mode). */
@Composable
fun ModalFooter(
    saveLabel: String,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    saveEnabled: Boolean,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onDelete != null) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(LessonsColors.DangerTint)
                    .border(1.5.dp, LessonsColors.DangerTintBorder, RoundedCornerShape(13.dp))
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Delete,
                    null,
                    tint = LessonsColors.Danger,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        FooterButton(
            label = "Отмена",
            modifier = Modifier.weight(1f),
            onClick = onCancel,
            primary = false,
            enabled = true,
        )
        FooterButton(
            label = saveLabel,
            modifier = Modifier.weight(1.5f),
            onClick = onSave,
            primary = true,
            enabled = saveEnabled,
        )
    }
}

@Composable
private fun FooterButton(
    label: String,
    onClick: () -> Unit,
    primary: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(13.dp)
    val base = modifier.height(48.dp)
    val styled = if (primary) {
        base
            .shadow(
                if (enabled) 10.dp else 0.dp,
                shape,
                spotColor = LessonsColors.Primary,
                ambientColor = LessonsColors.Primary
            )
            .clip(shape)
            .background(if (enabled) LessonsColors.Primary else LessonsColors.PrimaryTintBorder)
    } else {
        base
            .clip(shape)
            .background(LessonsColors.CardBackground)
            .border(1.5.dp, LessonsColors.Border, shape)
    }
    Row(
        modifier = styled.clickable(
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
        horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (primary) {
            Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Text(
            label,
            color = if (primary) Color.White else LessonsColors.TextSecondary,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
        )
    }
}

/** Top inset matching the design's modal field-group spacing. */
val ModalBodyVerticalGap: Dp = 13.dp
val ModalBodyHorizontalPadding: Dp = 22.dp

private val ruMonthsGenitive = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)

internal fun ruMonthGenitive(monthNumber: Int): String =
    ruMonthsGenitive[(monthNumber - 1).coerceIn(0, 11)]
