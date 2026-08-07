package com.dsankovsky.kmpclientplanner.ui.screens.service_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.ConfirmModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonDefaults
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicCard
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicIconButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHost
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTextArea
import com.dsankovsky.kmpclientplanner.ui.design.components.PaymentStatusButton
import com.dsankovsky.kmpclientplanner.ui.design.components.SessionStatusButton
import com.dsankovsky.kmpclientplanner.ui.design.elevationSm
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.minutesUntil
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIDayAndMonth
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIMoney
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_online
import kmpclientplanner.sharedui.generated.resources.service_confirm_deleting
import kmpclientplanner.sharedui.generated.resources.service_details_back
import kmpclientplanner.sharedui.generated.resources.service_details_close
import kmpclientplanner.sharedui.generated.resources.service_details_delete
import kmpclientplanner.sharedui.generated.resources.service_details_delete_confirm
import kmpclientplanner.sharedui.generated.resources.service_details_delete_title
import kmpclientplanner.sharedui.generated.resources.service_details_duration
import kmpclientplanner.sharedui.generated.resources.service_details_edit
import kmpclientplanner.sharedui.generated.resources.service_details_note
import kmpclientplanner.sharedui.generated.resources.service_homework
import kmpclientplanner.sharedui.generated.resources.service_homework_placeholder
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Экраны 05–07 — детали услуги. Шапка, статусы и заметка общие, отличается только
 * категорийная карточка: домашнее задание у репетитора, таблица упражнений у тренера,
 * фотографии у тату и бьюти.
 *
 * @param onClose крестик: закрывает детали внутри master-detail (панель на экране 04)
 * @param onBack шеврон «назад»: только когда детали открыты отдельным экраном — из
 *   истории занятий клиента
 */
@Composable
fun ServiceDetailsPane(
    serviceId: Long,
    onEditService: () -> Unit,
    onStatusUpdated: () -> Unit,
    onServiceDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    val viewModel: ServiceDetailsScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.collectWithLifecycle { event ->
        when (event) {
            ServiceDetailsScreenEvent.OpenEditServiceScreen -> onEditService()
            ServiceDetailsScreenEvent.StatusUpdated -> onStatusUpdated()
            ServiceDetailsScreenEvent.ServiceDeleted -> onServiceDeleted()
            ServiceDetailsScreenEvent.OnCloseScreen -> (onClose ?: onBack)?.invoke()
        }
    }

    LaunchedEffect(serviceId) {
        viewModel.handleActions(ServiceDetailsScreenAction.LoadData(serviceId))
    }

    Box(modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingScreen()
            else -> ServiceDetailsPaneContent(
                state = state,
                onAction = viewModel::handleActions,
                onClose = onClose,
                onBack = onBack,
            )
        }
        ServiceDetailsDialogs(state = state, onAction = viewModel::handleActions)
    }
}

@Composable
fun ServiceDetailsPaneContent(
    state: ServiceDetailsScreenState,
    onAction: (ServiceDetailsScreenAction) -> Unit,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrganicTheme.colors.bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 36.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        ServiceHeader(state = state, onAction = onAction, onClose = onClose, onBack = onBack)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PaymentStatusButton(
                isPaid = state.isPaid,
                onClick = { onAction(ServiceDetailsScreenAction.OnPaidStatusChanged) },
                textStyle = OrganicTheme.typography.button.copy(fontSize = 15.sp),
            )
            SessionStatusButton(
                isDone = state.isFinished,
                onClick = { onAction(ServiceDetailsScreenAction.OnFinishStatusChanged) },
                textStyle = OrganicTheme.typography.button.copy(fontSize = 15.sp),
            )
        }

        when (val fields = state.serviceSpecificFields) {
            is ServiceSpecificFields.EducationServiceSpecificFields ->
                HomeworkCard(fields.homework.orEmpty(), onAction)

            is ServiceSpecificFields.SportServiceSpecificFields -> WorkoutCard(state, onAction)

            is ServiceSpecificFields.TattooServiceSpecificFields -> PhotoCards(
                references = state.referenceImages,
                results = fields.images,
                note = state.comment,
                onDeleteResult = {
                    onAction(ServiceDetailsScreenAction.TattooServiceAction.OnImageDeleteClicked(it))
                },
            )

            is ServiceSpecificFields.BeautyServiceSpecificFields -> PhotoCards(
                references = emptyList(),
                results = fields.images,
                note = state.comment,
                onDeleteResult = {
                    onAction(ServiceDetailsScreenAction.BeautyServiceAction.OnImageDeleteClicked(it))
                },
            )

            null -> Unit
        }

        // У тату и бьюти заметка уже лежит в карточке результата.
        val noteInPhotoCard = state.service.serviceType == ServiceType.TATTOO ||
                state.service.serviceType == ServiceType.BEAUTY
        state.comment?.takeIf { it.isNotBlank() && !noteInPhotoCard }?.let { note ->
            OrganicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
                kicker = stringResource(Res.string.service_details_note),
                verticalGap = 10.dp,
            ) {
                OrganicText(note, style = OrganicTheme.typography.bodySm)
            }
        }
    }
}

