package com.dsankovsky.kmpclientplanner.ui.screens.pay_services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.animation.ExpandShrinkAnimatedVisibility
import com.dsankovsky.kmpclientplanner.ui.components.DropDownMenuView
import com.dsankovsky.kmpclientplanner.ui.components.ToolbarView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.confirm
import kmpclientplanner.sharedui.generated.resources.pay_services_title
import kmpclientplanner.sharedui.generated.resources.payment_available_services
import kmpclientplanner.sharedui.generated.resources.payment_title
import kmpclientplanner.sharedui.generated.resources.service_amount
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
    val servicesAmount = rememberTextFieldState(screenState.servicesAmount)

    LaunchedEffect(servicesAmount) {
        snapshotFlow { servicesAmount.text.toString() }
            .collect { text ->
                onAction(PayServiceScreenAction.OnServicesAmountChanged(text))
            }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ToolbarView(
                title = stringResource(Res.string.pay_services_title),
                onBackClicked = { onAction(PayServiceScreenAction.OnBackClicked) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 100.dp
            ).withNavBarPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.payment_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (screenState.clientsList.isNotEmpty()) {
                item {
                    DropDownMenuView(
                        currentItem = screenState.client,
                        items = screenState.clientsList,
                        transformItemToText = { it.getFullName() },
                        label = stringResource(Res.string.service_choose_client),
                        onItemSelected = { onAction(PayServiceScreenAction.OnChangeClientCLicked(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                ExpandShrinkAnimatedVisibility(screenState.client != null) {
                    Text(
                        text = stringResource(
                            Res.string.payment_available_services,
                            screenState.availableServices
                        ),
                        modifier = Modifier.padding(vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                OutlinedTextField(
                    state = servicesAmount,
                    label = {
                        Text(stringResource(Res.string.service_amount))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }

            item {
                Button(
                    onClick = { onAction(PayServiceScreenAction.OnPayClicked) },
                    enabled = screenState.isPaymentReady,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(stringResource(Res.string.confirm))
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewPayServicesScreenContent() {
    ClientPlannerTheme {
        PayServicesScreenContent(
            PayServiceScreenState(client = BaseClient()),
            {},
        )
    }
}
