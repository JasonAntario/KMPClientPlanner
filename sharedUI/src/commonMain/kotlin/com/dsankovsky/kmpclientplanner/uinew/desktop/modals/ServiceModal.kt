package com.dsankovsky.kmpclientplanner.uinew.desktop.modals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceAction
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceEvent
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.DateSource
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.TimeSource
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.InitialsAvatar
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.LessonsTextField
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.ModalScaffold
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.SegmentedControl
import com.dsankovsky.kmpclientplanner.uinew.desktop.screens.initialsOf
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import org.koin.compose.viewmodel.koinViewModel

/**
 * Desktop "create / edit lesson" dialog, redrawn in the Lessons design system and
 * driven by the existing [AddEditServiceViewModel]. Only the fields the view-model
 * already supports are wired (title, client, schedule, price, statuses, format, comment).
 */
@Composable
fun AddEditServiceModal(
    serviceId: Long?,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
) {
    val viewModel: AddEditServiceViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(serviceId) {
        viewModel.handleActions(AddEditServiceAction.LoadServiceData(serviceId))
    }
    viewModel.event.collectWithLifecycle { event ->
        when (event) {
            AddEditServiceEvent.OnDismissClicked -> onDismiss()
            AddEditServiceEvent.OnServiceSaved -> onSaved()
            AddEditServiceEvent.OnServiceDeleted -> onSaved()
        }
    }

    ServiceDialogs(state, viewModel::handleActions)

    ModalScaffold(onDismiss = onDismiss, cardWidth = 448.dp) {
        if (state.isLoading) {
            ModalLoading()
        } else {
            ServiceModalContent(
                state = state,
                onAction = viewModel::handleActions,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun ServiceModalContent(
    state: AddEditServiceScreenState,
    onAction: (AddEditServiceAction) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf(state.title) }
    var price by remember { mutableStateOf(state.price) }
    var comment by remember { mutableStateOf(state.comment) }
    LaunchedEffect(state.title) { title = state.title }
    LaunchedEffect(state.price) { price = state.price }
    LaunchedEffect(state.comment) { comment = state.comment }

    val badge = serviceBadge(state.serviceType)

    Column(Modifier.fillMaxWidth()) {
        ModalHeader(
            title = serviceTitle(state.isEdit, state.serviceType),
            badge = badge,
            onClose = onDismiss,
        )

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ModalBodyHorizontalPadding, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(ModalBodyVerticalGap),
        ) {
            ModalField(serviceTitleFieldLabel(state.serviceType)) {
                LessonsTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ModalField("Клиент") {
                DropdownField(
                    current = state.client,
                    items = state.clientsList,
                    itemText = { it.getFullName() },
                    onSelect = { onAction(AddEditServiceAction.OnClientChanged(it)) },
                    placeholder = "Выберите клиента",
                    modifier = Modifier.fillMaxWidth(),
                    leading = state.client?.let { cl ->
                        {
                            InitialsAvatar(
                                initialsOf(cl.getFullName()),
                                size = 30.dp,
                                fontSize = 10
                            )
                        }
                    },
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                ModalField("Дата", modifier = Modifier.weight(1f)) {
                    DateField(
                        date = state.startDateTime.date,
                        onDateChanged = { date ->
                            onAction(
                                AddEditServiceAction.OnDateChanged(
                                    date,
                                    DateSource.BASE_START_DATE
                                )
                            )
                            onAction(
                                AddEditServiceAction.OnDateChanged(
                                    date,
                                    DateSource.BASE_END_DATE
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ModalField("Начало", modifier = Modifier.weight(1f)) {
                    TimeField(
                        time = state.startDateTime.time,
                        onTimeChanged = {
                            onAction(
                                AddEditServiceAction.OnTimeChanged(
                                    it,
                                    TimeSource.BASE_START_TIME
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ModalField("Конец", modifier = Modifier.weight(1f)) {
                    TimeField(
                        time = state.endDateTime.time,
                        onTimeChanged = {
                            onAction(
                                AddEditServiceAction.OnTimeChanged(
                                    it,
                                    TimeSource.BASE_END_TIME
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                ModalField("Цена", modifier = Modifier.weight(1.6f)) {
                    LessonsTextField(
                        value = price,
                        onValueChange = {
                            price = it
                            onAction(AddEditServiceAction.OnPriceChanged(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ModalField("Валюта", modifier = Modifier.weight(1f)) {
                    DropdownField(
                        current = state.currency,
                        items = state.currenciesList,
                        itemText = { it.code },
                        onSelect = { onAction(AddEditServiceAction.OnCurrencyChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                ModalField("Статус", modifier = Modifier.weight(1f)) {
                    SegmentedControl(
                        options = listOf("Заплан.", "Проведено"),
                        selectedIndex = if (state.isFinished) 1 else 0,
                        onSelect = { onAction(AddEditServiceAction.OnFinishedStatusChanged(it == 1)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ModalField("Оплата", modifier = Modifier.weight(1f)) {
                    SegmentedControl(
                        options = listOf("Оплачено", "Не опл."),
                        selectedIndex = if (state.isPaid) 0 else 1,
                        onSelect = { onAction(AddEditServiceAction.OnPaidStatusChanged(it == 0)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            ServiceTypeFields(state, onAction)

            ModalField("Комментарий") {
                LessonsTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = "Заметка к занятию…",
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        ModalDivider()
        ModalFooter(
            saveLabel = "Сохранить",
            onCancel = onDismiss,
            onSave = {
                onAction(
                    AddEditServiceAction.OnSaveServiceClicked(
                        title = title,
                        address = state.address,
                        price = price,
                        comment = comment,
                    )
                )
            },
            saveEnabled = title.isNotBlank() && state.client != null,
            onDelete = if (state.isEdit) {
                { onAction(AddEditServiceAction.OnDeleteService) }
            } else null,
        )
    }
}

@Composable
private fun ServiceTypeFields(
    state: AddEditServiceScreenState,
    onAction: (AddEditServiceAction) -> Unit,
) {
    when (val fields = state.serviceSpecificFields) {
        is ServiceSpecificFields.EducationServiceSpecificFields -> {
            ModalDivider()
            TypeSectionHeader("Поля репетитора", Icons.Filled.School)
            ModalField("Формат") {
                SegmentedControl(
                    options = listOf("Онлайн", "Офлайн"),
                    selectedIndex = if (fields.isOnline) 0 else 1,
                    onSelect = {
                        onAction(
                            AddEditServiceAction.EducationServiceAction.OnFormatChanged(
                                it == 0
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        is ServiceSpecificFields.SportServiceSpecificFields -> {
            ModalDivider()
            TypeSectionHeader("Поля тренера", Icons.Filled.FitnessCenter)
            ModalField("Формат") {
                SegmentedControl(
                    options = listOf("Онлайн", "Офлайн"),
                    selectedIndex = if (fields.isOnline) 0 else 1,
                    onSelect = { onAction(AddEditServiceAction.SportServiceAction.OnFormatChanged(it == 0)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        else -> Unit
    }
}

@Composable
internal fun ModalLoading() {
    Box(Modifier.fillMaxWidth().heightIn(min = 240.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = LessonsColors.Primary)
    }
}

internal fun serviceBadge(type: ServiceType): ServiceTypeBadge = when (type) {
    ServiceType.EDUCATION -> ServiceTypeBadge("Репетитор", Icons.Filled.School)
    ServiceType.SPORT -> ServiceTypeBadge("Тренер", Icons.Filled.FitnessCenter)
    ServiceType.TATTOO -> ServiceTypeBadge("Тату", Icons.Filled.Brush)
    ServiceType.BEAUTY -> ServiceTypeBadge("Бьюти", Icons.Filled.Spa)
    ServiceType.BASE -> ServiceTypeBadge("Услуга", Icons.Filled.MoreHoriz)
}

private fun serviceTitle(isEdit: Boolean, type: ServiceType): String = when {
    isEdit -> "Редактирование"
    type == ServiceType.SPORT -> "Новая тренировка"
    type == ServiceType.TATTOO -> "Новый сеанс"
    type == ServiceType.BEAUTY -> "Новая процедура"
    else -> "Новое занятие"
}

private fun serviceTitleFieldLabel(type: ServiceType): String = when (type) {
    ServiceType.SPORT -> "Название"
    ServiceType.TATTOO -> "Проект"
    ServiceType.BEAUTY -> "Процедура"
    else -> "Тема занятия"
}
