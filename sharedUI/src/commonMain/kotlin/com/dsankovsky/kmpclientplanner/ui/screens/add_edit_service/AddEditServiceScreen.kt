@file:OptIn(ExperimentalMaterial3Api::class)

package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.specific_fields.AddEditServiceEducationFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.specific_fields.AddEditServiceSportFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import com.dsankovsky.kmpclientplanner.ui.components.DateTimeViewWithPicker
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.client_address
import kmpclientplanner.sharedui.generated.resources.client_address_placeholder
import kmpclientplanner.sharedui.generated.resources.client_price
import kmpclientplanner.sharedui.generated.resources.client_shoud_continue_autofill
import kmpclientplanner.sharedui.generated.resources.confirm
import kmpclientplanner.sharedui.generated.resources.service_add_service
import kmpclientplanner.sharedui.generated.resources.service_comment
import kmpclientplanner.sharedui.generated.resources.service_confirm_deleting
import kmpclientplanner.sharedui.generated.resources.service_crossing
import kmpclientplanner.sharedui.generated.resources.service_end_time
import kmpclientplanner.sharedui.generated.resources.service_start_time
import kmpclientplanner.sharedui.generated.resources.service_title
import kmpclientplanner.sharedui.generated.resources.service_update_data
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddEditServiceScreen(
    onEvent: (AddEditServiceEvent) -> Unit,
    modifier: Modifier = Modifier,
    serviceId: Long? = null,
) {
    val viewModel = koinViewModel<AddEditServiceViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleActions(AddEditServiceAction.LoadServiceData(serviceId))
    }

    viewModel.event.collectWithLifecycle { event ->
        onEvent(event)
    }

    state.showDialog?.let { dialog ->
        val onConfirm: () -> Unit = when (dialog) {
            AddEditServiceScreenState.ServiceScreenDialog.ConfirmServiceDeleting ->
                { { viewModel.handleActions(AddEditServiceAction.OnDeleteServiceConfirmed) } }
            is AddEditServiceScreenState.ServiceScreenDialog.ServicesCrossing ->
                { { viewModel.handleActions(AddEditServiceAction.OnSaveServiceConfirmed) } }
        }
        val onDismiss = { viewModel.handleActions(AddEditServiceAction.OnDialogDismissed) }

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

    when {
        state.isLoading -> LoadingScreen()
        else -> {
            AddEditServiceScreenContent(
                screenState = state,
                onAction = viewModel::handleActions,
                modifier = modifier
            )
        }
    }
}

@Composable
fun AddEditServiceScreenContent(
    screenState: AddEditServiceScreenState,
    onAction: (AddEditServiceAction) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding(),
        contentPadding = PaddingValues(16.dp).withNavBarPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
//        stickyHeader {
//            KufarDetailsScreenHeader(
//                title = if (screenState.isEdit)
//                    stringResource(Res.string.service_edit_service)
//                else
//                    stringResource(Res.string.service_new_service),
//                showDeleteIcon = screenState.isEdit,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .kufarBackground(KufarTheme.colors.bg.grey.secondary),
//                onBackClicked = {
//                    onAction(AddEditServiceAction.OnCloseScreenClicked)
//                },
//                onDeleteClicked = {
//                    onAction(AddEditServiceAction.OnDeleteService)
//                }
//            )
//        }

        item {
            val title = rememberTextFieldState(screenState.title)

            OutlinedTextField(
                state = title,
                label = {
                    Text(stringResource(Res.string.service_title))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )
        }

//        item {
//            KufarDropDownRawBoxed(
//                currentItem = screenState.client,
//                selectableItems = screenState.clientsList,
//                hint = stringResource(Res.string.required_field),
//                colors = kufarDropDownColorsWithBackground(
//                    backgroundColor = KufarTheme.colors.border.grey.divider,
//                    borderColor = KufarColor.Transparent
//                ),
//                label = stringResource(Res.string.service_choose_client),
//                onItemSelected = { client ->
//                    client?.let {
//                        onAction(AddEditServiceAction.OnClientChanged(client))
//                    }
//                },
//                transformation = {
//                    it?.getFullName() ?: ""
//                },
//                itemContent = {
//                    Column {
//                        KufarText(
//                            text = it?.getFullName() ?: "",
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(8.dp)
//                        )
//                        if (it != screenState.clientsList.last()) {
//                            KufarMaxWidthDivider()
//                        }
//                    }
//                }
//            )
//        }

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
            val address = rememberTextFieldState(screenState.address)

            OutlinedTextField(
                state = address,
                label = {
                    Text(stringResource(Res.string.client_address))
                },
                placeholder = {
                    Text(stringResource(Res.string.client_address_placeholder))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
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
                        imeAction = ImeAction.Next
                    ),
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
//                        onAction(AddEditServiceAction.OnCurrencyChanged(it))
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
        when (val fields = screenState.serviceSpecificFields) {
            is ServiceSpecificFields.EducationServiceSpecificFields -> {
                item {
                    AddEditServiceEducationFieldsView(fields = fields, onAction = onAction)
                    HorizontalDivider()
                }
            }

            is ServiceSpecificFields.SportServiceSpecificFields -> {
                item {
                    AddEditServiceSportFieldsView(fields = fields, onAction = onAction)
                    HorizontalDivider()
                }
            }

            else -> {}
        }

        item {
//            KufarStatusSelector(
//                isPaid = screenState.isPaid,
//                isFinished = screenState.isFinished,
//                onFinishedChanged = {
//                    onAction(AddEditServiceAction.OnFinishedStatusChanged(it))
//                },
//                onPaidChanged = {
//                    onAction(AddEditServiceAction.OnPaidStatusChanged(it))
//                },
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
            HorizontalDivider()
        }

        item {
            val comment = rememberTextFieldState(screenState.comment)

            OutlinedTextField(
                state = comment,
                label = {
                    Text(stringResource(Res.string.service_comment))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )
        }

        item {
            TextButton(
                onClick = {
                    onAction(AddEditServiceAction.OnSaveServiceClicked)
                },
                enabled = screenState.isFinishButtonEnabled(),
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    if (screenState.isEdit)
                        stringResource(Res.string.service_update_data)
                    else
                        stringResource(Res.string.service_add_service)
                )
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