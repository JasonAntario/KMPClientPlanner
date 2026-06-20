package com.dsankovsky.kmpclientplanner.uinew.desktop.screens

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.outlined.Circle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.InitialsAvatar
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.IconActionButton
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.LessonsCard
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.MasterDetailScaffold
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.SectionCaption
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.StatusDot
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.StatusPill
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.FilterChip
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenAction
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenAction
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenItem
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DesktopHomeScreen(
    rail: @Composable () -> Unit,
    onAddService: () -> Unit,
    onEditService: (Long) -> Unit,
) {
    val servicesViewModel: ServicesScreenViewModel = koinViewModel()
    val state by servicesViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { servicesViewModel.handleAction(ServicesListScreenAction.LoadData) }

    val lessons = remember(state.items) {
        state.items.filterIsInstance<ServicesListScreenItem.ServiceItem>()
    }
    var selectedId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(lessons) {
        if (lessons.none { it.id == selectedId }) selectedId = lessons.firstOrNull()?.id
    }

    MasterDetailScaffold(
        rail = rail,
        master = {
            LessonsMaster(
                state = state,
                lessons = lessons,
                selectedId = selectedId,
                onSelect = { selectedId = it },
                onFilter = { servicesViewModel.handleAction(ServicesListScreenAction.OnFilterClicked(it)) },
            )
        },
        detail = {
            val selected = lessons.firstOrNull { it.id == selectedId }
            if (selected == null) {
                EmptyDetail("Выберите занятие")
            } else {
                LessonDetail(
                    serviceId = selected.id,
                    onEdit = { onEditService(selected.id) },
                    onDelete = { servicesViewModel.handleAction(ServicesListScreenAction.OnDeleteService(selected)) },
                )
            }
        },
    )
}

@Composable
private fun LessonsMaster(
    state: com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenState,
    lessons: List<ServicesListScreenItem.ServiceItem>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onFilter: (com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 12.dp)) {
            Text("Занятия", color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Text(
                "${ruDateLabel(getCurrentDateTime().date)} · ${lessonsPlural(lessons.size)}",
                color = LessonsColors.TextMuted,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            )
        }
        Row(
            modifier = Modifier.padding(start = 22.dp, end = 22.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.filtersList.forEach { filter ->
                FilterChip(
                    label = filter.toLabel(),
                    selected = filter == state.currentFilter,
                    onClick = { onFilter(filter) },
                )
            }
        }
        if (lessons.isEmpty()) {
            EmptyDetail("Нет занятий")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                items(lessons, key = { it.id }) { item ->
                    LessonRow(item = item, selected = item.id == selectedId, onClick = { onSelect(item.id) })
                }
            }
        }
    }
}

@Composable
private fun LessonRow(item: ServicesListScreenItem.ServiceItem, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(LessonsColors.CardBackground)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) LessonsColors.Primary else LessonsColors.BorderCard,
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(44.dp)) {
            Text(item.startDate.startLabel(), color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Text(durationLabel(item.startDate, item.endDate), color = LessonsColors.TextMuted, fontFamily = nunitoFamily(), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
        }
        Box(Modifier.width(1.dp).height(36.dp).background(LessonsColors.Divider))
        Column(Modifier.weight(1f)) {
            Text(item.title, color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
            Text(item.client.getFullName(), color = LessonsColors.TextSecondary, fontFamily = nunitoFamily(), fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(5.dp)) {
            item.service.priceLabel()?.let {
                Text(it, color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
            StatusDot(statusColor(item.isFinished, item.isPaid))
        }
    }
}

@Composable
private fun LessonDetail(serviceId: Long, onEdit: () -> Unit, onDelete: () -> Unit) {
    val viewModel: ServiceDetailsScreenViewModel = koinViewModel(key = "service_$serviceId")
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(serviceId) { viewModel.handleActions(ServiceDetailsScreenAction.LoadData(serviceId)) }

    if (state.isLoading) {
        EmptyDetail("")
        return
    }

    Column(
        Modifier.fillMaxSize().background(LessonsColors.CardBackground).padding(horizontal = 26.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(state.title, color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Text(state.time, color = LessonsColors.TextMuted, fontFamily = nunitoFamily(), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                IconActionButton(Icons.Filled.Edit, onEdit)
                IconActionButton(
                    Icons.Filled.Delete,
                    onDelete,
                    tint = LessonsColors.Danger,
                    background = LessonsColors.DangerTint,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LessonsColors.DangerTintBorder),
                )
            }
        }

        // Client row
        LessonsCard {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp),
            ) {
                InitialsAvatar(initialsOf(state.clientName), size = 40.dp)
                Column(Modifier.weight(1f)) {
                    Text(state.clientName, color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(state.service.serviceSubtype ?: "", color = LessonsColors.TextMuted, fontFamily = nunitoFamily(), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
                state.price?.let {
                    Text(it, color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }
        }

        // Status pills
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            val finished = state.isFinished
            StatusPill(
                label = if (finished) "Проведено" else "Не проведено",
                icon = if (finished) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                contentColor = if (finished) LessonsColors.Success else LessonsColors.TextSecondary,
                background = if (finished) LessonsColors.SuccessTint else LessonsColors.PanelBackground,
                border = if (finished) LessonsColors.SuccessTintBorder else LessonsColors.BorderCard,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.handleActions(ServiceDetailsScreenAction.OnFinishStatusChanged) },
            )
            val paid = state.isPaid
            StatusPill(
                label = if (paid) "Оплачено" else "Не оплачено",
                icon = if (paid) Icons.Filled.CheckCircle else Icons.Filled.Circle,
                contentColor = if (paid) LessonsColors.Success else LessonsColors.Primary,
                background = if (paid) LessonsColors.SuccessTint else LessonsColors.PrimaryTint,
                border = if (paid) LessonsColors.SuccessTintBorder else LessonsColors.PrimaryTintBorder,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.handleActions(ServiceDetailsScreenAction.OnPaidStatusChanged) },
            )
        }

        // Homework (education)
        val homework = (state.serviceSpecificFields as? ServiceSpecificFields.EducationServiceSpecificFields)?.homework
        if (!homework.isNullOrBlank()) {
            LessonsCard {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.Notes, null, tint = LessonsColors.Primary, modifier = Modifier.padding(end = 0.dp))
                        Text("Домашнее задание", color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(homework, color = LessonsColors.TextBody, fontFamily = nunitoFamily(), fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
            }
        }

        // Comment
        if (!state.comment.isNullOrBlank()) {
            LessonsCard {
                Column(Modifier.padding(16.dp)) {
                    SectionCaption("Комментарий")
                    Spacer(Modifier.height(6.dp))
                    Text(state.comment!!, color = LessonsColors.TextBody, fontFamily = nunitoFamily(), fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun EmptyDetail(text: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(LessonsColors.CardBackground),
        contentAlignment = Alignment.Center,
    ) {
        if (text.isNotEmpty()) {
            Text(text, color = LessonsColors.TextMuted, fontFamily = nunitoFamily(), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

fun statusColor(isFinished: Boolean, isPaid: Boolean): Color = when {
    isPaid -> LessonsColors.Success
    isFinished -> LessonsColors.Primary
    else -> LessonsColors.Warning
}
