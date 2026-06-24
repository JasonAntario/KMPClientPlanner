package com.dsankovsky.kmpclientplanner.uinew.desktop.modals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientAction
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.ClientScreenDialog
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceAction
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceScreenState
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.autofill_description
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.client_confirm_deleting
import kmpclientplanner.sharedui.generated.resources.client_shoud_continue_autofill
import kmpclientplanner.sharedui.generated.resources.confirm
import kmpclientplanner.sharedui.generated.resources.service_confirm_deleting
import kmpclientplanner.sharedui.generated.resources.service_crossing
import org.jetbrains.compose.resources.stringResource

@Composable
fun ServiceDialogs(
    state: AddEditServiceScreenState,
    onAction: (AddEditServiceAction) -> Unit,
) {
    val dialog = state.showDialog ?: return
    val onDismiss = { onAction(AddEditServiceAction.OnDialogDismissed) }
    val onConfirm: () -> Unit = when (dialog) {
        AddEditServiceScreenState.ServiceScreenDialog.ConfirmServiceDeleting ->
            { { onAction(AddEditServiceAction.OnDeleteServiceConfirmed) } }
        is AddEditServiceScreenState.ServiceScreenDialog.ServicesCrossing ->
            { { onAction(AddEditServiceAction.OnSaveServiceConfirmed) } }
    }
    ConfirmDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    ) {
        when (dialog) {
            AddEditServiceScreenState.ServiceScreenDialog.ConfirmServiceDeleting ->
                Text(stringResource(Res.string.service_confirm_deleting), textAlign = TextAlign.Center)

            is AddEditServiceScreenState.ServiceScreenDialog.ServicesCrossing ->
                CrossingContent(dialog.services.map { it.title to it.getServiceTime() })
        }
    }
}

@Composable
fun ClientDialogs(
    state: AddEditClientScreenState,
    onAction: (AddEditClientAction) -> Unit,
) {
    val dialog = state.showDialog ?: return
    val onDismiss: () -> Unit
    val onConfirm: () -> Unit
    when (dialog) {
        ClientScreenDialog.ConfirmAutofillServices -> {
            onDismiss = { onAction(AddEditClientAction.OnAutofillDismissClicked) }
            onConfirm = { onAction(AddEditClientAction.OnAutofillConfirmClicked) }
        }

        ClientScreenDialog.ConfirmClientDeleting -> {
            onDismiss = { onAction(AddEditClientAction.CloseClientDialog) }
            onConfirm = { onAction(AddEditClientAction.OnDeleteClientConfirmed) }
        }

        is ClientScreenDialog.ServicesCrossing -> {
            onDismiss = { onAction(AddEditClientAction.CloseClientDialog) }
            onConfirm = { onAction(AddEditClientAction.OnAutofillWithCrossingConfirmClicked) }
        }
    }
    ConfirmDialog(onDismiss = onDismiss, onConfirm = onConfirm) {
        when (dialog) {
            ClientScreenDialog.ConfirmAutofillServices ->
                Text(stringResource(Res.string.autofill_description), textAlign = TextAlign.Center)

            ClientScreenDialog.ConfirmClientDeleting ->
                Text(stringResource(Res.string.client_confirm_deleting), textAlign = TextAlign.Center)

            is ClientScreenDialog.ServicesCrossing ->
                CrossingContent(dialog.services.map { it.title to it.getServiceTime() })
        }
    }
}

@Composable
private fun ConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    text: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(Res.string.confirm)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) } },
        text = text,
    )
}

@Composable
private fun CrossingContent(services: List<Pair<String, String>>) {
    Column(
        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(stringResource(Res.string.service_crossing), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        services.forEach { (title, time) ->
            Text(title)
            Text(time)
            HorizontalDivider()
        }
        Text(
            stringResource(Res.string.client_shoud_continue_autofill),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
