package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.ConfirmModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonDefaults
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
import com.dsankovsky.kmpclientplanner.ui.design.components.SegmentedControl
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.getDaysOfWeekList
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.client_add_lesson
import kmpclientplanner.sharedui.generated.resources.client_address
import kmpclientplanner.sharedui.generated.resources.client_choose_format_education
import kmpclientplanner.sharedui.generated.resources.client_choose_format_sport
import kmpclientplanner.sharedui.generated.resources.client_comment
import kmpclientplanner.sharedui.generated.resources.client_confirm_deleting
import kmpclientplanner.sharedui.generated.resources.client_currency
import kmpclientplanner.sharedui.generated.resources.client_details_delete_confirm
import kmpclientplanner.sharedui.generated.resources.client_details_delete_title
import kmpclientplanner.sharedui.generated.resources.client_form_autofill_confirm
import kmpclientplanner.sharedui.generated.resources.client_form_autofill_dismiss
import kmpclientplanner.sharedui.generated.resources.client_form_autofill_text
import kmpclientplanner.sharedui.generated.resources.client_form_autofill_title
import kmpclientplanner.sharedui.generated.resources.client_form_category
import kmpclientplanner.sharedui.generated.resources.client_form_category_hint
import kmpclientplanner.sharedui.generated.resources.client_form_crossing_confirm
import kmpclientplanner.sharedui.generated.resources.client_form_crossing_dismiss
import kmpclientplanner.sharedui.generated.resources.client_form_crossing_text
import kmpclientplanner.sharedui.generated.resources.client_form_crossing_title
import kmpclientplanner.sharedui.generated.resources.client_form_delete
import kmpclientplanner.sharedui.generated.resources.client_form_discard_confirm
import kmpclientplanner.sharedui.generated.resources.client_form_discard_dismiss
import kmpclientplanner.sharedui.generated.resources.client_form_discard_text
import kmpclientplanner.sharedui.generated.resources.client_form_discard_title
import kmpclientplanner.sharedui.generated.resources.client_form_duration
import kmpclientplanner.sharedui.generated.resources.client_form_save
import kmpclientplanner.sharedui.generated.resources.client_form_schedule
import kmpclientplanner.sharedui.generated.resources.client_form_schedule_hint
import kmpclientplanner.sharedui.generated.resources.client_form_subtitle
import kmpclientplanner.sharedui.generated.resources.client_form_time
import kmpclientplanner.sharedui.generated.resources.client_form_title
import kmpclientplanner.sharedui.generated.resources.client_level
import kmpclientplanner.sharedui.generated.resources.client_name
import kmpclientplanner.sharedui.generated.resources.client_offline
import kmpclientplanner.sharedui.generated.resources.client_online
import kmpclientplanner.sharedui.generated.resources.client_phone
import kmpclientplanner.sharedui.generated.resources.client_price
import kmpclientplanner.sharedui.generated.resources.client_surname
import kmpclientplanner.sharedui.generated.resources.client_weight
import kmpclientplanner.sharedui.generated.resources.day_of_week
import kmpclientplanner.sharedui.generated.resources.service_training_delete_lesson
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * М3 — форма клиента: создание и редактирование одним окном.
 *
 * Окно само владеет своим [OrganicModalHost]: закрытие по Esc и клику мимо должно проходить
 * через форму, а та с несохранёнными правками сначала спросит М9. Подтверждения (М6–М9)
 * подменяют панель формы, а не открывают второй скрим поверх первого.
 */
@Composable
fun ClientFormModal(
    clientId: Long?,
    onEvent: (AddEditClientEvent) -> Unit,
    fullScreen: Boolean = false,
) {
    val viewModel: AddEditClientViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.collectWithLifecycle { onEvent(it) }

    LaunchedEffect(clientId) {
        viewModel.handleActions(AddEditClientAction.LoadClientData(clientId))
    }

    OrganicModalHost(
        onDismissRequest = { viewModel.handleActions(AddEditClientAction.OnCloseRequested) },
    ) {
        ClientFormModalContent(
            state = state,
            onAction = viewModel::handleActions,
            fullScreen = fullScreen,
        )
    }
}

