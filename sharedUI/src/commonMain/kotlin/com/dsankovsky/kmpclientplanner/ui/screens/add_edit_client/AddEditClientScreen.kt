@file:OptIn(ExperimentalMaterial3Api::class)

package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.dsankovsky.kmpclientplanner.ui.components.DropDownMenuView
import com.dsankovsky.kmpclientplanner.ui.components.ShortNameBoxView
import com.dsankovsky.kmpclientplanner.ui.components.ToolbarView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.specific_fields.AddEditEducationClientFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.specific_fields.AddEditSportClientFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.autofill_description
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.client_add_client
import kmpclientplanner.sharedui.generated.resources.client_address
import kmpclientplanner.sharedui.generated.resources.client_comment
import kmpclientplanner.sharedui.generated.resources.client_confirm_deleting
import kmpclientplanner.sharedui.generated.resources.client_edit_client
import kmpclientplanner.sharedui.generated.resources.client_name
import kmpclientplanner.sharedui.generated.resources.client_new_client
import kmpclientplanner.sharedui.generated.resources.client_phone
import kmpclientplanner.sharedui.generated.resources.client_price
import kmpclientplanner.sharedui.generated.resources.client_price_will_fill_automatically
import kmpclientplanner.sharedui.generated.resources.client_shoud_continue_autofill
import kmpclientplanner.sharedui.generated.resources.client_surname
import kmpclientplanner.sharedui.generated.resources.client_update_data
import kmpclientplanner.sharedui.generated.resources.confirm
import kmpclientplanner.sharedui.generated.resources.service_crossing
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

    state.showDialog?.let { dialog ->
        val onDismiss: () -> Unit
        val onConfirm: () -> Unit

        when (dialog) {
            ClientScreenDialog.ConfirmAutofillServices -> {
                onDismiss =
                    { viewModel.handleActions(AddEditClientAction.OnAutofillDismissClicked) }
                onConfirm =
                    { viewModel.handleActions(AddEditClientAction.OnAutofillConfirmClicked) }
            }

            ClientScreenDialog.ConfirmClientDeleting -> {
                onDismiss = { viewModel.handleActions(AddEditClientAction.CloseClientDialog) }
                onConfirm = { viewModel.handleActions(AddEditClientAction.OnDeleteClientConfirmed) }
            }

            is ClientScreenDialog.ServicesCrossing -> {
                onDismiss = { viewModel.handleActions(AddEditClientAction.CloseClientDialog) }
                onConfirm =
                    { viewModel.handleActions(AddEditClientAction.OnAutofillWithCrossingConfirmClicked) }
            }
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(Res.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.cancel))
                }
            },
            text = {
                when (dialog) {
                    ClientScreenDialog.ConfirmAutofillServices -> {
                        Text(
                            text = stringResource(Res.string.autofill_description),
                            textAlign = TextAlign.Center
                        )
                    }

                    ClientScreenDialog.ConfirmClientDeleting -> {
                        Text(
                            text = stringResource(Res.string.client_confirm_deleting),
                            textAlign = TextAlign.Center
                        )
                    }

                    is ClientScreenDialog.ServicesCrossing -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            item {
                                Text(
                                    text = stringResource(Res.string.service_crossing),
                                    style = MaterialTheme.typography.titleSmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            items(dialog.services) { service ->
                                Text(text = service.title)
                                Text(text = service.getServiceTime())
                                HorizontalDivider()
                            }
                            item {
                                Text(
                                    text = stringResource(Res.string.client_shoud_continue_autofill),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    viewModel.event.collectWithLifecycle { event -> onEvent(event) }

    Scaffold(
        topBar = {
            ToolbarView(
                title = if (state.isEdit)
                    stringResource(Res.string.client_edit_client)
                else
                    stringResource(Res.string.client_new_client),
                onBackClicked = {
                    viewModel.handleActions(AddEditClientAction.OnCloseScreenClicked)
                },
                actionIcon = if (state.isEdit) Icons.Default.DeleteForever else null,
                actionIconColor = if (state.isEdit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                onActionClicked = {
                    viewModel.handleActions(AddEditClientAction.OnDeleteClient)
                }
            )
        }
    ) { paddingValues ->

        when {
            state.isLoading -> LoadingScreen()
            else -> {
                AddEditClientScreenContent(
                    screenState = state,
                    onAction = viewModel::handleActions,
                    modifier = modifier.padding(paddingValues)
                )
            }
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

    val name = rememberTextFieldState()
    val surname = rememberTextFieldState()
    val comment = rememberTextFieldState()
    val address = rememberTextFieldState()
    val phone = rememberTextFieldState()
    val price = rememberTextFieldState()

    val level = rememberTextFieldState()
    val weight = rememberTextFieldState()

    LaunchedEffect(screenState.name) { name.edit { replace(0, length, screenState.name) } }
    LaunchedEffect(screenState.surname) { surname.edit { replace(0, length, screenState.surname) } }
    LaunchedEffect(screenState.comment) { comment.edit { replace(0, length, screenState.comment) } }
    LaunchedEffect(screenState.address) { address.edit { replace(0, length, screenState.address) } }
    LaunchedEffect(screenState.phone) { phone.edit { replace(0, length, screenState.phone) } }
    LaunchedEffect(screenState.price) { price.edit { replace(0, length, screenState.price) } }

    val educationFields = screenState.clientSpecificFields as? ClientSpecificFields.EducationClientSpecificFields
    val sportFields = screenState.clientSpecificFields as? ClientSpecificFields.SportClientSpecificFields
    LaunchedEffect(educationFields?.level) { level.edit { replace(0, length, educationFields?.level.orEmpty()) } }
    LaunchedEffect(sportFields?.weight) { weight.edit { replace(0, length, sportFields?.weight.orEmpty()) } }

    val clientShortName by remember {
        derivedStateOf {
            if (surname.text.isNotEmpty()) {
                (name.text.take(1).toString() + surname.text.take(1)).uppercase()
            } else {
                name.text.take(2)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp).withNavBarPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ShortNameBoxView(
                    text = clientShortName.toString(),
                    modifier = Modifier.padding(20.dp).size(100.dp)
                )
            }
        }

        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                state = name,
                lineLimits = TextFieldLineLimits.SingleLine,
                label = {
                    Text(stringResource(Res.string.client_name))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                onKeyboardAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        }

        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                lineLimits = TextFieldLineLimits.SingleLine,
                state = surname,
                label = {
                    Text(stringResource(Res.string.client_surname))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                onKeyboardAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        }

        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                lineLimits = TextFieldLineLimits.SingleLine,
                state = address,
                label = {
                    Text(stringResource(Res.string.client_address))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                onKeyboardAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        }

        item {

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                state = phone,
                lineLimits = TextFieldLineLimits.SingleLine,
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
                OutlinedTextField(
                    state = price,
                    lineLimits = TextFieldLineLimits.SingleLine,
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

                DropDownMenuView(
                    currentItem = screenState.currency,
                    items = screenState.currenciesList,
                    transformItemToText = { it.code },
                    modifier = Modifier.weight(1f),
                    onItemSelected = {
                        onAction(AddEditClientAction.OnCurrencyChanged(it))
                    }
                )
            }
        }

        item {
            Text(
                text = stringResource(Res.string.client_price_will_fill_automatically),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        item {
            OutlinedTextField(
                state = comment,
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp),
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
                        level = level,
                        onAction = onAction
                    )
                }
            }

            is ClientSpecificFields.SportClientSpecificFields -> {
                item {
                    AddEditSportClientFieldsView(
                        fields = screenState.clientSpecificFields,
                        weight = weight,
                        onAction = onAction
                    )
                }
            }

            else -> {}
        }

        item {
            Button(
                onClick = {
                    onAction(
                        AddEditClientAction.OnClientSaveClicked(
                            name = name.text.toString(),
                            surname = surname.text.toString(),
                            comment = comment.text.toString(),
                            address = address.text.toString(),
                            phone = phone.text.toString(),
                            price = price.text.toString(),
                            level = level.text.toString(),
                            weight = weight.text.toString()
                        )
                    )
                },
                enabled = name.text.isNotBlank(),
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
                phone = "35544334",
                price = "12323123",
                surname = "Test"
            ), {})
    }
}