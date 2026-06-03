package com.dsankovsky.kmpclientplanner.ui.screens.client_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.ui.animation.SlideBottomAnimatedVisibility
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUITime
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.specific_fields.TattooClientFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.DetailItem
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_address
import kmpclientplanner.sharedui.generated.resources.client_details_fill_lessons
import kmpclientplanner.sharedui.generated.resources.client_details_fill_trainings
import kmpclientplanner.sharedui.generated.resources.client_details_lessons
import kmpclientplanner.sharedui.generated.resources.client_details_services
import kmpclientplanner.sharedui.generated.resources.client_details_training
import kmpclientplanner.sharedui.generated.resources.client_phone
import kmpclientplanner.sharedui.generated.resources.client_price
import kmpclientplanner.sharedui.generated.resources.service_comment
import kmpclientplanner.sharedui.generated.resources.service_details
import kmpclientplanner.sharedui.generated.resources.service_subtype
import kmpclientplanner.sharedui.generated.resources.service_update_data
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

    LaunchedEffect(Unit) {
        viewModel.handleActions(ClientDetailsActions.LoadData(clientId))
    }

//    state.showDialog?.let { dialog ->
//        KufarDialog(
//            onNegativeClick = {
//                when (dialog) {
//                    ClientScreenDialog.ConfirmAutofillServices -> {
//                        viewModel.handleActions(ClientDetailsActions.OnAutofillDismissClicked)
//                    }
//
//                    is ClientScreenDialog.ServicesCrossing -> {
//                        viewModel.handleActions(ClientDetailsActions.CloseClientDialog)
//                    }
//
//                    else -> {}
//                }
//            },
//            onPositiveClick = {
//                when (dialog) {
//                    ClientScreenDialog.ConfirmAutofillServices -> {
//                        viewModel.handleActions(ClientDetailsActions.OnAutofillConfirmClicked)
//                    }
//
//                    is ClientScreenDialog.ServicesCrossing -> {
//                        viewModel.handleActions(ClientDetailsActions.OnAutofillWithCrossingConfirmClicked)
//                    }
//
//                    else -> {}
//                }
//            }
//        ) {
//            when (dialog) {
//                ClientScreenDialog.ConfirmAutofillServices -> {
//                    KufarText(
//                        stringResource(Res.string.autofill_prolong_description),
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
//
//                else -> {}
//            }
//        }
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.clientName) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.handleActions(ClientDetailsActions.OnCloseScreenClicked)
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
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

//            item {
//                KufarDetailsScreenHeader(
//                    title = state.clientName,
//                    showEditIcon = true,
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    onBackClicked = {
//                        onAction(ClientDetailsActions.OnCloseScreenClicked)
//                    },
//                    onEditClicked = {
//                        onAction(ClientDetailsActions.OnEditClientClicked)
//                    }
//                )
//            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer
                            )
                    ) {
                        Text(
                            text = state.clientShortName,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            item {
                ClientDetailsList(state = state)
            }

            state.comment?.let { comment ->
                item {
                    Text(
                        stringResource(Res.string.service_comment),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        comment,
                        color = MaterialTheme.colorScheme.primary,
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
                            text = stringResource(Res.string.client_details_lessons),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    items(fields.lessonDateTimeList) { training ->
                        val day = training.dayOfWeek.name
                        val startTime = training.time.toUITime()
                        Text(
                            "$day, $startTime",
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    if (fields.lessonDateTimeList.isNotEmpty()) {
                        item {
                            TextButton(
                                onClick = {
                                    onAction(ClientDetailsActions.FillServicesClicked)
                                },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(
                                    stringResource(Res.string.client_details_fill_lessons),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }

                is ClientSpecificFields.SportClientSpecificFields -> {
                    item {
                        Text(
                            stringResource(Res.string.client_details_training),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    items(fields.lessonDateTimeList) { training ->
                        val day = training.dayOfWeek.name
                        val startTime = training.time.toUITime()
                        Text("$day, $startTime")
                    }

                    if (fields.lessonDateTimeList.isNotEmpty()) {
                        item {
                            TextButton(
                                onClick = {
                                    onAction(ClientDetailsActions.FillServicesClicked)
                                },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(stringResource(Res.string.client_details_fill_trainings))
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
                    Card(
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
                            Image(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }

        SlideBottomAnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            visible = state.clientSpecificFields != state.initialClientSpecificFields
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = {
                        onAction(ClientDetailsActions.OnUpdateDataClicked)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.service_update_data))
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
            state.client.getServiceName()
        )
    }
}


@PreviewLightDark
@Composable
private fun PreveiwClientDetailsScreen() {
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
                    )
                ),
                showServicesHistory = true
            ),
            onAction = {}
        )
    }
}