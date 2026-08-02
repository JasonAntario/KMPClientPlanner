package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.organicFocusRing
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * `.field` — подпись 12px над контролом, отступ 5.
 */
@Composable
fun OrganicField(
    label: String?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier) {
        if (label != null) {
            OrganicText(
                text = label,
                style = OrganicTheme.typography.label,
                color = OrganicTheme.colors.label,
                modifier = Modifier.padding(bottom = 5.dp),
            )
        }
        content()
    }
}

/**
 * `.input` — однострочное поле: pill, min-height 36, паддинг 6/14, фон surface,
 * бордер divider → text@45% на hover → accent в фокусе, каретка акцентом.
 *
 * Построено на [BasicTextField] с перегрузкой `value/onValueChange`: состояние формы живёт
 * во ViewModel (MVI), поэтому источник истины — она, а не `TextFieldState` внутри поля.
 */
@Composable
fun OrganicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    shape: Shape = OrganicTheme.shapes.pill,
    minHeight: Dp = 36.dp,
    contentPadding: PaddingValuesOf = PaddingValuesOf(horizontal = 14.dp, vertical = 6.dp),
    background: Color = OrganicTheme.colors.surface,
    textStyle: TextStyle = OrganicTheme.typography.bodySm,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val colors = OrganicTheme.colors
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val border = when {
        !enabled -> colors.divider
        focused -> colors.accent
        hovered -> colors.inputBorderHover
        else -> colors.divider
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            // BasicTextField сам hover не репортит — а бордер по нему меняется.
            .hoverable(interactionSource, enabled = enabled)
            // У поля `outline-offset: 0` — кольцо фокуса ложится вплотную к бордеру.
            .organicFocusRing(focused, shape, colors.accent, offset = 0.dp)
            .background(background, shape)
            .border(1.dp, border, shape)
            .defaultMinSize(minHeight = minHeight),
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        minLines = minLines,
        textStyle = textStyle.copy(color = if (enabled) colors.text else colors.muted),
        cursorBrush = SolidColor(colors.accent),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        decorationBox = { inner ->
            Row(
                modifier = Modifier.padding(
                    horizontal = contentPadding.horizontal,
                    vertical = contentPadding.vertical,
                ),
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                leading?.invoke()
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty() && placeholder != null) {
                        OrganicText(placeholder, style = textStyle, color = colors.muted)
                    }
                    inner()
                }
                trailing?.invoke()
            }
        },
    )
}

/**
 * `textarea.input` — единственное поле, которое не становится pill: radius 16,
 * паддинг 12/14, min-height 90 (120 для «Домашнего задания»).
 */
@Composable
fun OrganicTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    minHeight: Dp = 90.dp,
    background: Color = OrganicTheme.colors.surface,
) {
    OrganicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        singleLine = false,
        shape = OrganicTheme.shapes.textArea,
        minHeight = minHeight,
        contentPadding = PaddingValuesOf(horizontal = 14.dp, vertical = 12.dp),
        background = background,
    )
}

@Preview
@Composable
private fun OrganicTextFieldPreview() {
    PreviewSurface(width = 460.dp) {
        var name by remember { mutableStateOf("Мария Петрова") }
        var price by remember { mutableStateOf("") }
        var homework by remember { mutableStateOf("Упражнения 4–7, повторить времена.") }
        OrganicField("Имя клиента") {
            OrganicTextField(value = name, onValueChange = { name = it })
        }
        OrganicField("Стоимость") {
            OrganicTextField(value = price, onValueChange = { price = it }, placeholder = "0,00")
        }
        OrganicField("Валюта") {
            OrganicTextField(value = "BYN", onValueChange = {}, enabled = false)
        }
        OrganicField("Домашнее задание") {
            OrganicTextArea(
                value = homework,
                onValueChange = { homework = it },
                minHeight = 120.dp,
                background = OrganicTheme.colors.bg,
            )
        }
    }
}
