package com.dsankovsky.kmpclientplanner.ui.screens.pay_services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.components.DropDownMenuView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.pay_services_title
import kmpclientplanner.sharedui.generated.resources.payment_available_services
import kmpclientplanner.sharedui.generated.resources.payment_title
import kmpclientplanner.sharedui.generated.resources.service_choose_client
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PayServicesScreen(
    modifier: Modifier = Modifier,
    onEvent: (PayServiceScreenEvent) -> Unit
) {

    val viewModel: PayServicesScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.collectWithLifecycle {
        onEvent(it)
    }

    LaunchedEffect(Unit) {
        viewModel.handleActions(PayServiceScreenAction.LoadData)
    }

    PayServicesScreenContent(
        screenState = state,
        modifier = modifier,
        onAction = viewModel::handleActions
    )
}

@Composable
fun PayServicesScreenContent(
    screenState: PayServiceScreenState,
    onAction: (PayServiceScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val count = screenState.servicesAmount.toIntOrNull() ?: 0

    Dialog(
        onDismissRequest = { onAction(PayServiceScreenAction.OnBackClicked) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 12.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddCard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = stringResource(Res.string.pay_services_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = stringResource(Res.string.payment_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (screenState.clientsList.isNotEmpty()) {
                    DropDownMenuView(
                        currentItem = screenState.client,
                        items = screenState.clientsList,
                        transformItemToText = { it.getFullName() },
                        label = stringResource(Res.string.service_choose_client),
                        onItemSelected = { onAction(PayServiceScreenAction.OnChangeClientCLicked(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                StepperCard(
                    count = count,
                    available = screenState.availableServices,
                    onDecrement = {
                        onAction(PayServiceScreenAction.OnServicesAmountChanged((count - 1).toString()))
                    },
                    onIncrement = {
                        onAction(PayServiceScreenAction.OnServicesAmountChanged((count + 1).toString()))
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerLow,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(
                            Res.string.payment_available_services,
                            screenState.availableServices
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onAction(PayServiceScreenAction.OnBackClicked) }) {
                        Text("Отмена")
                    }
                    Button(
                        onClick = { onAction(PayServiceScreenAction.OnPayClicked) },
                        enabled = screenState.isPaymentReady,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Оплатить $count занятия")
                    }
                }
            }
        }
    }
}

@Composable
private fun StepperCard(
    count: Int,
    available: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainerLow,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StepperButton(
            icon = Icons.Rounded.Remove,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface,
            enabled = count > 0,
            onClick = onDecrement
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$count",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "из $available неоплаченных",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        StepperButton(
            icon = Icons.Rounded.Add,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            enabled = count < available,
            onClick = onIncrement
        )
    }
}

@Composable
private fun StepperButton(
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val alpha = if (enabled) 1f else 0.4f
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(48.dp)
            .background(containerColor.copy(alpha = alpha), CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor.copy(alpha = alpha)
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewPayServicesScreenContent() {
    ClientPlannerTheme {
        PayServicesScreenContent(
            PayServiceScreenState(client = BaseClient(), availableServices = 5),
            {},
        )
    }
}
