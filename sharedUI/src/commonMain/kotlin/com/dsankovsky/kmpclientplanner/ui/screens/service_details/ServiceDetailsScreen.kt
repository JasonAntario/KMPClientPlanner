package com.dsankovsky.kmpclientplanner.ui.screens.service_details

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.animation.SlideBottomAnimatedVisibility
import com.dsankovsky.kmpclientplanner.ui.components.DoneStatusPill
import com.dsankovsky.kmpclientplanner.ui.components.PaidStatusPill
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                ServiceDetailsHeader(state)
            }

            item {
                ServiceDetailsInfoGrid(state)
            }

            state.comment?.let { comment ->
                item {
                    CommentCard(comment)
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
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

                Button(
                    onClick = {
                        onAction(ServiceDetailsScreenAction.OnUpdateDataClicked)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(Res.string.service_update_data),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceDetailsHeader(
    state: ServiceDetailsScreenState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (state.title.isNotBlank()) {
                Text(
                    text = state.title.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = state.clientName,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                MetaChip(Icons.Rounded.Schedule, state.time)
                state.address?.let { MetaChip(Icons.Rounded.Place, it) }
            }
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DoneStatusPill(isDone = state.isFinished)
            PaidStatusPill(isPaid = state.isPaid)
        }
    }
}

@Composable
private fun MetaChip(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    if (text.isBlank()) return
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ServiceDetailsInfoGrid(
    state: ServiceDetailsScreenState,
    modifier: Modifier = Modifier
) {
    val items = buildList {
        add(InfoEntry(stringResource(Res.string.service_date), state.service.getServiceDate(), Icons.Rounded.CalendarMonth))
        add(InfoEntry(stringResource(Res.string.service_time), state.time, Icons.Rounded.Schedule))
        state.price?.let { add(InfoEntry(stringResource(Res.string.client_price), it, Icons.Rounded.Payments)) }
        state.address?.let { add(InfoEntry(stringResource(Res.string.client_address), it, Icons.Rounded.Place)) }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { entry ->
                    InfoCard(entry = entry, modifier = Modifier.weight(1f))
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private data class InfoEntry(val label: String, val value: String, val icon: ImageVector)

@Composable
private fun InfoCard(entry: InfoEntry, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = entry.icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = entry.label,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = entry.value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CommentCard(comment: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(Res.string.service_comment),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = comment,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
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
                price = "50 BYN",
                comment = "Ну гавкает как пес полный"
            ),
            {}
        )
    }
}
