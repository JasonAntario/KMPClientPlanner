package com.dsankovsky.kmpclientplanner.ui.screens.service_details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.animation.SlideBottomAnimatedVisibility
import com.dsankovsky.kmpclientplanner.ui.components.ToolbarView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.edgeToEdgeBottomPadding
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields.ServiceBeautyFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields.ServiceEducationFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields.ServiceSportFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.specific_fields.ServiceTattooFieldsView
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_address
import kmpclientplanner.sharedui.generated.resources.client_price
import kmpclientplanner.sharedui.generated.resources.service_comment
import kmpclientplanner.sharedui.generated.resources.service_date
import kmpclientplanner.sharedui.generated.resources.service_details
import kmpclientplanner.sharedui.generated.resources.service_time
import kmpclientplanner.sharedui.generated.resources.service_update_data
import kmpclientplanner.sharedui.generated.resources.statistics_client
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ServiceDetailsScreen(
    serviceId: Long,
    onEvent: (ServiceDetailsScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel: ServiceDetailsScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.collectWithLifecycle {
        onEvent(it)
    }

    LaunchedEffect(Unit) {
        viewModel.handleActions(ServiceDetailsScreenAction.LoadData(serviceId))
    }

    Scaffold(
        topBar = {
            ToolbarView(
                title = stringResource(Res.string.service_details),
                onBackClicked = {
                    viewModel.handleActions(ServiceDetailsScreenAction.OnCloseScreenClicked)
                },
                actionIcon = Icons.Default.Edit,
                actionIconColor = MaterialTheme.colorScheme.onSurface,
                onActionClicked = {
                    viewModel.handleActions(ServiceDetailsScreenAction.OnEditServiceClicked)
                }
            )
        }
    ) { paddingValues ->

        when {
            state.isLoading -> LoadingScreen()
            else -> {
                ServiceDetailsScreenContent(
                    state = state,
                    onAction = viewModel::handleActions,
                    modifier = modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ServiceDetailsScreenContent(
    state: ServiceDetailsScreenState,
    onAction: (ServiceDetailsScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 79.dp,
                top = 16.dp
            ).withNavBarPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                Text(
                    state.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                ServiceDetailsList(state)
            }

            state.comment?.let { comment ->
                item {
                    Text(
                        stringResource(Res.string.service_comment),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        comment,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (val field = state.serviceSpecificFields) {
                is ServiceSpecificFields.BeautyServiceSpecificFields -> {
                    item {
                        ServiceBeautyFieldsView(fields = field, onAction = onAction)
                    }
                }

                is ServiceSpecificFields.EducationServiceSpecificFields -> {
                    item {
                        ServiceEducationFieldsView(fields = field, onAction = onAction)
                    }
                }

                is ServiceSpecificFields.SportServiceSpecificFields -> {
                    item {
                        ServiceSportFieldsView(
                            fields = field,
                            onAction = onAction,
                            exercisesList = state.exercisesList.map { it.title }
                        )
                    }
                }

                is ServiceSpecificFields.TattooServiceSpecificFields -> {
                    item {
                        ServiceTattooFieldsView(fields = field, onAction = onAction)
                    }
                }

                null -> {

                }
            }
        }

        SlideBottomAnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = state.serviceSpecificFields != state.initialServiceSpecificFields
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .edgeToEdgeBottomPadding(0.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {

                TextButton(
                    onClick = {
                        onAction(ServiceDetailsScreenAction.OnUpdateDataClicked)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(Res.string.service_update_data),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceDetailsList(
    state: ServiceDetailsScreenState,
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

        DetailItem(
            stringResource(Res.string.service_date),
            state.service.getServiceDate()
        )
        DetailItem(
            stringResource(Res.string.service_time),
            state.time
        )
        DetailItem(
            stringResource(Res.string.statistics_client),
            state.clientName
        )
        state.address?.let { address ->
            DetailItem(
                stringResource(Res.string.client_address),
                address
            )
        }
        state.price?.let { address ->
            DetailItem(
                stringResource(Res.string.client_price),
                address
            )
        }
    }
}

@Composable
fun DetailItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 10f), 0f)
            val dottedLineColor = MaterialTheme.colorScheme.primary
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
            ) {
                drawLine(
                    color = dottedLineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = pathEffect
                )
            }
        }
        Text(
            description,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewServiceDetailsScreenContent() {
    ClientPlannerTheme {
        ServiceDetailsScreenContent(
            state = ServiceDetailsScreenState(
                title = "Тату собаки",
                date = getCurrentDateTime().date,
                time = "10:00 - 11:00",
                clientName = "Отис Пес",
                address = "ул. Песья, 33",
                comment = "Ну гавкает как пес полный"
            ),
            {}
        )
    }
}