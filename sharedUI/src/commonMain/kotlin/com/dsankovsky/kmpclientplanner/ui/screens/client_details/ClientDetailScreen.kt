package com.dsankovsky.kmpclientplanner.ui.screens.client_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.ui.components.CardView
import com.dsankovsky.kmpclientplanner.ui.components.ShortNameBoxView
import com.dsankovsky.kmpclientplanner.ui.components.ToolbarView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.extensions.toUITime
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.ClientScreenDialog
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.specific_fields.TattooClientFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.DetailItem
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.autofill_prolong_description
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.client_address
import kmpclientplanner.sharedui.generated.resources.client_details_fill_lessons
import kmpclientplanner.sharedui.generated.resources.client_details_fill_trainings
import kmpclientplanner.sharedui.generated.resources.client_details_lessons
import kmpclientplanner.sharedui.generated.resources.client_details_services
import kmpclientplanner.sharedui.generated.resources.client_details_training
import kmpclientplanner.sharedui.generated.resources.client_phone
import kmpclientplanner.sharedui.generated.resources.client_price
import kmpclientplanner.sharedui.generated.resources.client_shoud_continue_autofill
import kmpclientplanner.sharedui.generated.resources.confirm
import kmpclientplanner.sharedui.generated.resources.service_comment
import kmpclientplanner.sharedui.generated.resources.service_crossing
import kmpclientplanner.sharedui.generated.resources.service_details
import kmpclientplanner.sharedui.generated.resources.service_subtype
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClientDetailsScreen(
    clientId: Long,
    onEvent: (ClientDetailsEvents) -> Unit,
    modifier: Modifier = Modifier,
) {

    val viewModel: ClientDetailsViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.collectWithLifecycle {
        onEvent(it)
    }

    LaunchedEffect(clientId) {
        viewModel.handleActions(ClientDetailsActions.LoadData(clientId))
    }

    state.showDialog?.let { dialog ->
        val onDismiss: () -> Unit = when (dialog) {
            ClientScreenDialog.ConfirmAutofillServices -> {
                { viewModel.handleActions(ClientDetailsActions.OnAutofillDismissClicked) }
            }

            is ClientScreenDialog.ServicesCrossing -> {
                { viewModel.handleActions(ClientDetailsActions.CloseClientDialog) }
            }

            else -> {
                { viewModel.handleActions(ClientDetailsActions.CloseClientDialog) }
            }
        }
        val onConfirm: () -> Unit = when (dialog) {
            ClientScreenDialog.ConfirmAutofillServices -> {
                { viewModel.handleActions(ClientDetailsActions.OnAutofillConfirmClicked) }
            }

            is ClientScreenDialog.ServicesCrossing -> {
                { viewModel.handleActions(ClientDetailsActions.OnAutofillWithCrossingConfirmClicked) }
            }

            else -> {
                { viewModel.handleActions(ClientDetailsActions.CloseClientDialog) }
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
                            text = stringResource(Res.string.autofill_prolong_description),
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

                    else -> {}
                }
            }
        )
    }

    Scaffold(
        topBar = {
            ToolbarView(
                title = state.client.getFullName(),
                actionIcon = Icons.Default.Edit,
                onActionClicked = {
                    viewModel.handleActions(ClientDetailsActions.OnEditClientClicked)
                },
                onBackClicked = {
                    viewModel.handleActions(ClientDetailsActions.OnCloseScreenClicked)
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingScreen()
            else -> {
                ClientDetailsScreenContent(
                    state = state,
                    onAction = viewModel::handleActions,
                    modifier = modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun ClientDetailsScreenContent(
    state: ClientDetailsScreenState,
    onAction: (ClientDetailsActions) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp).withNavBarPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ShortNameBoxView(
                        text = state.clientShortName,
                        modifier = Modifier.size(150.dp)
                    )
                }
            }

            item {
                ClientDetailsList(state = state)
            }

            state.comment?.let { comment ->
                item {
                    Text(
                        text = stringResource(Res.string.service_comment),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = comment,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }


            when (val fields = state.clientSpecificFields) {
                is ClientSpecificFields.EducationClientSpecificFields -> {
                    item {
                        Text(
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            text = stringResource(Res.string.client_details_lessons),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    items(fields.lessonDateTimeList) { training ->
                        val day = training.dayOfWeek.name
                        val startTime = training.time.toUITime()
                        Text(
                            text = "$day, $startTime",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    if (fields.lessonDateTimeList.isNotEmpty()) {
                        item {
                            TextButton(
                                onClick = {
                                    onAction(ClientDetailsActions.FillServicesClicked)
                                },
                                modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                            ) {
                                Text(
                                    stringResource(Res.string.client_details_fill_lessons),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                is ClientSpecificFields.SportClientSpecificFields -> {
                    item {
                        Text(
                            stringResource(Res.string.client_details_training),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    items(fields.lessonDateTimeList) { training ->
                        val day = training.dayOfWeek.name
                        val startTime = training.time.toUITime()
                        Text(
                            "$day, $startTime",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    if (fields.lessonDateTimeList.isNotEmpty()) {
                        item {
                            TextButton(
                                onClick = {
                                    onAction(ClientDetailsActions.FillServicesClicked)
                                },
                                modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                            ) {
                                Text(
                                    stringResource(Res.string.client_details_fill_trainings),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                is ClientSpecificFields.TattooClientSpecificFields -> {
                    item {
                        TattooClientFieldsView(
                            fields = fields,
                            onAction = onAction,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                }

                null -> {}
            }

            if (state.showServicesHistory) {
                item {
                    CardView(
                        modifier = Modifier.padding(top = 12.dp),
                        onClick = {
                            onAction(ClientDetailsActions.ShowServicesHistory)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(Res.string.client_details_services))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ClientDetailsList(
    state: ClientDetailsScreenState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            stringResource(Res.string.service_details),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        state.phone?.let {
            DetailItem(
                stringResource(Res.string.client_phone),
                it
            )
        }

        state.address?.let {
            DetailItem(
                stringResource(Res.string.client_address),
                it
            )
        }

        state.price?.let {
            DetailItem(
                stringResource(Res.string.client_price),
                it
            )
        }


        DetailItem(
            stringResource(Res.string.service_subtype),
            state.client.serviceType.toUIName()
        )
    }
}


@PreviewLightDark
@Composable
private fun PreviewEducation() {
    ClientPlannerTheme {
        ClientDetailsScreenContent(
            state = ClientDetailsScreenState(
                price = "BYN 35,00",
                clientShortName = "ИО",
                clientName = "Игорь Отисов",
                address = "ул. Покемонов 35",
                phone = "291440022",
                comment = "Он странный чел",
                clientSpecificFields = ClientSpecificFields.EducationClientSpecificFields(
                    lessonDateTimeList = listOf(
                        ServiceDateTime()
                    ),
                    level = "Stupid as hell",
                    isOnline = true
                ),

                showServicesHistory = true
            ),
            onAction = {}
        )
    }
}


@PreviewLightDark
@Composable
private fun PreviewSport() {
    ClientPlannerTheme {
        ClientDetailsScreenContent(
            state = ClientDetailsScreenState(
                price = "BYN 35,00",
                clientShortName = "ИО",
                clientName = "Игорь Отисов",
                address = "ул. Покемонов 35",
                phone = "291440022",
                comment = "Он странный чел",
                clientSpecificFields = ClientSpecificFields.SportClientSpecificFields(
                    lessonDateTimeList = listOf(
                        ServiceDateTime()
                    ),
                    weight = "111 kg",
                    isOnline = true
                ),
                showServicesHistory = true
            ),
            onAction = {}
        )
    }
}