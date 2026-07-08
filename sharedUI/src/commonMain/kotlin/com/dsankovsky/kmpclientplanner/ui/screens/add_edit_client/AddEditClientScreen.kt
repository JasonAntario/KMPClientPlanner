@file:OptIn(ExperimentalMaterial3Api::class)

package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.ui.components.DropDownMenuView
import com.dsankovsky.kmpclientplanner.ui.components.ShortNameBoxView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.specific_fields.AddEditEducationClientFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.specific_fields.AddEditSportClientFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.FormTopBar
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.autofill_description
import kmpclientplanner.sharedui.generated.resources.cancel
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
            shape = RoundedCornerShape(28.dp),
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

    Scaffold { paddingValues ->
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

    val saveEnabled = name.text.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .imePadding()
    ) {
        FormTopBar(
            title = if (screenState.isEdit)
                stringResource(Res.string.client_edit_client)
            else
                stringResource(Res.string.client_new_client),
            subtitle = screenState.serviceType
                .takeIf { it != ServiceType.BASE }
                ?.let { "Тип услуг · ${it.toUIName()}" },
            isEdit = screenState.isEdit,
            saveEnabled = saveEnabled,
            onClose = { onAction(AddEditClientAction.OnCloseScreenClicked) },
            onDelete = { onAction(AddEditClientAction.OnDeleteClient) },
            onSave = {
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
            }
        )

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            BoxWithConstraints(
                modifier = Modifier.widthIn(max = 820.dp).fillMaxWidth()
            ) {
                val isWide = maxWidth >= 600.dp

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp).withNavBarPadding(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ShortNameBoxView(
                                text = clientShortName.toString(),
                                backgroundColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp).size(96.dp)
                            )
                        }
                    }

                    item { SectionLabel(text = "Базовые данные") }

                    if (isWide) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ClientField(
                                    state = name,
                                    label = "${stringResource(Res.string.client_name)} *",
                                    keyboardType = KeyboardType.Text,
                                    capitalization = KeyboardCapitalization.Words,
                                    modifier = Modifier.weight(1f),
                                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                                )
                                ClientField(
                                    state = surname,
                                    label = stringResource(Res.string.client_surname),
                                    keyboardType = KeyboardType.Text,
                                    capitalization = KeyboardCapitalization.Words,
                                    modifier = Modifier.weight(1f),
                                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                                )
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ClientField(
                                    state = phone,
                                    label = stringResource(Res.string.client_phone),
                                    keyboardType = KeyboardType.Phone,
                                    modifier = Modifier.weight(1f),
                                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                                )
                                ClientField(
                                    state = address,
                                    label = stringResource(Res.string.client_address),
                                    keyboardType = KeyboardType.Text,
                                    capitalization = KeyboardCapitalization.Words,
                                    modifier = Modifier.weight(1f),
                                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                                )
                            }
                        }
                    } else {
                        item {
                            ClientField(
                                state = name,
                                label = "${stringResource(Res.string.client_name)} *",
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                                modifier = Modifier.fillMaxWidth(),
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        }
                        item {
                            ClientField(
                                state = surname,
                                label = stringResource(Res.string.client_surname),
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                                modifier = Modifier.fillMaxWidth(),
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        }
                        item {
                            ClientField(
                                state = phone,
                                label = stringResource(Res.string.client_phone),
                                keyboardType = KeyboardType.Phone,
                                modifier = Modifier.fillMaxWidth(),
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        }
                        item {
                            ClientField(
                                state = address,
                                label = stringResource(Res.string.client_address),
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                                modifier = Modifier.fillMaxWidth(),
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        }
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                state = price,
                                shape = RoundedCornerShape(8.dp),
                                lineLimits = TextFieldLineLimits.SingleLine,
                                label = { Text(stringResource(Res.string.client_price)) },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
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
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    when (screenState.clientSpecificFields) {
                        is ClientSpecificFields.EducationClientSpecificFields -> {
                            item {
                                TypeFieldsHeader(serviceType = screenState.serviceType)
                            }
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
                                TypeFieldsHeader(serviceType = screenState.serviceType)
                            }
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
                        OutlinedTextField(
                            state = comment,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(Res.string.client_comment)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientField(
    state: TextFieldState,
    label: String,
    keyboardType: KeyboardType,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None
) {
    OutlinedTextField(
        state = state,
        shape = RoundedCornerShape(8.dp),
        lineLimits = TextFieldLineLimits.SingleLine,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization,
            imeAction = ImeAction.Next
        ),
        onKeyboardAction = { onNext() },
        modifier = modifier
    )
}

@Composable
private fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun TypeFieldsHeader(
    serviceType: ServiceType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SectionLabel(text = "Поля для типа")
            ClientTypeChip(serviceType = serviceType)
        }
    }
}

@Composable
private fun ClientTypeChip(
    serviceType: ServiceType,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = serviceType.iconRounded(),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = serviceType.toUIName(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

private fun ServiceType.iconRounded(): ImageVector = when (this) {
    ServiceType.EDUCATION -> Icons.Rounded.School
    ServiceType.SPORT -> Icons.Rounded.FitnessCenter
    ServiceType.BEAUTY -> Icons.Rounded.Face
    ServiceType.TATTOO -> Icons.Rounded.Gesture
    ServiceType.BASE -> Icons.Rounded.Category
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
