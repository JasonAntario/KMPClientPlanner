package com.dsankovsky.kmpclientplanner.ui.screens.client_details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Videocam
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.ui.components.ShortNameBoxView
import com.dsankovsky.kmpclientplanner.ui.components.ToolbarView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.extensions.toUITime
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.ClientScreenDialog
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.specific_fields.TattooClientFieldsView
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.autofill_prolong_description
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.client_address
import kmpclientplanner.sharedui.generated.resources.client_choose_format_education
import kmpclientplanner.sharedui.generated.resources.client_choose_format_sport
import kmpclientplanner.sharedui.generated.resources.client_details_fill_lessons
import kmpclientplanner.sharedui.generated.resources.client_details_fill_trainings
import kmpclientplanner.sharedui.generated.resources.client_details_lessons
import kmpclientplanner.sharedui.generated.resources.client_details_services
import kmpclientplanner.sharedui.generated.resources.client_details_training
import kmpclientplanner.sharedui.generated.resources.client_level
import kmpclientplanner.sharedui.generated.resources.client_offline
import kmpclientplanner.sharedui.generated.resources.client_online
import kmpclientplanner.sharedui.generated.resources.client_phone
import kmpclientplanner.sharedui.generated.resources.client_price
import kmpclientplanner.sharedui.generated.resources.client_shoud_continue_autofill
import kmpclientplanner.sharedui.generated.resources.confirm
import kmpclientplanner.sharedui.generated.resources.service_comment
import kmpclientplanner.sharedui.generated.resources.service_crossing
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
    val lessonsTitle = stringResource(Res.string.client_details_lessons)
    val fillLessonsText = stringResource(Res.string.client_details_fill_lessons)
    val trainingTitle = stringResource(Res.string.client_details_training)
    val fillTrainingsText = stringResource(Res.string.client_details_fill_trainings)

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp).withNavBarPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ClientDetailsHeader(
                    state = state,
                    onEditClicked = { onAction(ClientDetailsActions.OnEditClientClicked) }
                )
            }

            item {
                ClientInfoGrid(state = state)
            }

            state.comment?.let { comment ->
                item {
                    LabeledCard(
                        label = stringResource(Res.string.service_comment),
                        body = comment
                    )
                }
            }

            when (val fields = state.clientSpecificFields) {
                is ClientSpecificFields.EducationClientSpecificFields -> {
                    scheduleSection(
                        title = lessonsTitle,
                        fillButtonText = fillLessonsText,
                        lessons = fields.lessonDateTimeList,
                        onFillClicked = { onAction(ClientDetailsActions.FillServicesClicked) }
                    )
                }

                is ClientSpecificFields.SportClientSpecificFields -> {
                    scheduleSection(
                        title = trainingTitle,
                        fillButtonText = fillTrainingsText,
                        lessons = fields.lessonDateTimeList,
                        onFillClicked = { onAction(ClientDetailsActions.FillServicesClicked) }
                    )
                }

                is ClientSpecificFields.TattooClientSpecificFields -> {
                    item {
                        TattooClientFieldsView(
                            fields = fields,
                            onAction = onAction
                        )
                    }
                }

                null -> {}
            }

            if (state.showServicesHistory) {
                item {
                    ServicesHistorySection(
                        onClick = { onAction(ClientDetailsActions.ShowServicesHistory) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientDetailsHeader(
    state: ClientDetailsScreenState,
    onEditClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name = state.clientName.ifBlank { state.client.getFullName() }
    val subline = listOfNotNull(
        state.client.serviceSubtype?.takeIf { it.isNotBlank() },
        state.client.serviceType.toUIName()
    ).joinToString(" · ")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShortNameBoxView(
            text = state.clientShortName,
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(64.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = name,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subline.isNotEmpty()) {
                Text(
                    text = subline,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        CircularIconButton(
            icon = Icons.Rounded.Edit,
            onClick = onEditClicked
        )
    }
}

@Composable
private fun CircularIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

private data class InfoField(
    val label: String,
    val value: String,
    val icon: ImageVector? = null
)

@Composable
private fun ClientInfoGrid(
    state: ClientDetailsScreenState,
    modifier: Modifier = Modifier
) {
    val phoneLabel = stringResource(Res.string.client_phone)
    val addressLabel = stringResource(Res.string.client_address)
    val priceLabel = stringResource(Res.string.client_price)
    val levelLabel = stringResource(Res.string.client_level)
    val onlineText = stringResource(Res.string.client_online)
    val offlineText = stringResource(Res.string.client_offline)
    val eduFormatLabel = stringResource(Res.string.client_choose_format_education)
    val sportFormatLabel = stringResource(Res.string.client_choose_format_sport)

    val items = buildList {
        state.phone?.let { add(InfoField(phoneLabel, it)) }
        when (val fields = state.clientSpecificFields) {
            is ClientSpecificFields.EducationClientSpecificFields -> {
                add(
                    InfoField(
                        label = eduFormatLabel,
                        value = if (fields.isOnline) onlineText else offlineText,
                        icon = if (fields.isOnline) Icons.Rounded.Videocam else Icons.Rounded.Place
                    )
                )
                fields.level?.takeIf { it.isNotBlank() }?.let { add(InfoField(levelLabel, it)) }
            }

            is ClientSpecificFields.SportClientSpecificFields -> {
                add(
                    InfoField(
                        label = sportFormatLabel,
                        value = if (fields.isOnline) onlineText else offlineText,
                        icon = if (fields.isOnline) Icons.Rounded.Videocam else Icons.Rounded.Place
                    )
                )
            }

            else -> {}
        }
        state.address?.let { add(InfoField(addressLabel, it)) }
        state.price?.let { add(InfoField(priceLabel, it)) }
    }

    if (items.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { field ->
                    InfoCard(field = field, modifier = Modifier.weight(1f))
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    field: InfoField,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = field.label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            field.icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = field.value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun LabeledCard(
    label: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.scheduleSection(
    title: String,
    fillButtonText: String,
    lessons: List<ServiceDateTime>,
    onFillClicked: () -> Unit
) {
    item {
        SectionHeader(icon = Icons.Rounded.Schedule, title = title)
    }
    items(lessons) { lesson ->
        val day = lesson.dayOfWeek.name
        val startTime = lesson.time.toUITime()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = day,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = startTime,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    if (lessons.isNotEmpty()) {
        item {
            TextButton(
                onClick = onFillClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = fillButtonText,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ServicesHistorySection(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionHeader(
            icon = Icons.Rounded.History,
            title = stringResource(Res.string.client_details_services)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(Res.string.client_details_services),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
        }
    }
}


@PreviewLightDark
@Composable
private fun PreviewEducation() {
    ClientPlannerTheme {
        ClientDetailsScreenContent(
            state = ClientDetailsScreenState(
                isLoading = false,
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
                isLoading = false,
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
