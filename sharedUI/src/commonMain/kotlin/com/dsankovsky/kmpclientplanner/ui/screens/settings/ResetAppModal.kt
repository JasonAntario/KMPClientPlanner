package com.dsankovsky.kmpclientplanner.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dsankovsky.kmpclientplanner.ui.design.components.ConfirmModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicField
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTextField
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.settings_reset_confirm
import kmpclientplanner.sharedui.generated.resources.settings_reset_description
import kmpclientplanner.sharedui.generated.resources.settings_reset_label
import kmpclientplanner.sharedui.generated.resources.settings_reset_title
import kmpclientplanner.sharedui.generated.resources.settings_reset_word
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * М10 — сброс приложения. Кнопка «Удалить всё» разблокируется, только когда введено
 * контрольное слово: раньше данные стирались одним нажатием без подтверждения.
 *
 * Модалка берёт собственный [SettingsViewModel] — она живёт вне `NavDisplay`, поэтому
 * до экземпляра с экрана настроек не дотягивается. Своё состояние ей и не нужно:
 * из VM используется только очистка базы.
 */
@Composable
fun ResetAppModal(
    onDismiss: () -> Unit,
    onCleared: () -> Unit,
    modifier: Modifier = Modifier,
    fullScreen: Boolean = false,
) {
    val viewModel: SettingsViewModel = koinViewModel()
    var confirmation by remember { mutableStateOf("") }
    val word = stringResource(Res.string.settings_reset_word)

    viewModel.event.collectWithLifecycle { event ->
        when (event) {
            SettingsScreenEvent.AllDataCleared -> onCleared()
            SettingsScreenEvent.ResetRequested -> Unit
        }
    }

    ConfirmModal(
        title = stringResource(Res.string.settings_reset_title),
        text = stringResource(Res.string.settings_reset_description),
        confirmText = stringResource(Res.string.settings_reset_confirm),
        onConfirm = { viewModel.handleActions(SettingsScreenAction.DeleteAllData) },
        onDismiss = onDismiss,
        modifier = modifier,
        destructive = true,
        confirmEnabled = confirmation.trim() == word,
        fullScreen = fullScreen,
        extraContent = {
            OrganicField(label = stringResource(Res.string.settings_reset_label, word)) {
                OrganicTextField(
                    value = confirmation,
                    onValueChange = { confirmation = it },
                    placeholder = word,
                )
            }
        },
    )
}
