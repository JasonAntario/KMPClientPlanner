package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.ConfirmModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonDefaults
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicDateField
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicField
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicIconButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalActions
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHeader
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHost
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalPanel
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalWidth
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicSelect
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTextArea
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTextField
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTimeField
import com.dsankovsky.kmpclientplanner.ui.design.components.PaymentStatusButton
import com.dsankovsky.kmpclientplanner.ui.design.components.SegmentedControl
import com.dsankovsky.kmpclientplanner.ui.design.components.SessionStatusButton
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.client_address
import kmpclientplanner.sharedui.generated.resources.client_choose_format_education
import kmpclientplanner.sharedui.generated.resources.client_choose_format_sport
import kmpclientplanner.sharedui.generated.resources.client_offline
import kmpclientplanner.sharedui.generated.resources.client_online
import kmpclientplanner.sharedui.generated.resources.client_form_category
import kmpclientplanner.sharedui.generated.resources.client_form_discard_confirm
import kmpclientplanner.sharedui.generated.resources.client_form_discard_dismiss
import kmpclientplanner.sharedui.generated.resources.client_form_save
import kmpclientplanner.sharedui.generated.resources.client_form_time
import kmpclientplanner.sharedui.generated.resources.service_comment
import kmpclientplanner.sharedui.generated.resources.service_confirm_deleting
import kmpclientplanner.sharedui.generated.resources.service_currenct
import kmpclientplanner.sharedui.generated.resources.service_details_delete_confirm
import kmpclientplanner.sharedui.generated.resources.service_details_delete_title
import kmpclientplanner.sharedui.generated.resources.service_form_client
import kmpclientplanner.sharedui.generated.resources.service_form_client_placeholder
import kmpclientplanner.sharedui.generated.resources.service_form_crossing_confirm
import kmpclientplanner.sharedui.generated.resources.service_form_crossing_dismiss
import kmpclientplanner.sharedui.generated.resources.service_form_crossing_text
import kmpclientplanner.sharedui.generated.resources.service_form_crossing_title
import kmpclientplanner.sharedui.generated.resources.service_form_date
import kmpclientplanner.sharedui.generated.resources.service_form_delete
import kmpclientplanner.sharedui.generated.resources.service_form_discard_text
import kmpclientplanner.sharedui.generated.resources.service_form_discard_title
import kmpclientplanner.sharedui.generated.resources.service_form_duration
import kmpclientplanner.sharedui.generated.resources.service_form_name
import kmpclientplanner.sharedui.generated.resources.service_form_new_address
import kmpclientplanner.sharedui.generated.resources.service_form_subtitle
import kmpclientplanner.sharedui.generated.resources.service_form_title
import kmpclientplanner.sharedui.generated.resources.service_homework
import kmpclientplanner.sharedui.generated.resources.service_homework_placeholder
import kmpclientplanner.sharedui.generated.resources.service_price
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * М1 — форма услуги: создание и редактирование одним окном.
 *
 * Дата, время и длительность — текстовые поля, как в макете; в модели по-прежнему две даты,
 * поэтому конец занятия пересчитывается от начала и длительности. Подтверждения (М6, М8, М9)
 * подменяют панель формы, а не открывают второй скрим поверх первого.
 */
@Composable
fun ServiceFormModal(
    serviceId: Long?,
    onEvent: (AddEditServiceEvent) -> Unit,
    fullScreen: Boolean = false,
) {
    val viewModel: AddEditServiceViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.collectWithLifecycle { onEvent(it) }

    LaunchedEffect(serviceId) {
        viewModel.handleActions(AddEditServiceAction.LoadServiceData(serviceId))
    }

    OrganicModalHost(
        onDismissRequest = { viewModel.handleActions(AddEditServiceAction.OnCloseRequested) },
    ) {
        ServiceFormModalContent(
            state = state,
            onAction = viewModel::handleActions,
            fullScreen = fullScreen,
        )
    }
}