@Composable
fun ClientFormModalContent(
    state: AddEditClientScreenState,
    onAction: (AddEditClientAction) -> Unit,
    modifier: Modifier = Modifier,
    fullScreen: Boolean = false,
) {
    state.showDialog?.let { dialog ->
        ClientFormDialog(dialog = dialog, state = state, onAction = onAction, fullScreen = fullScreen)
        return
    }

    OrganicModal(modifier = modifier, width = OrganicModalWidth.Form, fullScreen = fullScreen) {
        OrganicModalHeader(
            title = stringResource(Res.string.client_form_title),
            subtitle = stringResource(Res.string.client_form_subtitle),
            onClose = { onAction(AddEditClientAction.OnCloseRequested) },
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().height(FormMinHeight)) { LoadingScreen() }
        } else {
            // Форма длиннее окна на compact и у категорий с расписанием — крутим содержимое,
            // а шапка и кнопки остаются на месте.
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(FormGap),
            ) {
                ClientFields(state = state, onAction = onAction, stacked = fullScreen)
            }
        }

        OrganicModalActions(
            destructive = if (state.isEdit) {
                {
                    OrganicButton(
                        text = stringResource(Res.string.client_form_delete),
                        onClick = { onAction(AddEditClientAction.OnDeleteClient) },
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
                onClick = { onAction(AddEditClientAction.OnCloseRequested) },
                colors = OrganicButtonDefaults.secondary(),
            )
            OrganicButton(
                text = stringResource(Res.string.client_form_save),
                onClick = { onAction(AddEditClientAction.OnClientSaveClicked) },
                enabled = state.canSave,
            )
        }
    }
}