@Composable
private fun ServiceHeader(
    state: ServiceDetailsScreenState,
    onAction: (ServiceDetailsScreenAction) -> Unit,
    onClose: (() -> Unit)?,
    onBack: (() -> Unit)?,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (onBack != null) {
            OrganicIconButton(
                icon = OrganicIcons.ChevronLeft,
                onClick = onBack,
                contentDescription = stringResource(Res.string.service_details_back),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            OrganicText(
                text = serviceKicker(state).uppercase(),
                style = OrganicTheme.typography.kicker,
                color = OrganicTheme.colors.accent,
            )
            OrganicText(
                text = state.clientName,
                style = OrganicTheme.typography.h2,
            )
            OrganicText(
                text = serviceMeta(state),
                style = OrganicTheme.typography.bodySm,
                color = OrganicTheme.colors.muted,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2)) {
            OrganicIconButton(
                icon = OrganicIcons.Pencil,
                onClick = { onAction(ServiceDetailsScreenAction.OnEditServiceClicked) },
                contentDescription = stringResource(Res.string.service_details_edit),
            )
            OrganicIconButton(
                icon = OrganicIcons.Trash,
                onClick = { onAction(ServiceDetailsScreenAction.OnDeleteServiceClicked) },
                contentDescription = stringResource(Res.string.service_details_delete),
            )
            if (onClose != null) {
                OrganicIconButton(
                    icon = OrganicIcons.X,
                    onClick = onClose,
                    contentDescription = stringResource(Res.string.service_details_close),
                )
            }
        }
    }
}

/** «Английский · 60 минут». */
@Composable
private fun serviceKicker(state: ServiceDetailsScreenState): String {
    val minutes = state.startDateTime.minutesUntil(state.endDateTime)
    return listOfNotNull(
        state.title.takeIf { it.isNotBlank() },
        minutes.takeIf { it > 0 }?.let { stringResource(Res.string.service_details_duration, it) },
    ).joinToString(" · ")
}

/** «29 июля, 15:00 – 16:00 · онлайн · 40,00 BYN». */
@Composable
private fun serviceMeta(state: ServiceDetailsScreenState): String {
    val place = when {
        state.isOnline -> stringResource(Res.string.client_online)
        else -> state.address?.takeIf { it.isNotBlank() }
    }
    return listOfNotNull(
        "${state.date.toUIDayAndMonth()}, ${state.time}",
        place,
        state.service.price?.toUIMoney(state.service.currency),
    ).joinToString(" · ")
}

/** Карточка «Домашнее задание» (05): одно поле, сохраняется само. */
@Composable
private fun HomeworkCard(
    homework: String,
    onAction: (ServiceDetailsScreenAction) -> Unit,
) {
    OrganicCard(
        modifier = Modifier
            .fillMaxWidth()
            .elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
        kicker = stringResource(Res.string.service_homework),
        verticalGap = 14.dp,
    ) {
        OrganicTextArea(
            value = homework,
            onValueChange = {
                onAction(ServiceDetailsScreenAction.EducationServiceAction.OnHomeworkChanged(it))
            },
            minHeight = 120.dp,
            background = OrganicTheme.colors.bg,
            placeholder = stringResource(Res.string.service_homework_placeholder),
        )
    }
}

@Composable
private fun ServiceDetailsDialogs(
    state: ServiceDetailsScreenState,
    onAction: (ServiceDetailsScreenAction) -> Unit,
) {
    val dialog = state.dialog ?: return
    val onDismiss = { onAction(ServiceDetailsScreenAction.CloseDialog) }

    OrganicModalHost(onDismissRequest = onDismiss) {
        when (dialog) {
            ServiceDetailsDialog.ConfirmDeleting -> ConfirmModal(
                title = stringResource(Res.string.service_details_delete_title),
                text = stringResource(Res.string.service_confirm_deleting),
                confirmText = stringResource(Res.string.service_details_delete_confirm),
                onConfirm = { onAction(ServiceDetailsScreenAction.OnDeleteServiceConfirmed) },
                onDismiss = onDismiss,
                destructive = true,
            )

            is ServiceDetailsDialog.NewExercise -> NewExerciseModal(
                prefill = dialog.prefill,
                onAdd = { title, sets, repeats, weight ->
                    onAction(
                        ServiceDetailsScreenAction.SportServiceAction.OnExerciseAdded(
                            title = title,
                            setsCount = sets,
                            repeats = repeats,
                            weight = weight,
                        ),
                    )
                },
                onDismiss = onDismiss,
            )

            ServiceDetailsDialog.PickExercise -> PickExerciseModal(
                exercises = state.knownExercises,
                onPick = {
                    onAction(ServiceDetailsScreenAction.SportServiceAction.OnKnownExercisePicked(it))
                },
                onDismiss = onDismiss,
            )
        }
    }
}

@Preview
@Composable
private fun ServiceDetailsEducationPreview() {
    OrganicTheme {
        ServiceDetailsPaneContent(
            state = educationPreviewState(),
            onAction = {},
        )
    }
}

internal fun educationPreviewState() = ServiceDetailsScreenState(
    isLoading = false,
    title = "Английский",
    clientName = "Анна Ковалёва",
    time = "15:00 - 16:00",
    isOnline = true,
    comment = "Прошли Present Perfect, слабое место — вопросительная форма.",
    service = BaseService(price = 40f, currency = CurrencyItem.BYN, serviceType = ServiceType.EDUCATION),
    serviceSpecificFields = ServiceSpecificFields.EducationServiceSpecificFields(
        isOnline = true,
        homework = "Unit 4, упр. 3–7 письменно. Эссе 180 слов «My summer» — к 5 августа.",
    ),
)