@Composable
fun ServiceFormModalContent(
    state: AddEditServiceScreenState,
    onAction: (AddEditServiceAction) -> Unit,
    modifier: Modifier = Modifier,
    fullScreen: Boolean = false,
) {
    state.showDialog?.let { dialog ->
        ServiceFormDialog(dialog = dialog, onAction = onAction, fullScreen = fullScreen)
        return
    }

    OrganicModal(modifier = modifier, width = OrganicModalWidth.Form, fullScreen = fullScreen) {
        OrganicModalHeader(
            title = stringResource(Res.string.service_form_title),
            subtitle = stringResource(Res.string.service_form_subtitle),
            onClose = { onAction(AddEditServiceAction.OnCloseRequested) },
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().height(FormMinHeight)) { LoadingScreen() }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(FormGap),
            ) {
                ServiceFields(state = state, onAction = onAction, stacked = fullScreen)
            }
        }

        OrganicModalActions(
            destructive = if (state.isEdit) {
                {
                    OrganicButton(
                        text = stringResource(Res.string.service_form_delete),
                        onClick = { onAction(AddEditServiceAction.OnDeleteService) },
                        colors = OrganicButtonDefaults.ghost(),
                        icon = OrganicIcons.Trash,
                    )
                }
            } else {
                null
            },
        ) {
            OrganicButton(
                text = stringResource(Res.string.cancel),
                onClick = { onAction(AddEditServiceAction.OnCloseRequested) },
                colors = OrganicButtonDefaults.secondary(),
            )
            OrganicButton(
                text = stringResource(Res.string.client_form_save),
                onClick = { onAction(AddEditServiceAction.OnSaveServiceClicked) },
                enabled = state.isFinishButtonEnabled(),
            )
        }
    }
}

