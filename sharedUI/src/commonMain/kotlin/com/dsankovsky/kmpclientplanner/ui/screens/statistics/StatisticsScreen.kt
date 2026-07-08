@file:OptIn(ExperimentalMaterial3Api::class)

package com.dsankovsky.kmpclientplanner.ui.screens.statistics

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIDate
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.components.KufarPieChart
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.components.StatisticsCurrencyCardView
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.model.StatisticsClientItem
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.date_picker_cancel
import kmpclientplanner.sharedui.generated.resources.date_picker_confirm
import kmpclientplanner.sharedui.generated.resources.statistics_by_client
import kmpclientplanner.sharedui.generated.resources.statistics_expected
import kmpclientplanner.sharedui.generated.resources.statistics_expected_in_period
import kmpclientplanner.sharedui.generated.resources.statistics_income_in_period
import kmpclientplanner.sharedui.generated.resources.statistics_title
import kmpclientplanner.sharedui.generated.resources.tabs_current_month
import kmpclientplanner.sharedui.generated.resources.tabs_current_week
import kmpclientplanner.sharedui.generated.resources.tabs_custom_interval
import kmpclientplanner.sharedui.generated.resources.tabs_next_month
import kmpclientplanner.sharedui.generated.resources.tabs_next_week
import kmpclientplanner.sharedui.generated.resources.tabs_today
import kmpclientplanner.sharedui.generated.resources.tabs_tomorrow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StatisticsScreen(
    onOpenPayServices: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    val viewModel: StatisticsScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleAction(StatisticsScreenAction.LoadData)
    }

    StatisticsScreenContent(
        state,
        onAction = viewModel::handleAction,
        onOpenPayServices = onOpenPayServices,
        modifier = modifier
    )
}


