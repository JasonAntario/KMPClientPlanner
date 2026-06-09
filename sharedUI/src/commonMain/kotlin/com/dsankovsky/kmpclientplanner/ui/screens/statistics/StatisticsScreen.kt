package com.dsankovsky.kmpclientplanner.ui.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.components.HeaderView
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIDate
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.components.KufarPieChart
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.components.StatisticsCurrencyCardView
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.model.StatisticsClientItem
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.statistics_by_client
import kmpclientplanner.sharedui.generated.resources.statistics_expected
import kmpclientplanner.sharedui.generated.resources.statistics_expected_in_period
import kmpclientplanner.sharedui.generated.resources.statistics_income_in_period
import kmpclientplanner.sharedui.generated.resources.statistics_paid
import kmpclientplanner.sharedui.generated.resources.statistics_title
import kmpclientplanner.sharedui.generated.resources.tabs_all_time
import kmpclientplanner.sharedui.generated.resources.tabs_day
import kmpclientplanner.sharedui.generated.resources.tabs_month
import kmpclientplanner.sharedui.generated.resources.tabs_week
import kmpclientplanner.sharedui.generated.resources.tabs_year
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier
) {

    val viewModel: StatisticsScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleAction(StatisticsScreenAction.LoadData)
    }

    StatisticsScreenContent(state, onAction = viewModel::handleAction, modifier = modifier)
}


@Composable
fun StatisticsScreenContent(
    state: StatisticsScreenState,
    onAction: (StatisticsScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            top = 24.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = 100.dp
        ).withNavBarPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            HeaderView(
                stringResource(Res.string.statistics_title),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
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
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
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
                        Column {
                            Text(
                                text = "${start.toUIDate()} — ${end.toUIDate()}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        item {
            KufarPieChart(
                paidAmount = state.receivedTotal,
                expectedAmount = state.expectedTotal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
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
                Text(
                    text = stringResource(Res.string.statistics_by_client),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
        }

        items(state.itemsByClients) { item ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.client.getFullName(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                StatisticsCurrencyCardView(
                    title = stringResource(Res.string.statistics_paid),
                    items = item.income,
                    modifier = Modifier.fillMaxWidth()
                )
                StatisticsCurrencyCardView(
                    title = stringResource(Res.string.statistics_expected),
                    items = item.mustBePaid,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ServicesFilter.toTabLabel(): String = when (this) {
    ServicesFilter.DAY -> stringResource(Res.string.tabs_day)
    ServicesFilter.CURRENT_WEEK -> stringResource(Res.string.tabs_week)
    ServicesFilter.CURRENT_MONTH -> stringResource(Res.string.tabs_month)
    ServicesFilter.YEAR -> stringResource(Res.string.tabs_year)
    ServicesFilter.ALL_TIME -> stringResource(Res.string.tabs_all_time)
    else -> name
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