@Composable
private fun ColumnScope.ServiceFields(
    state: AddEditServiceScreenState,
    onAction: (AddEditServiceAction) -> Unit,
    stacked: Boolean,
) {
    // Название — первым и во всю ширину: с него начинают заполнять форму, и в одну колонку
    // с датой оно жалось до многоточий на длинных названиях услуг.
    OrganicField(label = stringResource(Res.string.service_form_name), required = true) {
        OrganicTextField(
            value = state.title,
            onValueChange = { onAction(AddEditServiceAction.OnTitleChanged(it)) },
        )
    }

    OrganicField(label = stringResource(Res.string.service_form_client), required = true) {
        OrganicSelect(
            value = state.client,
            items = state.clientsList,
            onSelect = { onAction(AddEditServiceAction.OnClientChanged(it)) },
            itemLabel = { it.getFullName() },
            placeholder = stringResource(Res.string.service_form_client_placeholder),
        )
    }

    FormRow(stacked) {
        OrganicField(label = stringResource(Res.string.service_form_date), modifier = Modifier.cell(1.2f)) {
            OrganicDateField(
                date = state.startDateTime.date,
                onDateSelected = { onAction(AddEditServiceAction.OnDateChanged(it)) },
            )
        }
        OrganicField(label = stringResource(Res.string.client_form_time), modifier = Modifier.cell()) {
            OrganicTimeField(
                time = state.startDateTime.time,
                onTimeSelected = { onAction(AddEditServiceAction.OnTimeChanged(it)) },
            )
        }
        OrganicField(label = stringResource(Res.string.service_form_duration), modifier = Modifier.cell()) {
            OrganicTextField(
                value = state.durationText,
                onValueChange = { onAction(AddEditServiceAction.OnDurationChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
    }

    FormRow(stacked) {
        OrganicField(label = stringResource(Res.string.service_price), modifier = Modifier.cell()) {
            OrganicTextField(
                value = state.price,
                onValueChange = { onAction(AddEditServiceAction.OnPriceChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }
        OrganicField(label = stringResource(Res.string.service_currenct), modifier = Modifier.cell()) {
            OrganicSelect(
                value = state.currency,
                items = state.currenciesList,
                onSelect = { onAction(AddEditServiceAction.OnCurrencyChanged(it)) },
                itemLabel = { it.code },
            )
        }
    }

    AddressField(state = state, onAction = onAction)

    OrganicField(label = stringResource(Res.string.service_comment)) {
        OrganicTextArea(
            value = state.comment,
            onValueChange = { onAction(AddEditServiceAction.OnCommentChanged(it)) },
            minHeight = 64.dp,
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2)) {
        PaymentStatusButton(
            isPaid = state.isPaid,
            onClick = { onAction(AddEditServiceAction.OnPaidStatusChanged(!state.isPaid)) },
            textStyle = OrganicTheme.typography.button.copy(fontSize = 13.sp),
        )
        SessionStatusButton(
            isDone = state.isFinished,
            onClick = { onAction(AddEditServiceAction.OnFinishedStatusChanged(!state.isFinished)) },
            textStyle = OrganicTheme.typography.button.copy(fontSize = 13.sp),
        )
    }

    CategoryFields(state = state, onAction = onAction, stacked = stacked)
}

/**
 * Адрес: селект по ранее введённым и кнопка «новый адрес», которая переключает поле
 * в свободный ввод — как в макете.
 */
@Composable
private fun AddressField(
    state: AddEditServiceScreenState,
    onAction: (AddEditServiceAction) -> Unit,
) {
    var manualInput by remember { mutableStateOf(false) }
    val useSelect = state.addressList.isNotEmpty() && !manualInput

    OrganicField(label = stringResource(Res.string.client_address)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f)) {
                if (useSelect) {
                    OrganicSelect(
                        value = state.address.takeIf { it.isNotBlank() },
                        items = state.addressList,
                        onSelect = { onAction(AddEditServiceAction.OnAddressChanged(it)) },
                        itemLabel = { it },
                    )
                } else {
                    OrganicTextField(
                        value = state.address,
                        onValueChange = { onAction(AddEditServiceAction.OnAddressChanged(it)) },
                    )
                }
            }
            if (state.addressList.isNotEmpty()) {
                OrganicIconButton(
                    icon = if (useSelect) OrganicIcons.Plus else OrganicIcons.X,
                    onClick = { manualInput = !manualInput },
                    contentDescription = stringResource(Res.string.service_form_new_address),
                )
            }
        }
    }
}

/** Блок «Поля категории»: у репетитора — домашнее задание и формат, у тренера — формат. */
@Composable
private fun ColumnScope.CategoryFields(
    state: AddEditServiceScreenState,
    onAction: (AddEditServiceAction) -> Unit,
    stacked: Boolean,
) {
    val education = state.serviceSpecificFields as? ServiceSpecificFields.EducationServiceSpecificFields
    val sport = state.serviceSpecificFields as? ServiceSpecificFields.SportServiceSpecificFields
    if (education == null && sport == null) return

    FormDivider()
    OrganicText(
        text = stringResource(Res.string.client_form_category, state.serviceType.toUIName()),
        style = OrganicTheme.typography.kicker,
        color = OrganicTheme.colors.accent,
    )

    OrganicField(
        label = stringResource(
            if (education != null) {
                Res.string.client_choose_format_education
            } else {
                Res.string.client_choose_format_sport
            },
        ),
    ) {
        SegmentedControl(
            options = listOf(false, true),
            selected = education?.isOnline ?: (sport?.isOnline == true),
            onSelect = { online ->
                onAction(
                    if (education != null) {
                        AddEditServiceAction.EducationServiceAction.OnFormatChanged(online)
                    } else {
                        AddEditServiceAction.SportServiceAction.OnFormatChanged(online)
                    },
                )
            },
            optionLabel = {
                stringResource(if (it) Res.string.client_online else Res.string.client_offline)
            },
        )
    }

    if (education != null) {
        OrganicField(label = stringResource(Res.string.service_homework)) {
            OrganicTextArea(
                value = education.homework.orEmpty(),
                onValueChange = {
                    onAction(AddEditServiceAction.EducationServiceAction.OnHomeworkChanged(it))
                },
                minHeight = 70.dp,
                placeholder = stringResource(Res.string.service_homework_placeholder),
            )
        }
    }
}

/** М6, М8, М9: панель формы подменяется коротким окном подтверждения. */
@Composable
private fun ServiceFormDialog(
    dialog: AddEditServiceScreenState.ServiceScreenDialog,
    onAction: (AddEditServiceAction) -> Unit,
    fullScreen: Boolean,
) {
    when (dialog) {
        AddEditServiceScreenState.ServiceScreenDialog.ConfirmServiceDeleting -> ConfirmModal(
            title = stringResource(Res.string.service_details_delete_title),
            text = stringResource(Res.string.service_confirm_deleting),
            confirmText = stringResource(Res.string.service_details_delete_confirm),
            onConfirm = { onAction(AddEditServiceAction.OnDeleteServiceConfirmed) },
            onDismiss = { onAction(AddEditServiceAction.OnDialogDismissed) },
            destructive = true,
            fullScreen = fullScreen,
        )

        AddEditServiceScreenState.ServiceScreenDialog.ConfirmDiscard -> ConfirmModal(
            title = stringResource(Res.string.service_form_discard_title),
            text = stringResource(Res.string.service_form_discard_text),
            confirmText = stringResource(Res.string.client_form_discard_confirm),
            dismissText = stringResource(Res.string.client_form_discard_dismiss),
            onConfirm = { onAction(AddEditServiceAction.OnCloseScreenClicked) },
            onDismiss = { onAction(AddEditServiceAction.OnDialogDismissed) },
            fullScreen = fullScreen,
        )

        is AddEditServiceScreenState.ServiceScreenDialog.ServicesCrossing -> ConfirmModal(
            title = stringResource(Res.string.service_form_crossing_title),
            text = stringResource(Res.string.service_form_crossing_text),
            confirmText = stringResource(Res.string.service_form_crossing_confirm),
            dismissText = stringResource(Res.string.service_form_crossing_dismiss),
            onConfirm = { onAction(AddEditServiceAction.OnSaveServiceConfirmed) },
            onDismiss = { onAction(AddEditServiceAction.OnDialogDismissed) },
            fullScreen = fullScreen,
            extraContent = {
                OrganicModalPanel {
                    dialog.services.take(CrossingServicesLimit).forEach { service ->
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            OrganicText(service.title, style = OrganicTheme.typography.bodySm)
                            OrganicText(
                                text = service.getServiceTime(),
                                style = OrganicTheme.typography.bodySm,
                                color = OrganicTheme.colors.muted,
                            )
                        }
                    }
                }
            },
        )
    }
}

/**
 * Сетка формы из макета: 1fr / 1fr на широком окне и одна колонка на compact. Ячейки
 * размечают себя через [FormRowScope.cell] — в колонке `weight` делил бы высоту.
 */
@Composable
private fun FormRow(
    stacked: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable FormRowScope.() -> Unit,
) {
    if (stacked) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(FormGap)) {
            FormRowScope(null).content()
        }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(FormGap),
            verticalAlignment = Alignment.Bottom,
        ) {
            FormRowScope(this).content()
        }
    }
}

private class FormRowScope(private val rowScope: RowScope?) {
    fun Modifier.cell(weight: Float = 1f): Modifier =
        if (rowScope == null) fillMaxWidth() else with(rowScope) { weight(weight) }
}

@Composable
private fun FormDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .height(1.dp)
            .background(OrganicTheme.colors.divider),
    )
}

private val FormGap = 14.dp
private val FormMinHeight = 160.dp
private const val CrossingServicesLimit = 6

@Preview
@Composable
private fun ServiceFormModalPreview() {
    OrganicTheme {
        OrganicModalHost(onDismissRequest = {}) {
            val client = BaseClient(id = 1, name = "Анна", surname = "Ковалёва")
            ServiceFormModalContent(
                state = AddEditServiceScreenState(
                    isLoading = false,
                    isEdit = true,
                    title = "Английский язык",
                    client = client,
                    clientsList = listOf(client),
                    durationText = "60",
                    address = "Онлайн, Zoom",
                    addressList = listOf("Онлайн, Zoom"),
                    price = "40",
                    serviceType = ServiceType.EDUCATION,
                    serviceSpecificFields = ServiceSpecificFields.EducationServiceSpecificFields(),
                ),
                onAction = {},
            )
        }
    }
}
