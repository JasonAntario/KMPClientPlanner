package com.dsankovsky.kmpclientplanner.uinew.desktop.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily

/** Uppercase overline used as a field label above inputs. 700 · 11 · +6%. */
@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = LessonsColors.TextMuted,
        fontFamily = nunitoFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 0.66.sp,
    )
}

/**
 * Single-line text field from the design system. Radius 14, 1dp neutral border that
 * thickens to a 1.5dp terracotta ring on focus; placeholder uses the muted ink.
 */
@Composable
fun LessonsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
) {
    val shape = RoundedCornerShape(14.dp)
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val borderColor by animateColorAsState(if (focused) LessonsColors.Primary else LessonsColors.Border)
    val borderWidth by animateDpAsState(if (focused) 1.5.dp else 1.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(LessonsColors.CardBackground)
            .border(borderWidth, borderColor, shape)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty() && placeholder.isNotEmpty()) {
            Text(
                text = placeholder,
                color = LessonsColors.TextMuted,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interaction,
            singleLine = singleLine,
            cursorBrush = SolidColor(LessonsColors.Primary),
            textStyle = TextStyle(
                color = LessonsColors.TextPrimary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            ),
        )
    }
}

/** Pill toggle / switch. 44×26 track, 20dp thumb, terracotta when on. */
@Composable
fun LessonsToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackColor by animateColorAsState(if (checked) LessonsColors.Primary else LessonsColors.ToggleOff)
    val thumbOffset by animateDpAsState(if (checked) 18.dp else 0.dp)
    Box(
        modifier = modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) },
            )
            .padding(horizontal = 3.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}

@Preview
@Composable
private fun FieldLabelPreview() = ComponentPreview {
    FieldLabel("Имя клиента")
}

@Preview
@Composable
private fun LessonsTextFieldPreview() = ComponentPreview {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LessonsTextField(
            value = "Физика — оптика",
            onValueChange = {},
            modifier = Modifier.width(280.dp)
        )
        LessonsTextField(
            value = "",
            onValueChange = {},
            placeholder = "Добавьте комментарий…",
            modifier = Modifier.width(280.dp)
        )
    }
}

@Preview
@Composable
private fun LessonsTogglePreview() = ComponentPreview {
    var checked by remember { mutableStateOf(true) }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LessonsToggle(checked = checked, onCheckedChange = { checked = it })
        LessonsToggle(checked = false, onCheckedChange = {})
    }
}
