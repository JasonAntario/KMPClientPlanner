package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.specific_fields.AddEditEducationClientFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.specific_fields.AddEditSportClientFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_add_client
import kmpclientplanner.sharedui.generated.resources.client_address
import kmpclientplanner.sharedui.generated.resources.client_comment
import kmpclientplanner.sharedui.generated.resources.client_name
import kmpclientplanner.sharedui.generated.resources.client_phone
import kmpclientplanner.sharedui.generated.resources.client_price
import kmpclientplanner.sharedui.generated.resources.client_price_will_fill_automatically
import kmpclientplanner.sharedui.generated.resources.client_surname
import kmpclientplanner.sharedui.generated.resources.client_update_data
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddEditClientScreen(
    onEvent: (AddEditClientEvent) -> Unit,
    modifier: Modifier = Modifier,
    clientId: Long? = null
) {
    val viewModel: AddEditClientViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleActions(AddEditClientAction.LoadClientData(clientId))
    }

//    state.showDialog?.let { dialog ->
//        KufarDialog(
//            onNegativeClick = {
//                when (dialog) {
//                    ClientScreenDialog.ConfirmAutofillServices -> {
//                        viewModel.handleActions(AddEditClientAction.OnAutofillDismissClicked)
//                    }
//
//                    ClientScreenDialog.ConfirmClientDeleting -> {
//                        viewModel.handleActions(AddEditClientAction.CloseClientDialog)
//                    }
//
//                    is ClientScreenDialog.ServicesCrossing -> {
//                        viewModel.handleActions(AddEditClientAction.CloseClientDialog)
//                    }
//                }
//            },
//            onPositiveClick = {
//                when (dialog) {
//                    ClientScreenDialog.ConfirmAutofillServices -> {
//                        viewModel.handleActions(AddEditClientAction.OnAutofillConfirmClicked)
//                    }
//
//                    ClientScreenDialog.ConfirmClientDeleting -> {
//                        viewModel.handleActions(AddEditClientAction.OnDeleteClientConfirmed)
//                    }
//
//                    is ClientScreenDialog.ServicesCrossing -> {
//                        viewModel.handleActions(AddEditClientAction.OnAutofillWithCrossingConfirmClicked)
//                    }
//                }
//            }
//        ) {
//            when (dialog) {
//                ClientScreenDialog.ConfirmAutofillServices -> {
//                    Text(
//                        text = stringResource(Res.string.autofill_description),
//                        textAlign = TextAlign.Center
//                    )
//                }
//
//                ClientScreenDialog.ConfirmClientDeleting -> {
//                    KufarText(
//                        stringResource(Res.string.client_confirm_deleting),
//                        textAlign = TextAlign.Center
//                    )
//                }
//
//                is ClientScreenDialog.ServicesCrossing -> {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .heightIn(max = 400.dp),
//                        verticalArrangement = Arrangement.spacedBy(4.dp)
//                    ) {
//                        item {
//                            KufarText(
//                                stringResource(Res.string.service_crossing),
//                                textStyle = KufarTheme.typography.H2.Bold,
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                        items(dialog.services) { baseService ->
//                            KufarText(baseService.title)
//                            KufarText(baseService.getServiceTime())
//                            KufarMaxWidthDivider()
//                        }
//                        item {
//                            KufarSpacer(modifier = Modifier.height(8.dp))
//                            KufarText(
//                                stringResource(Res.string.client_shoud_continue_autofill),
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }

    viewModel.event.collectWithLifecycle { event -> onEvent(event) }

    when {
        state.isLoading -> LoadingScreen()
        else -> {
            AddEditClientScreenContent(
                screenState = state,
                onAction = viewModel::handleActions,
                modifier = modifier
            )
        }
    }
}

