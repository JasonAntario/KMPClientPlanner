package com.dsankovsky.kmpclientplanner.ui.screens.pay_services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonDefaults
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicField
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalActions
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHeader
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHost
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalPanel
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalWidth
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicSelect
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicStepper
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIDayAndMonth
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIMoney
import com.dsankovsky.kmpclientplanner.ui.extensions.toUITime
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.prepay_amount
import kmpclientplanner.sharedui.generated.resources.prepay_amount_hint
import kmpclientplanner.sharedui.generated.resources.prepay_client
import kmpclientplanner.sharedui.generated.resources.prepay_client_placeholder
import kmpclientplanner.sharedui.generated.resources.prepay_client_unpaid
import kmpclientplanner.sharedui.generated.resources.prepay_empty
import kmpclientplanner.sharedui.generated.resources.prepay_note
import kmpclientplanner.sharedui.generated.resources.prepay_pay
import kmpclientplanner.sharedui.generated.resources.prepay_title
import kmpclientplanner.sharedui.generated.resources.prepay_total
import kmpclientplanner.sharedui.generated.resources.prepay_will_be_paid
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * М5 — предоплата: клиент, счётчик занятий и плашка с тем, что именно уйдёт в оплату.
 *
 * Модалка живёт в слое `ModalState` уровня приложения, поэтому наружу отдаёт только
 * события: закрытие и успешную оплату (снекбар показывает `AppModal`).
 */
@Composable
fun PrepayModal(
    onEvent: (PayServiceScreenEvent) -> Unit,
    clientId: Long? = null,
    fullScreen: Boolean = false,
) {
    val viewModel: PayServicesScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.collectWithLifecycle { onEvent(it) }

    LaunchedEffect(clientId) {
        viewModel.handleActions(PayServiceScreenAction.LoadData(clientId))
    }

    PrepayModalContent(
        state = state,
        onAction = viewModel::handleActions,
        fullScreen = fullScreen,
    )
}

@Composable
fun PrepayModalContent(
    state: PayServiceScreenState,
    onAction: (PayServiceScreenAction) -> Unit,
    modifier: Modifier = Modifier,
    fullScreen: Boolean = false,
) {
    val onClose = { onAction(PayServiceScreenAction.OnCloseClicked) }
    OrganicModal(
        modifier = modifier,
        width = OrganicModalWidth.Medium,
        fullScreen = fullScreen,
    ) {
        OrganicModalHeader(title = stringResource(Res.string.prepay_title), onClose = onClose)

        // Подписи считаем заранее: `itemLabel` — обычная лямбда, из неё @Composable
        // (а множественное число клиентов — это plural-ресурс) вызвать нельзя.
        val options = state.selectableClients
            .filter { it.unpaidCount > 0 }
            .map { client -> client to client.label() }

        OrganicField(label = stringResource(Res.string.prepay_client)) {
            OrganicSelect(
                value = options.firstOrNull { it.first.client.id == state.selectedClientId },
                items = options,
                onSelect = { onAction(PayServiceScreenAction.OnClientSelected(it.first.client.id)) },
                itemLabel = { it.second },
                placeholder = stringResource(Res.string.prepay_client_placeholder),
                // Клиент из карточки менять нельзя — селект остаётся как подпись, кого платим.
                enabled = !state.isEmpty && !state.isClientLocked,
            )
        }

        if (state.isEmpty) {
            OrganicText(
                text = stringResource(Res.string.prepay_empty),
                style = OrganicTheme.typography.bodySm,
                color = OrganicTheme.colors.muted,
            )
        }

        if (state.selectedClientId != null && state.maxAmount > 0) {
            AmountField(state = state, onAction = onAction)
            ServicesPanel(state)
            OrganicText(
                text = stringResource(Res.string.prepay_note),
                style = OrganicTheme.typography.label,
                color = OrganicTheme.colors.muted,
            )
        }

        OrganicModalActions {
            OrganicButton(
                text = stringResource(Res.string.cancel),
                onClick = onClose,
                colors = OrganicButtonDefaults.secondary(),
            )
            OrganicButton(
                text = stringResource(Res.string.prepay_pay),
                onClick = { onAction(PayServiceScreenAction.OnPayClicked) },
                enabled = state.isPaymentReady,
            )
        }
    }
}

/** «Дмитрий Лис — 4 неоплаченных занятия». */
@Composable
private fun PrepayClient.label(): String {
    val unpaid = pluralStringResource(Res.plurals.prepay_client_unpaid, unpaidCount, unpaidCount)
    return "${client.getFullName()} — $unpaid"
}

@Composable
private fun AmountField(
    state: PayServiceScreenState,
    onAction: (PayServiceScreenAction) -> Unit,
) {
    OrganicField(label = stringResource(Res.string.prepay_amount)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrganicStepper(
                value = state.amount,
                onValueChange = { onAction(PayServiceScreenAction.OnAmountChanged(it)) },
                range = 1..state.maxAmount,
            )
            OrganicText(
                text = stringResource(Res.string.prepay_amount_hint, state.maxAmount),
                style = OrganicTheme.typography.label,
                color = OrganicTheme.colors.muted,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Плашка «Будут отмечены оплаченными»: строки занятий и итог. */
@Composable
private fun ColumnScope.ServicesPanel(state: PayServiceScreenState) {
    OrganicModalPanel(kicker = stringResource(Res.string.prepay_will_be_paid)) {
        state.servicesToPay.forEach { service ->
            PanelRow(
                left = "${service.startDate.date.toUIDayAndMonth()}, ${service.startDate.time.toUITime()}",
                right = service.price.toUIMoney(service.currency),
                style = OrganicTheme.typography.bodySm,
            )
        }
        state.totals.forEach { total ->
            PanelRow(
                left = stringResource(Res.string.prepay_total),
                right = total.money.toUIMoney(total.currency),
                style = OrganicTheme.typography.cardTitle,
            )
        }
    }
}

@Composable
private fun PanelRow(
    left: String,
    right: String,
    style: androidx.compose.ui.text.TextStyle,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        OrganicText(text = left, style = style)
        OrganicText(text = right, style = style)
    }
}

@Preview
@Composable
private fun PrepayModalPreview() {
    OrganicTheme {
        OrganicModalHost(onDismissRequest = {}) {
            PrepayModalContent(state = previewState(), onAction = {})
        }
    }
}

@Composable
private fun previewState(): PayServiceScreenState {
    val client = BaseClient(id = 1, name = "Дмитрий", surname = "Лис")
    val unpaid = List(4) { index ->
        BaseService(
            id = index + 1L,
            clientId = 1,
            price = 60f,
            currency = CurrencyItem.BYN,
        )
    }
    return PayServiceScreenState(
        isLoading = false,
        clients = listOf(PrepayClient(client, unpaid.size)),
        selectedClientId = client.id,
        amount = 3,
        unpaidServices = unpaid,
    )
}
