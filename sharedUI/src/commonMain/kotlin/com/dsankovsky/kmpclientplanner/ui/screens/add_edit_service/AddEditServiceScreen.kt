@file:OptIn(ExperimentalMaterial3Api::class)

package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
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
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.components.DateTimeViewWithPicker
import com.dsankovsky.kmpclientplanner.ui.components.DropDownMenuView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.specific_fields.AddEditServiceEducationFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.specific_fields.AddEditServiceSportFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.client_address
import kmpclientplanner.sharedui.generated.resources.client_address_placeholder
import kmpclientplanner.sharedui.generated.resources.client_price
import kmpclientplanner.sharedui.generated.resources.client_shoud_continue_autofill
import kmpclientplanner.sharedui.generated.resources.confirm
import kmpclientplanner.sharedui.generated.resources.service_add_service
import kmpclientplanner.sharedui.generated.resources.service_choose_client
import kmpclientplanner.sharedui.generated.resources.service_comment
import kmpclientplanner.sharedui.generated.resources.service_confirm_deleting
import kmpclientplanner.sharedui.generated.resources.service_crossing
import kmpclientplanner.sharedui.generated.resources.service_edit_service
import kmpclientplanner.sharedui.generated.resources.service_end_time
import kmpclientplanner.sharedui.generated.resources.service_finished
import kmpclientplanner.sharedui.generated.resources.service_new_service
import kmpclientplanner.sharedui.generated.resources.service_paid
import kmpclientplanner.sharedui.generated.resources.service_start_time
import kmpclientplanner.sharedui.generated.resources.service_title
import kmpclientplanner.sharedui.generated.resources.service_update_data
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddEditServiceScreen(
    serviceId: Long? = null,
    modifier: Modifier = Modifier,
    onEvent: (AddEditServiceEvent) -> Unit
) {
    val viewModel = koinViewModel<AddEditServiceViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(serviceId) {
        viewModel.handleActions(AddEditServiceAction.LoadServiceData(serviceId))
    }

    viewModel.event.collectWithLifecycle { event ->
        onEvent(event)
    }

    state.showDialog?.let { dialog ->
        val onConfirm: () -> Unit = when (dialog) {
            AddEditServiceScreenState.ServiceScreenDialog.ConfirmServiceDeleting -> {
                { viewModel.handleActions(AddEditServiceAction.OnDeleteServiceConfirmed) }
            }

            is AddEditServiceScreenState.ServiceScreenDialog.ServicesCrossing -> {
                { viewModel.handleActions(AddEditServiceAction.OnSaveServiceConfirmed) }
            }
        }
        val onDismiss = { viewModel.handleActions(AddEditServiceAction.OnDialogDismissed) }

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
                    AddEditServiceScreenState.ServiceScreenDialog.ConfirmServiceDeleting -> {
                        Text(
                            text = stringResource(Res.string.service_confirm_deleting),
                            textAlign = TextAlign.Center
                        )
                    }

                    is AddEditServiceScreenState.ServiceScreenDialog.ServicesCrossing -> {
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
    Scaffold { paddingValues ->
        when {
            state.isLoading -> LoadingScreen()
            else -> {
                AddEditServiceScreenContent(
                    screenState = state,
                    onAction = viewModel::handleActions,
                    modifier = modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun AddEditServiceScreenContent(
    screenState: AddEditServiceScreenState,
    onAction: (AddEditServiceAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val title = rememberTextFieldState()
    val address = rememberTextFieldState()
    val price = rememberTextFieldState()
    val comment = rememberTextFieldState()

    LaunchedEffect(screenState.title) { title.edit { replace(0, length, screenState.title) } }
    LaunchedEffect(screenState.address) { address.edit { replace(0, length, screenState.address) } }
    LaunchedEffect(screenState.price) { price.edit { replace(0, length, screenState.price) } }
    LaunchedEffect(screenState.comment) { comment.edit { replace(0, length, screenState.comment) } }

    LaunchedEffect(screenState.client) {
        screenState.client?.let { cl ->
            address.edit {
                replace(0, length, cl.address ?: originalText)
            }
            price.edit {
                replace(0, length, cl.price?.toString() ?: originalText)
            }
        }
    }

    val saveEnabled = screenState.client != null && title.text.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .imePadding()
    ) {
        FormTopBar(
            title = if (screenState.isEdit)
                stringResource(Res.string.service_edit_service)
            else
                stringResource(Res.string.service_new_service),
            subtitle = null,
            isEdit = screenState.isEdit,
            saveEnabled = saveEnabled,
            onClose = { onAction(AddEditServiceAction.OnCloseScreenClicked) },
            onDelete = { onAction(AddEditServiceAction.OnDeleteService) },
            onSave = {
                onAction(
                    AddEditServiceAction.OnSaveServiceClicked(
                        title = title.text.toString(),
                        address = address.text.toString(),
                        price = price.text.toString(),
                        comment = comment.text.toString()
                    )
                )
            }
        )

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 820.dp).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp).withNavBarPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        state = title,
                        shape = RoundedCornerShape(8.dp),
                        label = { Text(stringResource(Res.string.service_title)) },
                        modifier = Modifier.fillMaxWidth(),
                        lineLimits = TextFieldLineLimits.SingleLine,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        onKeyboardAction = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                }

                if (screenState.clientsList.isNotEmpty()) {
                    item {
                        ClientChipsRow(
                            selected = screenState.client,
                            clients = screenState.clientsList,
                            onSelect = { onAction(AddEditServiceAction.OnClientChanged(it)) }
                        )
                    }
                }

                item {
                    DateTimeViewWithPicker(
                        title = stringResource(Res.string.service_start_time),
                        dateTime = screenState.startDateTime,
                        onDateChanged = { date ->
                            onAction(AddEditServiceAction.OnDateChanged(date, DateSource.BASE_START_DATE))
                        },
                        onTimeChanged = { time ->
                            onAction(AddEditServiceAction.OnTimeChanged(time, TimeSource.BASE_START_TIME))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    DateTimeViewWithPicker(
                        title = stringResource(Res.string.service_end_time),
                        dateTime = screenState.endDateTime,
                        onDateChanged = { date ->
                            onAction(AddEditServiceAction.OnDateChanged(date, DateSource.BASE_END_DATE))
                        },
                        onTimeChanged = { time ->
                            onAction(AddEditServiceAction.OnTimeChanged(time, TimeSource.BASE_END_TIME))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        state = address,
                        shape = RoundedCornerShape(8.dp),
                        label = { Text(stringResource(Res.string.client_address)) },
                        placeholder = { Text(stringResource(Res.string.client_address_placeholder)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        lineLimits = TextFieldLineLimits.SingleLine,
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
                            shape = RoundedCornerShape(8.dp),
                            label = { Text(stringResource(Res.string.client_price)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.weight(2f),
                            lineLimits = TextFieldLineLimits.SingleLine,
                            onKeyboardAction = {
                                focusManager.moveFocus(FocusDirection.Right)
                            }
                        )

                        DropDownMenuView(
                            currentItem = screenState.currency,
                            items = screenState.currenciesList,
                            transformItemToText = { it.code },
                            modifier = Modifier.weight(1f),
                            onItemSelected = {
                                onAction(AddEditServiceAction.OnCurrencyChanged(it))
                            }
                        )
                    }
                }

                when (val fields = screenState.serviceSpecificFields) {
                    is ServiceSpecificFields.EducationServiceSpecificFields -> {
                        item {
                            AddEditServiceEducationFieldsView(fields = fields, onAction = onAction)
                        }
                    }

                    is ServiceSpecificFields.SportServiceSpecificFields -> {
                        item {
                            AddEditServiceSportFieldsView(fields = fields, onAction = onAction)
                        }
                    }

                    else -> {}
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatusSwitchCard(
                            label = stringResource(Res.string.service_finished),
                            checked = screenState.isFinished,
                            onCheckedChange = {
                                onAction(AddEditServiceAction.OnFinishedStatusChanged(it))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        StatusSwitchCard(
                            label = stringResource(Res.string.service_paid),
                            checked = screenState.isPaid,
                            onCheckedChange = {
                                onAction(AddEditServiceAction.OnPaidStatusChanged(it))
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        state = comment,
                        shape = RoundedCornerShape(8.dp),
                        label = { Text(stringResource(Res.string.service_comment)) },
                        modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun ClientChipsRow(
    selected: BaseClient?,
    clients: List<BaseClient>,
    onSelect: (BaseClient) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(Res.string.service_choose_client),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selected?.let { client ->
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .clickable { expanded = true }
                            .padding(start = 6.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = client.getShortName(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = client.getFullName(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable { expanded = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Добавить",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                clients.forEach { client ->
                    DropdownMenuItem(
                        text = { Text(client.getFullName()) },
                        onClick = {
                            onSelect(client)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusSwitchCard(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f, fill = false)
            )
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
internal fun FormTopBar(
    title: String,
    subtitle: String?,
    isEdit: Boolean,
    saveEnabled: Boolean,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isEdit) {
                OutlinedButton(
                    onClick = onDelete,
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Удалить",
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }

            Button(
                onClick = onSave,
                enabled = saveEnabled,
                shape = CircleShape
            ) {
                Text("Сохранить")
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewAddEditServiceScreen() {
    ClientPlannerTheme {
        AddEditServiceScreenContent(
            screenState = AddEditServiceScreenState(
                isEdit = true,
                serviceType = ServiceType.EDUCATION,
                serviceSpecificFields = ServiceSpecificFields.EducationServiceSpecificFields()
            ),
            {},
        )
    }
}
