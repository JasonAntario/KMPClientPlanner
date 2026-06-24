package com.dsankovsky.kmpclientplanner.uinew.desktop.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServiceScreenAction
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServiceScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServiceScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServicesScreenViewModel
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.InitialsAvatar
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.ModalScaffold
import com.dsankovsky.kmpclientplanner.uinew.desktop.screens.initialsOf
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily
import org.koin.compose.viewmodel.koinViewModel

/**
 * Desktop "prepay" dialog, redrawn in the Lessons design system and driven by the
 * existing [PayServicesScreenViewModel] (client picker + amount stepper).
 */
@Composable
fun PrepayModal(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
) {
    val viewModel: PayServicesScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.handleActions(PayServiceScreenAction.LoadData) }
    viewModel.event.collectWithLifecycle { event ->
        when (event) {
            PayServiceScreenEvent.OnDismissClicked -> onDismiss()
            PayServiceScreenEvent.OnSuccess -> onSuccess()
        }
    }

    ModalScaffold(onDismiss = onDismiss, cardWidth = 460.dp) {
        PrepayContent(state = state, onAction = viewModel::handleActions, onDismiss = onDismiss)
    }
}

@Composable
private fun PrepayContent(
    state: PayServiceScreenState,
    onAction: (PayServiceScreenAction) -> Unit,
    onDismiss: () -> Unit,
) {
    val amount = state.servicesAmount.toIntOrNull() ?: 0

    Column(Modifier.fillMaxWidth()) {
        ModalHeader(title = "Предоплата", onClose = onDismiss)

        Column(
            modifier = Modifier.padding(horizontal = ModalBodyHorizontalPadding, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(ModalBodyVerticalGap),
        ) {
            ModalField("Клиент") {
                ClientSelector(
                    state = state,
                    onSelect = { onAction(PayServiceScreenAction.OnChangeClientCLicked(it)) },
                )
            }

            ModalField("Количество занятий") {
                Stepper(
                    value = amount,
                    available = state.availableServices,
                    enabled = state.client != null,
                    onChange = { onAction(PayServiceScreenAction.OnServicesAmountChanged(it.toString())) },
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "К оплате",
                    color = LessonsColors.TextMuted,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                )
                Text(
                    "$amount занятий",
                    color = LessonsColors.Success,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                )
            }
            PayButton(
                enabled = state.isPaymentReady,
                onClick = { onAction(PayServiceScreenAction.OnPayClicked) },
                modifier = Modifier.weight(1.4f),
            )
        }
    }
}

@Composable
private fun ClientSelector(
    state: PayServiceScreenState,
    onSelect: (com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(LessonsColors.CardBackground)
                .border(1.dp, LessonsColors.Border, shape)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val client = state.client
            if (client != null) {
                InitialsAvatar(initialsOf(client.getFullName()), size = 32.dp, fontSize = 11)
                Column(Modifier.weight(1f)) {
                    Text(
                        client.getFullName(),
                        color = LessonsColors.TextPrimary,
                        fontFamily = nunitoFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Text(
                        "${state.availableServices} неоплаченных занятий",
                        color = LessonsColors.TextMuted,
                        fontFamily = nunitoFamily(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                    )
                }
            } else {
                Text(
                    "Выберите клиента",
                    modifier = Modifier.weight(1f),
                    color = LessonsColors.TextMuted,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
            Icon(Icons.Filled.KeyboardArrowDown, null, tint = LessonsColors.TextMuted, modifier = Modifier.size(16.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            state.clientsList.forEach { client ->
                DropdownMenuItem(
                    text = {
                        Text(
                            client.getFullName(),
                            fontFamily = nunitoFamily(),
                            fontWeight = if (client == state.client) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (client == state.client) LessonsColors.Primary else LessonsColors.TextPrimary,
                        )
                    },
                    onClick = {
                        onSelect(client)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun Stepper(
    value: Int,
    available: Int,
    enabled: Boolean,
    onChange: (Int) -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(LessonsColors.CardBackground)
            .border(1.dp, LessonsColors.BorderCard, shape)
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StepButton(
            icon = Icons.Filled.Remove,
            primary = false,
            enabled = enabled && value > 0,
            onClick = { onChange((value - 1).coerceAtLeast(0)) },
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$value",
                color = LessonsColors.TextPrimary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
            )
            Text(
                "из $available доступных",
                color = LessonsColors.TextMuted,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
            )
        }
        StepButton(
            icon = Icons.Filled.Add,
            primary = true,
            enabled = enabled && value < available,
            onClick = { onChange((value + 1).coerceAtMost(available)) },
        )
    }
}

@Composable
private fun StepButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primary: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    val bg = when {
        primary && enabled -> LessonsColors.Primary
        primary -> LessonsColors.PrimaryTintBorder
        else -> LessonsColors.RailBackground
    }
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(shape)
            .background(bg)
            .then(if (!primary) Modifier.border(1.dp, LessonsColors.Border, shape) else Modifier)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            null,
            tint = if (primary) Color.White else LessonsColors.TextMuted,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun PayButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(13.dp)
    Row(
        modifier = modifier
            .height(48.dp)
            .shadow(if (enabled) 10.dp else 0.dp, shape, spotColor = LessonsColors.Primary, ambientColor = LessonsColors.Primary)
            .clip(shape)
            .background(if (enabled) LessonsColors.Primary else LessonsColors.PrimaryTintBorder)
            .clickable(enabled = enabled, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
    ) {
        Icon(Icons.Filled.CheckCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
        Text(
            "Оплатить",
            color = Color.White,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
        )
    }
}
