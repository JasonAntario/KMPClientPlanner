@file:OptIn(ExperimentalTestApi::class)

package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runComposeUiTest
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHost
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTextField
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Клавиатура в модальном окне.
 *
 * Пробел в поле формы закрывал окно: `Modifier.clickable` на скриме активируется ещё и
 * на Space/Enter, а поле пробел как key-event не съедает — событие всплывало к предку.
 * Рендер-кадры такое не показывают, поэтому проверка отдельная.
 */
class OrganicModalKeyboardTest {

    @Test
    fun `space in a field does not close the modal`() = runComposeUiTest {
        var dismissed = false
        setContent { ModalWithField(onDismissRequest = { dismissed = true }) }

        onNodeWithTag(FieldTag).performClick()
        onNodeWithTag(FieldTag).performKeyInput { pressKey(Key.Spacebar) }

        assertFalse(dismissed, "пробел в поле не должен закрывать модалку")
    }

    @Test
    fun `escape closes the modal even from a focused field`() = runComposeUiTest {
        var dismissed = false
        setContent { ModalWithField(onDismissRequest = { dismissed = true }) }

        onNodeWithTag(FieldTag).performClick()
        onNodeWithTag(FieldTag).performKeyInput { pressKey(Key.Escape) }

        assertTrue(dismissed, "Esc должен закрывать модалку и с фокусом в поле")
    }
}

private const val FieldTag = "modal-field"

@androidx.compose.runtime.Composable
private fun ModalWithField(onDismissRequest: () -> Unit) {
    OrganicTheme {
        OrganicModalHost(onDismissRequest = onDismissRequest) {
            OrganicModal {
                var value by remember { mutableStateOf("") }
                OrganicTextField(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.testTag(FieldTag),
                )
            }
        }
    }
}
