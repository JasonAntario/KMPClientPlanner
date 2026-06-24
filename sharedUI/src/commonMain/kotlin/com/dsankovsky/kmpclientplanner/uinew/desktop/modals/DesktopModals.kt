package com.dsankovsky.kmpclientplanner.uinew.desktop.modals

import androidx.compose.runtime.Composable
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientEvent
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientScreen
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceEvent
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceScreen
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServiceScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServicesScreen
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.ModalScaffold
import androidx.compose.ui.unit.dp

/**
 * The desktop "create / edit lesson" dialog. Reuses the existing [AddEditServiceScreen]
 * (and its ViewModel, validation and type-specific fields) inside a centered modal card.
 */
@Composable
fun AddEditServiceModal(
    serviceId: Long?,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
) {
    ModalScaffold(onDismiss = onDismiss, cardWidth = 560.dp) {
        AddEditServiceScreen(
            serviceId = serviceId,
            onEvent = { event ->
                when (event) {
                    AddEditServiceEvent.OnDismissClicked -> onDismiss()
                    AddEditServiceEvent.OnServiceSaved -> onSaved()
                    AddEditServiceEvent.OnServiceDeleted -> onSaved()
                }
            },
        )
    }
}

/** The desktop "create / edit client" dialog, reusing [AddEditClientScreen]. */
@Composable
fun AddEditClientModal(
    clientId: Long?,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
) {
    ModalScaffold(onDismiss = onDismiss, cardWidth = 560.dp) {
        AddEditClientScreen(
            clientId = clientId,
            onEvent = { event ->
                when (event) {
                    AddEditClientEvent.OnDismissClicked -> onDismiss()
                    AddEditClientEvent.OnClientSaved -> onSaved()
                    is AddEditClientEvent.OnClientDeleted -> onSaved()
                    AddEditClientEvent.AutofillCompleted -> Unit
                }
            },
        )
    }
}

/** The desktop "prepay" dialog, reusing [PayServicesScreen]. */
@Composable
fun PrepayModal(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
) {
    ModalScaffold(onDismiss = onDismiss, cardWidth = 560.dp) {
        PayServicesScreen(
            onEvent = { event ->
                when (event) {
                    PayServiceScreenEvent.OnDismissClicked -> onDismiss()
                    PayServiceScreenEvent.OnSuccess -> onSuccess()
                }
            },
        )
    }
}