@Composable
private fun ColumnScope.ClientFields(
    state: AddEditClientScreenState,
    onAction: (AddEditClientAction) -> Unit,
    stacked: Boolean,
) {
    FormRow(stacked) {
        OrganicField(
            label = stringResource(Res.string.client_name),
            modifier = Modifier.cell(),
            required = true,
        ) {
            OrganicTextField(
                value = state.name,
                onValueChange = { onAction(AddEditClientAction.OnNameChanged(it)) },
            )
        }
        OrganicField(label = stringResource(Res.string.client_surname), modifier = Modifier.cell()) {
            OrganicTextField(
                value = state.surname,
                onValueChange = { onAction(AddEditClientAction.OnSurnameChanged(it)) },
            )
        }
    }

    FormRow(stacked) {
        OrganicField(label = stringResource(Res.string.client_phone), modifier = Modifier.cell()) {
            OrganicTextField(
                value = state.phone,
                onValueChange = { onAction(AddEditClientAction.OnPhoneChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )
        }
        OrganicField(label = stringResource(Res.string.client_address), modifier = Modifier.cell()) {
            OrganicTextField(
                value = state.address,
                onValueChange = { onAction(AddEditClientAction.OnAddressChanged(it)) },
            )
        }
    }

    FormRow(stacked) {
        OrganicField(label = stringResource(Res.string.client_price), modifier = Modifier.cell()) {
            OrganicTextField(
                value = state.price,
                onValueChange = { onAction(AddEditClientAction.OnPriceChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }
        OrganicField(label = stringResource(Res.string.client_currency), modifier = Modifier.cell()) {
            OrganicSelect(
                value = state.currency,
                items = state.currenciesList,
                onSelect = { onAction(AddEditClientAction.OnCurrencyChanged(it)) },
                itemLabel = { it.code },
            )
        }
    }

    OrganicField(label = stringResource(Res.string.client_comment)) {
        OrganicTextArea(
            value = state.comment,
            onValueChange = { onAction(AddEditClientAction.OnCommentChanged(it)) },
            minHeight = 64.dp,
        )
    }

    CategoryFields(state = state, onAction = onAction, stacked = stacked)
}

/** Блок «Поля категории» — свой у репетитора и тренера, у тату и бьюти его нет. */
@Composable
private fun ColumnScope.CategoryFields(
    state: AddEditClientScreenState,
    onAction: (AddEditClientAction) -> Unit,
    stacked: Boolean,
) {
    val fields = state.clientSpecificFields
    val education = fields as? ClientSpecificFields.EducationClientSpecificFields
    val sport = fields as? ClientSpecificFields.SportClientSpecificFields
    if (education == null && sport == null) return

    FormDivider()
    OrganicText(
        text = stringResource(Res.string.client_form_category, state.serviceType.toUIName()),
        style = OrganicTheme.typography.kicker,
        color = OrganicTheme.colors.accent,
    )

    FormRow(stacked) {
        if (education != null) {
            OrganicField(label = stringResource(Res.string.client_level), modifier = Modifier.cell()) {
                OrganicTextField(
                    value = education.level.orEmpty(),
                    onValueChange = {
                        onAction(AddEditClientAction.EducationClientAction.OnLevelChanged(it))
                    },
                )
            }
        } else if (sport != null) {
            OrganicField(label = stringResource(Res.string.client_weight), modifier = Modifier.cell()) {
                OrganicTextField(
                    value = sport.weight.orEmpty(),
                    onValueChange = {
                        onAction(AddEditClientAction.SportClientAction.OnWeightChanged(it))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            }
        }
        val isOnline = education?.isOnline ?: sport?.isOnline == true
        OrganicField(
            label = stringResource(
                if (education != null) {
                    Res.string.client_choose_format_education
                } else {
                    Res.string.client_choose_format_sport
                },
            ),
            modifier = Modifier.cell(),
        ) {
            SegmentedControl(
                options = listOf(false, true),
                selected = isOnline,
                onSelect = { online ->
                    onAction(
                        if (education != null) {
                            AddEditClientAction.EducationClientAction.OnFormatChanged(online)
                        } else {
                            AddEditClientAction.SportClientAction.OnFormatChanged(online)
                        },
                    )
                },
                optionLabel = {
                    stringResource(if (it) Res.string.client_online else Res.string.client_offline)
                },
            )
        }
    }

    OrganicText(
        text = stringResource(Res.string.client_form_category_hint),
        style = OrganicTheme.typography.label,
        color = OrganicTheme.colors.muted,
    )

    ScheduleBlock(
        slots = education?.lessonDateTimeList ?: sport?.lessonDateTimeList.orEmpty(),
        isEducation = education != null,
        onAction = onAction,
        stacked = stacked,
    )
}

/**
 * Регулярное расписание: день недели, время и длительность. В макете этого блока нет —
 * там у клиента только «первое занятие», — но именно на нём стоит автозаполнение (М7),
 * поэтому блок остаётся.
 */
@Composable
private fun ColumnScope.ScheduleBlock(
    slots: List<ServiceDateTime>,
    isEducation: Boolean,
    onAction: (AddEditClientAction) -> Unit,
    stacked: Boolean,
) {
    OrganicModalPanel(kicker = stringResource(Res.string.client_form_schedule)) {
        slots.forEachIndexed { index, slot ->
            ScheduleRow(
                slot = slot,
                stacked = stacked,
                onDayOfWeekChanged = {
                    onAction(
                        if (isEducation) {
                            AddEditClientAction.EducationClientAction.OnDayOfWeekChanged(index, it)
                        } else {
                            AddEditClientAction.SportClientAction.OnDayOfWeekChanged(index, it)
                        },
                    )
                },
                onTimeChanged = {
                    onAction(
                        if (isEducation) {
                            AddEditClientAction.EducationClientAction.OnTimeChanged(index, it)
                        } else {
                            AddEditClientAction.SportClientAction.OnTimeChanged(index, it)
                        },
                    )
                },
                onDurationChanged = {
                    onAction(
                        if (isEducation) {
                            AddEditClientAction.EducationClientAction.OnDurationChanged(index, it)
                        } else {
                            AddEditClientAction.SportClientAction.OnDurationChanged(index, it)
                        },
                    )
                },
                onDelete = {
                    onAction(
                        if (isEducation) {
                            AddEditClientAction.EducationClientAction.OnDeleteLessonClicked(index)
                        } else {
                            AddEditClientAction.SportClientAction.OnDeleteTrainingClicked(index)
                        },
                    )
                },
            )
        }

        if (slots.isEmpty()) {
            OrganicText(
                text = stringResource(Res.string.client_form_schedule_hint),
                style = OrganicTheme.typography.label,
                color = OrganicTheme.colors.muted,
            )
        }

        OrganicButton(
            text = stringResource(Res.string.client_add_lesson),
            onClick = {
                onAction(
                    if (isEducation) {
                        AddEditClientAction.EducationClientAction.OnAddNewServiceTime
                    } else {
                        AddEditClientAction.SportClientAction.OnAddNewServiceTime
                    },
                )
            },
            colors = OrganicButtonDefaults.secondary(),
            icon = OrganicIcons.Plus,
        )
    }
}

@Composable
private fun ScheduleRow(
    slot: ServiceDateTime,
    stacked: Boolean,
    onDayOfWeekChanged: (DayOfWeek) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    onDurationChanged: (String) -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2),
        verticalAlignment = Alignment.Bottom,
    ) {
        FormRow(stacked, modifier = Modifier.weight(1f)) {
            OrganicField(label = stringResource(Res.string.day_of_week), modifier = Modifier.cell(1.4f)) {
                // Названия дней — ресурсы, а `itemLabel` не композабл: считаем заранее.
                val days = getDaysOfWeekList().map { day -> day to day.toUIName() }
                OrganicSelect(
                    value = days.firstOrNull { it.first == slot.dayOfWeek },
                    items = days,
                    onSelect = { onDayOfWeekChanged(it.first) },
                    itemLabel = { it.second },
                )
            }
            OrganicField(label = stringResource(Res.string.client_form_time), modifier = Modifier.cell(1.2f)) {
                OrganicTimeField(time = slot.time, onTimeSelected = onTimeChanged)
            }
            OrganicField(label = stringResource(Res.string.client_form_duration), modifier = Modifier.cell()) {
                OrganicTextField(
                    value = slot.duration,
                    onValueChange = onDurationChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            }
        }
        OrganicIconButton(
            icon = OrganicIcons.Trash,
            onClick = onDelete,
            contentDescription = stringResource(Res.string.service_training_delete_lesson),
            colors = OrganicButtonDefaults.ghost(),
        )
    }
}

/** М6–М9: панель формы подменяется коротким окном подтверждения. */
@Composable
private fun ClientFormDialog(
    dialog: ClientScreenDialog,
    state: AddEditClientScreenState,
    onAction: (AddEditClientAction) -> Unit,
    fullScreen: Boolean,
) {
    when (dialog) {
        ClientScreenDialog.ConfirmClientDeleting -> ConfirmModal(
            title = stringResource(Res.string.client_details_delete_title),
            text = stringResource(Res.string.client_confirm_deleting),
            confirmText = stringResource(Res.string.client_details_delete_confirm),
            onConfirm = { onAction(AddEditClientAction.OnDeleteClientConfirmed) },
            onDismiss = { onAction(AddEditClientAction.CloseClientDialog) },
            destructive = true,
            fullScreen = fullScreen,
        )

        ClientScreenDialog.ConfirmDiscard -> ConfirmModal(
            title = stringResource(Res.string.client_form_discard_title),
            text = stringResource(Res.string.client_form_discard_text),
            confirmText = stringResource(Res.string.client_form_discard_confirm),
            dismissText = stringResource(Res.string.client_form_discard_dismiss),
            onConfirm = { onAction(AddEditClientAction.OnCloseScreenClicked) },
            onDismiss = { onAction(AddEditClientAction.CloseClientDialog) },
            fullScreen = fullScreen,
        )

        ClientScreenDialog.ConfirmAutofillServices -> ConfirmModal(
            title = stringResource(Res.string.client_form_autofill_title),
            text = stringResource(Res.string.client_form_autofill_text),
            confirmText = stringResource(Res.string.client_form_autofill_confirm),
            dismissText = stringResource(Res.string.client_form_autofill_dismiss),
            onConfirm = { onAction(AddEditClientAction.OnAutofillConfirmClicked) },
            // «Не сейчас» — клиент уже сохранён, форму закрываем.
            onDismiss = { onAction(AddEditClientAction.OnAutofillDismissClicked) },
            fullScreen = fullScreen,
        )

        is ClientScreenDialog.ServicesCrossing -> ConfirmModal(
            title = stringResource(Res.string.client_form_crossing_title),
            text = stringResource(Res.string.client_form_crossing_text),
            confirmText = stringResource(Res.string.client_form_crossing_confirm),
            dismissText = stringResource(Res.string.client_form_crossing_dismiss),
            onConfirm = { onAction(AddEditClientAction.OnAutofillWithCrossingConfirmClicked) },
            onDismiss = { onAction(AddEditClientAction.CloseClientDialog) },
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
 * Сетка формы из макета: 1fr / 1fr на широком окне и одна колонка на compact.
 *
 * Ячейки размечают себя сами через [FormRowScope.cell] — в колонке `weight` значил бы
 * распределение высоты, поэтому там он подменяется на `fillMaxWidth`.
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

private val FormGap = 14.dp
private val FormMinHeight = 160.dp
private const val CrossingServicesLimit = 6

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

@Preview
@Composable
private fun ClientFormModalPreview() {
    OrganicTheme {
        OrganicModalHost(onDismissRequest = {}) {
            ClientFormModalContent(
                state = AddEditClientScreenState(
                    isLoading = false,
                    isEdit = true,
                    name = "Анна",
                    surname = "Ковалёва",
                    phone = "+375 29 123-45-67",
                    price = "40",
                    comment = "Готовится к экзамену в декабре.",
                    serviceType = ServiceType.EDUCATION,
                    clientSpecificFields = ClientSpecificFields.EducationClientSpecificFields(
                        level = "B1",
                        lessonDateTimeList = listOf(ServiceDateTime()),
                    ),
                ),
                onAction = {},
            )
        }
    }
}
