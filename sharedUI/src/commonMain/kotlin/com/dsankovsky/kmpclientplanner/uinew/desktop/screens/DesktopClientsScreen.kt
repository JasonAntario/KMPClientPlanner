package com.dsankovsky.kmpclientplanner.uinew.desktop.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsActions
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientListItem
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsListScreenAction
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenItem
import com.dsankovsky.kmpclientplanner.ui.screens.services_history.ServicesHistoryScreenAction
import com.dsankovsky.kmpclientplanner.ui.screens.services_history.ServicesHistoryScreenViewModel
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.IconActionButton
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.InitialsAvatar
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.LessonsCard
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.MasterDetailScaffold
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.SectionCaption
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.StatTile
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.StatusDot
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun DesktopClientsScreen(
    rail: @Composable () -> Unit,
    onAddClient: () -> Unit,
    onEditClient: (Long) -> Unit,
) {
    val viewModel: ClientsScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.handleAction(ClientsListScreenAction.LoadClientsList) }

    val clients = remember(state.clients) {
        state.clients.filterIsInstance<ClientListItem.Client>().map { it.client }
    }
    var selectedId by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(clients) {
        if (clients.none { it.id == selectedId }) selectedId = clients.firstOrNull()?.id
    }

    MasterDetailScaffold(
        masterWidth = 380.dp,
        rail = rail,
        master = {
            ClientsMaster(
                items = state.clients,
                count = clients.size,
                selectedId = selectedId,
                onSelect = { selectedId = it },
            )
        },
        detail = {
            val selected = clients.firstOrNull { it.id == selectedId }
            if (selected == null) {
                EmptyDetail("Выберите клиента")
            } else {
                ClientDetail(
                    clientId = selected.id,
                    onEdit = { onEditClient(selected.id) },
                )
            }
        },
    )
}

@Composable
private fun ClientsMaster(
    items: List<ClientListItem>,
    count: Int,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(items, query) {
        if (query.isBlank()) items
        else items.filterIsInstance<ClientListItem.Client>()
            .filter { it.client.getFullName().contains(query, ignoreCase = true) }
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Клиенты",
                color = LessonsColors.TextPrimary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
            Text(
                "$count чел.",
                color = LessonsColors.TextMuted,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
        // Search
        Row(
            Modifier.padding(start = 18.dp, end = 18.dp, bottom = 12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(LessonsColors.CardBackground)
                .border(1.dp, LessonsColors.Border, RoundedCornerShape(12.dp))
                .padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            Icon(
                Icons.Filled.Search,
                null,
                tint = LessonsColors.TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        "Поиск…",
                        color = LessonsColors.TextMuted,
                        fontFamily = nunitoFamily(),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = LessonsColors.TextPrimary,
                        fontFamily = nunitoFamily(),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(filtered) { item ->
                when (item) {
                    is ClientListItem.LetterDivider -> Text(
                        item.letter,
                        modifier = Modifier.padding(start = 6.dp, top = 6.dp, bottom = 3.dp),
                        color = LessonsColors.Primary,
                        fontFamily = nunitoFamily(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                    )

                    is ClientListItem.Client -> ClientRow(
                        client = item.client,
                        selected = item.client.id == selectedId,
                        onClick = { onSelect(item.client.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientRow(client: BaseClient, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (selected) Modifier.background(LessonsColors.PrimaryTint)
                    .border(1.dp, LessonsColors.PrimaryTintBorder, shape)
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        InitialsAvatar(client.getShortName())
        Column(Modifier.weight(1f)) {
            Text(
                client.getFullName(),
                color = LessonsColors.TextPrimary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1
            )
            Text(
                client.serviceSubtype ?: "",
                color = LessonsColors.TextMuted,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun ClientDetail(clientId: Long, onEdit: () -> Unit) {
    val detailsViewModel: ClientDetailsViewModel = koinViewModel(key = "client_$clientId")
    val historyViewModel: ServicesHistoryScreenViewModel = koinViewModel(key = "history_$clientId")
    val state by detailsViewModel.state.collectAsStateWithLifecycle()
    val historyState by historyViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(clientId) {
        detailsViewModel.handleActions(ClientDetailsActions.LoadData(clientId))
        historyViewModel.handleAction(ServicesHistoryScreenAction.LoadData(clientId))
    }

    if (state.isLoading) {
        EmptyDetail("")
        return
    }

    val history = remember(historyState.items) {
        historyState.items.filterIsInstance<ServicesListScreenItem.ServiceItem>()
    }
    val currency = state.client.currency.code
    val paidTotal = history.filter { it.isPaid }.sumOf { (it.service.price ?: 0f).toDouble() }
    val debtTotal = history.filterNot { it.isPaid }.sumOf { (it.service.price ?: 0f).toDouble() }

    Column(
        Modifier.fillMaxSize().background(LessonsColors.CardBackground)
            .padding(horizontal = 28.dp, vertical = 26.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InitialsAvatar(state.clientShortName, size = 72.dp, fontSize = 24)
            Column(Modifier.weight(1f)) {
                Text(
                    state.clientName,
                    color = LessonsColors.TextPrimary,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                Text(
                    state.client.serviceSubtype ?: "",
                    color = LessonsColors.TextMuted,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
            IconActionButton(Icons.Filled.Edit, onEdit)
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                value = history.size.toString(),
                caption = "занятий",
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = "${paidTotal.roundToInt()} $currency",
                caption = "оплачено",
                valueColor = LessonsColors.Success,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = "${debtTotal.roundToInt()} $currency",
                caption = "в долгу",
                valueColor = LessonsColors.Primary,
                modifier = Modifier.weight(1f)
            )
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            state.phone?.takeIf { it.isNotBlank() }
                ?.let { ContactCard(Icons.Filled.Phone, it, Modifier.weight(1f)) }
            state.address?.takeIf { it.isNotBlank() }
                ?.let { ContactCard(Icons.Filled.LocationOn, it, Modifier.weight(1f)) }
        }

        SectionCaption("История занятий", modifier = Modifier.padding(top = 4.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(history, key = { it.id }) { item ->
                HistoryRow(item)
            }
        }
    }
}

@Composable
private fun ContactCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    modifier: Modifier = Modifier
) {
    LessonsCard(modifier = modifier, radius = 14.dp) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            Icon(icon, null, tint = LessonsColors.Primary, modifier = Modifier.size(17.dp))
            Text(
                value,
                color = LessonsColors.TextPrimary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun HistoryRow(item: ServicesListScreenItem.ServiceItem) {
    LessonsCard(radius = 13.dp) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    item.title,
                    color = LessonsColors.TextPrimary,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Text(
                    "${item.startDate.date} · ${item.startDate.startLabel()}",
                    color = LessonsColors.TextMuted,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            }
            StatusDot(statusColor(item.isFinished, item.isPaid))
            item.service.priceLabel()?.let {
                Text(
                    it,
                    color = LessonsColors.TextPrimary,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