@Composable
fun StatisticsScreenContent(
    state: StatisticsScreenState,
    onAction: (StatisticsScreenAction) -> Unit,
    onOpenPayServices: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (state.showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { onAction(StatisticsScreenAction.CloseDatePickerClicked) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val startMillis = dateRangePickerState.selectedStartDateMillis
                        val endMillis = dateRangePickerState.selectedEndDateMillis
                        if (startMillis != null && endMillis != null) {
                            onAction(
                                StatisticsScreenAction.SetCustomInterval(
                                    startDate = Instant.fromEpochMilliseconds(startMillis)
                                        .toLocalDateTime(TimeZone.UTC).date,
                                    endDate = Instant.fromEpochMilliseconds(endMillis)
                                        .toLocalDateTime(TimeZone.UTC).date
                                )
                            )
                        }
                    }
                ) {
                    Text(stringResource(Res.string.date_picker_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(StatisticsScreenAction.CloseDatePickerClicked) }) {
                    Text(stringResource(Res.string.date_picker_cancel))
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f)
            )
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = 24.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 24.dp
            ).withNavBarPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(Res.string.statistics_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = onOpenPayServices,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCard,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text("Предоплатить")
                    }
                }
            }

            item {
                val selectedIndex = state.filters.indexOf(state.currentFilter)
                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedIndex,
                    edgePadding = 0.dp
                ) {
                    state.filters.forEachIndexed { index, filter ->
                        Tab(
                            selected = index == selectedIndex,
                            onClick = { onAction(StatisticsScreenAction.OnFilterClicked(filter)) },
                            text = {
                                Text(
                                    text = filter.toTabLabel(),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        )
                    }
                }
            }

            state.dateInterval?.let { (start, end) ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${start.toUIDate()} — ${end.toUIDate()}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            item {
                SummaryGradientCard(
                    label = stringResource(Res.string.statistics_income_in_period),
                    items = state.receivedTotalByCurrency,
                    fallbackAmount = state.receivedTotal,
                    clientsCount = state.itemsByClients.size,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Процент оплаты",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        KufarPieChart(
                            paidAmount = state.receivedTotal,
                            expectedAmount = state.expectedTotal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
            }

            if (state.receivedTotalByCurrency.isNotEmpty()) {
                item {
                    StatisticsCurrencyCardView(
                        title = stringResource(Res.string.statistics_income_in_period),
                        items = state.receivedTotalByCurrency,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (state.expectedTotalByCurrency.isNotEmpty()) {
                item {
                    StatisticsCurrencyCardView(
                        title = stringResource(Res.string.statistics_expected_in_period),
                        items = state.expectedTotalByCurrency,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (state.itemsByClients.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Leaderboard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(Res.string.statistics_by_client),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            items(state.itemsByClients) { item ->
                ClientPayoutRow(item)
            }
        }
    }
}

@Composable
private fun SummaryGradientCard(
    label: String,
    items: List<StatisticsClientItem.StatisticsPaymentItem>,
    fallbackAmount: Float,
    clientsCount: Int,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
    )
    val bigAmount = items.firstOrNull()?.let { "${it.money} ${it.currency.code}" }
        ?: "$fallbackAmount"
    val secondaryParts = buildList {
        items.drop(1).forEach { add("+ ${it.money} ${it.currency.code}") }
        if (clientsCount > 0) add("$clientsCount клиентов")
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
            )
            Text(
                text = bigAmount,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            if (secondaryParts.isNotEmpty()) {
                Text(
                    text = secondaryParts.joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun ClientPayoutRow(
    item: StatisticsClientItem,
    modifier: Modifier = Modifier
) {
    val paidSum = item.income.fold(0f) { acc, it -> acc + it.money }
    val expectedSum = item.mustBePaid.fold(0f) { acc, it -> acc + it.money }
    val share = if (paidSum + expectedSum > 0f) paidSum / (paidSum + expectedSum) else 0f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.client.getShortName(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.client.getFullName(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = item.income.formatAmounts(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerHighest,
                            CircleShape
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(share.coerceIn(0f, 1f))
                            .height(6.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
                if (item.mustBePaid.isNotEmpty()) {
                    Text(
                        text = "${stringResource(Res.string.statistics_expected)}: ${item.mustBePaid.formatAmounts()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun List<StatisticsClientItem.StatisticsPaymentItem>.formatAmounts(): String =
    if (isEmpty()) "—" else joinToString(" · ") { "${it.money} ${it.currency.code}" }

@Composable
private fun ServicesFilter.toTabLabel(): String = when (this) {
    ServicesFilter.TODAY -> stringResource(Res.string.tabs_today)
    ServicesFilter.TOMORROW -> stringResource(Res.string.tabs_tomorrow)
    ServicesFilter.CURRENT_WEEK -> stringResource(Res.string.tabs_current_week)
    ServicesFilter.NEXT_WEEK -> stringResource(Res.string.tabs_next_week)
    ServicesFilter.CURRENT_MONTH -> stringResource(Res.string.tabs_current_month)
    ServicesFilter.NEXT_MONTH -> stringResource(Res.string.tabs_next_month)
    ServicesFilter.CUSTOM_INTERVAL -> stringResource(Res.string.tabs_custom_interval)
}

@PreviewLightDark
@Composable
private fun PreviewStatisticsScreen() {
    ClientPlannerTheme {
        StatisticsScreenContent(
            StatisticsScreenState(
                paidPercentage = 1f,
                receivedTotal = 11f,
                expectedTotal = 34f,
                dateInterval = Pair(
                    getCurrentDateTime().date, getCurrentDateTime().date.plus(
                        DatePeriod(months = 2)
                    )
                ),
                receivedTotalByCurrency = listOf(
                    StatisticsClientItem.StatisticsPaymentItem(100f, CurrencyItem.BYN),
                    StatisticsClientItem.StatisticsPaymentItem(200f, CurrencyItem.EUR)
                ),
                expectedTotalByCurrency = listOf(
                    StatisticsClientItem.StatisticsPaymentItem(1000f, CurrencyItem.BYN),
                    StatisticsClientItem.StatisticsPaymentItem(2000f, CurrencyItem.EUR)
                ),
                itemsByClients = listOf(
                    StatisticsClientItem(
                        client = BaseClient(
                            name = "Otis",
                            surname = "Pes"
                        ),
                        income = listOf(
                            StatisticsClientItem.StatisticsPaymentItem(100f, CurrencyItem.BYN),
                            StatisticsClientItem.StatisticsPaymentItem(200f, CurrencyItem.EUR)
                        ),
                        mustBePaid = listOf(
                            StatisticsClientItem.StatisticsPaymentItem(300f, CurrencyItem.BYN),
                            StatisticsClientItem.StatisticsPaymentItem(400f, CurrencyItem.EUR)
                        )
                    ),
                    StatisticsClientItem(
                        client = BaseClient(
                            name = "Marshall",
                            surname = "Woof"
                        ),
                        income = listOf(
                            StatisticsClientItem.StatisticsPaymentItem(100f, CurrencyItem.BYN),
                            StatisticsClientItem.StatisticsPaymentItem(200f, CurrencyItem.USD)
                        ),
                        mustBePaid = listOf(
                            StatisticsClientItem.StatisticsPaymentItem(300f, CurrencyItem.USD),
                            StatisticsClientItem.StatisticsPaymentItem(400f, CurrencyItem.EUR)
                        )
                    ),
                    StatisticsClientItem(
                        client = BaseClient(
                            name = "Lloyd",
                            surname = "Banks"
                        ),
                        income = listOf(
                            StatisticsClientItem.StatisticsPaymentItem(100f, CurrencyItem.BYN),
                            StatisticsClientItem.StatisticsPaymentItem(200f, CurrencyItem.USD)
                        ),
                        mustBePaid = listOf(
                            StatisticsClientItem.StatisticsPaymentItem(300f, CurrencyItem.USD),
                            StatisticsClientItem.StatisticsPaymentItem(400f, CurrencyItem.EUR)
                        )
                    )
                )
            ), {})
    }
}
