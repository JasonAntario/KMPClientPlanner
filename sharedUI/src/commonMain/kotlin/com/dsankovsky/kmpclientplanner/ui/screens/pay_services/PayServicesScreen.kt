package com.dsankovsky.kmpclientplanner.ui.screens.pay_services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.animation.ExpandShrinkAnimatedVisibility
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.edgeToEdgeBottomPadding
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.confirm
import kmpclientplanner.sharedui.generated.resources.payment_available_services
import kmpclientplanner.sharedui.generated.resources.payment_title
import kmpclientplanner.sharedui.generated.resources.service_amount
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
    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
            .edgeToEdgeBottomPadding(0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

//        KufarDetailsScreenHeader(
//            title = stringResource(Res.string.pay_services_title),
//            onBackClicked = {
//                onAction(PayServiceScreenAction.OnBackClicked)
//            }
//        )

        Text(
            text = stringResource(Res.string.payment_title),
        )

//        KufarDropDownRawBoxed(
//            currentItem = screenState.client,
//            selectableItems = screenState.clientsList,
//            colors = kufarDropDownColorsWithBackground(
//                backgroundColor = KufarTheme.colors.border.grey.divider,
//                borderColor = KufarColor.Transparent
//            ),
//            label = stringResource(Res.string.service_choose_client),
//            onItemSelected = { client ->
//                client?.let {
//                    onAction(PayServiceScreenAction.OnChangeClientCLicked(client))
//                }
//            },
//            transformation = {
//                it?.getFullName() ?: ""
//            },
//            itemContent = {
//                Column {
//                    KufarText(
//                        text = it?.getFullName() ?: "",
//                        modifier = Modifier.padding(8.dp)
//                    )
//                    if (it != screenState.clientsList.last()) {
//                        KufarMaxWidthDivider()
//                    }
//                }
//            }
//        )

        ExpandShrinkAnimatedVisibility(screenState.client != null) {
            Text(
                stringResource(
                    Res.string.payment_available_services,
                    screenState.availableServices
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        val servicesAmount = rememberTextFieldState(screenState.servicesAmount)

        OutlinedTextField(
            state = servicesAmount,
            label = {
                Text(stringResource(Res.string.service_amount))
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            onKeyboardAction = {
                onAction(PayServiceScreenAction.OnServicesAmountChanged(servicesAmount.text.toString()))
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = {
                onAction(PayServiceScreenAction.OnPayClicked)
            },
            enabled = screenState.isPaymentReady,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.confirm))
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