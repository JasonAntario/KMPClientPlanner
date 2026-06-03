package com.dsankovsky.kmpclientplanner.ui.screens.statistics

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.components.KufarPieChart
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.components.KufarStatisticPaymentItem
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.model.StatisticsClientItem
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.statistics_expected_in_period
import kmpclientplanner.sharedui.generated.resources.statistics_income_in_period
import kmpclientplanner.sharedui.generated.resources.statistics_title
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
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = 24.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = 100.dp
        ).withNavBarPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                text = stringResource(Res.string.statistics_title),
            )
        }

//        item {
//            KufarSimpleScrollableTabRow(
//                tabItems = state.filters.map { it.tabItem },
//                selectedTabItem = state.currentFilter.tabItem,
//                modifier = Modifier
//                    .ignoreParentsPadding(horizontal = 16.dp)
//                    .fillMaxWidth(),
//                onSelectItem = { tab ->
//                    val filter = state.filters.first { it.tabItem == tab }
//                    onAction(StatisticsScreenAction.OnFilterClicked(filter))
//                }
//            )
//        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                ) {
                    Image(
                        contentDescription = null,
                        imageVector = Icons.Outlined.CalendarMonth,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }

                state.dateInterval?.let {
                    Text(
                        "interval",
                    )
                }
            }
        }

        item {
            KufarPieChart(
                paidAmount = state.receivedTotal,
                expectedAmount = state.expectedTotal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp, top = 24.dp)
            )
        }

        if (state.receivedTotalByCurrency.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.statistics_income_in_period),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        state.receivedTotalByCurrency.forEach {
                            Text(
                                "${it.money} ${it.currency.code}",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        if (state.expectedTotalByCurrency.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.statistics_expected_in_period),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        state.expectedTotalByCurrency.forEach {
                            Text(
                                "${it.money} ${it.currency.code}",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        items(state.itemsByClients) { item ->
            KufarStatisticPaymentItem(
                clientName = item.client.getFullName(),
                income = item.income,
                mustBePaid = item.mustBePaid
            )
        }
    }
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