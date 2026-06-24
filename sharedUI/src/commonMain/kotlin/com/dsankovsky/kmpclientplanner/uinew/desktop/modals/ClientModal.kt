package com.dsankovsky.kmpclientplanner.uinew.desktop.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientAction
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientEvent
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientViewModel
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.LessonsTextField
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.ModalScaffold
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.SegmentedControl
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily
import org.koin.compose.viewmodel.koinViewModel

/**
 * Desktop "create / edit client" dialog, redrawn in the Lessons design system and
 * driven by the existing [AddEditClientViewModel].
 */
@Composable
fun AddEditClientModal(
    clientId: Long?,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
) {
    val viewModel: AddEditClientViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(clientId) { viewModel.handleActions(AddEditClientAction.LoadClientData(clientId)) }
    viewModel.event.collectWithLifecycle { event ->
        when (event) {
            AddEditClientEvent.OnDismissClicked -> onDismiss()
            AddEditClientEvent.OnClientSaved -> onSaved()
            is AddEditClientEvent.OnClientDeleted -> onSaved()
            AddEditClientEvent.AutofillCompleted -> Unit
        }
    }

    ClientDialogs(state, viewModel::handleActions)

    ModalScaffold(onDismiss = onDismiss, cardWidth = 448.dp) {
        if (state.isLoading) {
            ModalLoading()
        } else {
            ClientModalContent(state = state, onAction = viewModel::handleActions, onDismiss = onDismiss)
        }
    }
}

@Composable
private fun ClientModalContent(
    state: AddEditClientScreenState,
    onAction: (AddEditClientAction) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(state.name) }
    var surname by remember { mutableStateOf(state.surname) }
    var phone by remember { mutableStateOf(state.phone) }
    var price by remember { mutableStateOf(state.price) }
    var address by remember { mutableStateOf(state.address) }
    var comment by remember { mutableStateOf(state.comment) }

    val educationFields = state.clientSpecificFields as? ClientSpecificFields.EducationClientSpecificFields
    val sportFields = state.clientSpecificFields as? ClientSpecificFields.SportClientSpecificFields
    var level by remember { mutableStateOf(educationFields?.level.orEmpty()) }
    var weight by remember { mutableStateOf(sportFields?.weight.orEmpty()) }

    LaunchedEffect(state.name) { name = state.name }
    LaunchedEffect(state.surname) { surname = state.surname }
    LaunchedEffect(state.phone) { phone = state.phone }
    LaunchedEffect(state.price) { price = state.price }
    LaunchedEffect(state.address) { address = state.address }
    LaunchedEffect(state.comment) { comment = state.comment }
    LaunchedEffect(educationFields?.level) { level = educationFields?.level.orEmpty() }
    LaunchedEffect(sportFields?.weight) { weight = sportFields?.weight.orEmpty() }

    Column(Modifier.fillMaxWidth()) {
        ModalHeader(
            title = if (state.isEdit) "Редактирование" else "Новый клиент",
            badge = serviceBadge(state.serviceType),
            onClose = onDismiss,
        )

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ModalBodyHorizontalPadding, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(ModalBodyVerticalGap),
        ) {
            PhotoPlaceholder()

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                ModalField("Имя", modifier = Modifier.weight(1f)) {
                    LessonsTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth())
                }
                ModalField("Фамилия", modifier = Modifier.weight(1f)) {
                    LessonsTextField(value = surname, onValueChange = { surname = it }, modifier = Modifier.fillMaxWidth())
                }
            }

            ModalField("Телефон") {
                LessonsTextField(value = phone, onValueChange = { phone = it }, modifier = Modifier.fillMaxWidth())
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                ModalField("Цена по умолч.", modifier = Modifier.weight(1.6f)) {
                    LessonsTextField(
                        value = price,
                        onValueChange = {
                            price = it
                            onAction(AddEditClientAction.OnPriceChanged(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ModalField("Валюта", modifier = Modifier.weight(1f)) {
                    DropdownField(
                        current = state.currency,
                        items = state.currenciesList,
                        itemText = { it.code },
                        onSelect = { onAction(AddEditClientAction.OnCurrencyChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            ModalField("Адрес") {
                LessonsTextField(value = address, onValueChange = { address = it }, modifier = Modifier.fillMaxWidth())
            }

            if (educationFields != null) {
                ModalDivider()
                TypeSectionHeader("Поля репетитора", Icons.Filled.School)
                ModalField("Уровень") {
                    LessonsTextField(value = level, onValueChange = { level = it }, modifier = Modifier.fillMaxWidth())
                }
                ModalField("Формат") {
                    SegmentedControl(
                        options = listOf("Онлайн", "Офлайн"),
                        selectedIndex = if (educationFields.isOnline) 0 else 1,
                        onSelect = { onAction(AddEditClientAction.EducationClientAction.OnFormatChanged(it == 0)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            if (sportFields != null) {
                ModalDivider()
                TypeSectionHeader("Поля тренера", Icons.Filled.FitnessCenter)
                ModalField("Вес") {
                    LessonsTextField(value = weight, onValueChange = { weight = it }, modifier = Modifier.fillMaxWidth())
                }
                ModalField("Формат") {
                    SegmentedControl(
                        options = listOf("Онлайн", "Офлайн"),
                        selectedIndex = if (sportFields.isOnline) 0 else 1,
                        onSelect = { onAction(AddEditClientAction.SportClientAction.OnFormatChanged(it == 0)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            ModalField("Комментарий") {
                LessonsTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = "Заметка о клиенте…",
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
                    AddEditClientAction.OnClientSaveClicked(
                        name = name,
                        surname = surname,
                        comment = comment,
                        address = address,
                        phone = phone,
                        price = price,
                        level = level,
                        weight = weight,
                    )
                )
            },
            saveEnabled = name.isNotBlank(),
            onDelete = if (state.isEdit) {
                { onAction(AddEditClientAction.OnDeleteClient) }
            } else null,
        )
    }
}

@Composable
private fun PhotoPlaceholder() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(LessonsColors.PrimaryTint)
                .border(1.5.dp, LessonsColors.PrimaryTintBorder, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.PhotoCamera, null, tint = LessonsColors.Primary, modifier = Modifier.size(20.dp))
        }
        Text(
            "Добавьте фото клиента —\nнеобязательно",
            color = LessonsColors.TextMuted,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
        )
    }
}