@Composable
fun AddEditClientScreenContent(
    screenState: AddEditClientScreenState,
    onAction: (AddEditClientAction) -> Unit,
    modifier: Modifier = Modifier
) {

    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .imePadding(),
        contentPadding = PaddingValues(16.dp).withNavBarPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

//        stickyHeader {
//            KufarDetailsScreenHeader(
//                title = if (screenState.isEdit)
//                    stringResource(Res.string.client_edit_client)
//                else
//                    stringResource(Res.string.client_new_client),
//                showDeleteIcon = screenState.isEdit,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .kufarBackground(KufarTheme.colors.bg.grey.secondary),
//                onBackClicked = {
//                    onAction(AddEditClientAction.OnCloseScreenClicked)
//                },
//                onDeleteClicked = {
//                    onAction(AddEditClientAction.OnDeleteClient)
//                }
//            )
//        }

        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(20.dp)
                        .size(100.dp)
                ) {
                    Text(
                        text = screenState.getShortName(),
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }
        }

        item {
            val name = rememberTextFieldState(screenState.name)

            OutlinedTextField(
                state = name,
                label = {
                    Text(stringResource(Res.string.client_name))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                onKeyboardAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        }

        item {
            val surname = rememberTextFieldState(screenState.surname)

            OutlinedTextField(
                state = surname,
                label = {
                    Text(stringResource(Res.string.client_surname))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                onKeyboardAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        }

        item {
            val address = rememberTextFieldState(screenState.surname)

            OutlinedTextField(
                state = address,
                label = {
                    Text(stringResource(Res.string.client_address))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                onKeyboardAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        }

        item {
            val phone = rememberTextFieldState(screenState.phone)

            OutlinedTextField(
                state = phone,
                label = {
                    Text(stringResource(Res.string.client_phone))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                onKeyboardAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                val price = rememberTextFieldState(screenState.price)

                OutlinedTextField(
                    state = price,
                    label = {
                        Text(stringResource(Res.string.client_price))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                    ),
                    onKeyboardAction = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    modifier = Modifier.weight(2f)
                )

//                KufarDropDownRawFree(
//                    currentItem = screenState.currency,
//                    selectableItems = screenState.currenciesList,
//                    colors = kufarDropDownColorsWithBackground(
//                        backgroundColor = KufarTheme.colors.border.grey.divider,
//                        borderColor = KufarColor.Transparent
//                    ),
//                    modifier = Modifier.weight(1f),
//                    label = stringResource(Res.string.client_currency),
//                    onItemSelected = {
//                        onAction(AddEditClientAction.OnCurrencyChanged(it))
//                    },
//                    transformation = { it.code },
//                    itemContent = {
//                        val isSelected = it.code == screenState.currency.code
//                        val textColor = if (isSelected)
//                            KufarTheme.colors.text.accent.green.main
//                        else
//                            KufarTheme.colors.text.grey.primary
//                        Column {
//                            Row(
//                                modifier = Modifier
//                                    .defaultMinSize(minWidth = 200.dp)
//                                    .padding(8.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Column(modifier = Modifier.weight(1f)) {
//                                    KufarText(it.code, textColor = textColor)
//                                    KufarText(
//                                        stringResource(it.descriptionId),
//                                        textColor = textColor,
//                                        textStyle = KufarTheme.typography.B3
//                                    )
//                                }
//                                if (isSelected) {
//                                    KufarImage(
//                                        imageVector = Icons.Default.Check,
//                                        modifier = Modifier.size(24.dp),
//                                        iconColor = KufarTheme.colors.icon.accent.green.main
//                                    )
//                                }
//                            }
//                            if (it.code != screenState.currenciesList.last().code) {
//                                KufarMaxWidthDivider()
//                            }
//                        }
//                    }
//                )
            }
        }

        item {
            Text(
                text = stringResource(Res.string.client_price_will_fill_automatically),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        item {
            val comment = rememberTextFieldState(screenState.comment)

            OutlinedTextField(
                state = comment,
                label = {
                    Text(stringResource(Res.string.client_comment))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                onKeyboardAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
            )
        }

        when (screenState.clientSpecificFields) {
            is ClientSpecificFields.EducationClientSpecificFields -> {
                item {
                    AddEditEducationClientFieldsView(
                        fields = screenState.clientSpecificFields,
                        onAction = onAction
                    )
                }
            }

            is ClientSpecificFields.SportClientSpecificFields -> {
                item {
                    AddEditSportClientFieldsView(
                        fields = screenState.clientSpecificFields,
                        onAction = onAction
                    )
                }
            }

            else -> {}
        }

        item {
            TextButton(
                onClick = {
                    onAction(AddEditClientAction.OnClientSaveClicked)
                },
                enabled = screenState.name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    if (screenState.isEdit)
                        stringResource(Res.string.client_update_data)
                    else
                        stringResource(Res.string.client_add_client)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewAddEditClientScreen() {
    ClientPlannerTheme {
        AddEditClientScreenContent(
            screenState = AddEditClientScreenState(
                isEdit = true,
                serviceType = ServiceType.EDUCATION,
                clientSpecificFields = ClientSpecificFields.EducationClientSpecificFields(),
                name = "Igor",
                surname = "Test"
            ), {})
    }
}