@file:OptIn(ExperimentalMaterial3Api::class)

package com.dsankovsky.kmpclientplanner.ui.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicCard
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicProgressBar
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicScreenHeader
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableCell
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableHeader
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableHeaderCell
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableRow
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.SegmentedControl
import com.dsankovsky.kmpclientplanner.ui.design.elevationSm
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIDate
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIAmount
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIMoney
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.model.StatisticsClientItem
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.date_picker_cancel
import kmpclientplanner.sharedui.generated.resources.date_picker_confirm
import kmpclientplanner.sharedui.generated.resources.statistics_awaiting
import kmpclientplanner.sharedui.generated.resources.statistics_by_client
import kmpclientplanner.sharedui.generated.resources.statistics_debt_summary
import kmpclientplanner.sharedui.generated.resources.statistics_empty_value
import kmpclientplanner.sharedui.generated.resources.statistics_paid_of_total
import kmpclientplanner.sharedui.generated.resources.statistics_paid_percentage
import kmpclientplanner.sharedui.generated.resources.statistics_prepay
import kmpclientplanner.sharedui.generated.resources.statistics_received
import kmpclientplanner.sharedui.generated.resources.statistics_table_client
import kmpclientplanner.sharedui.generated.resources.statistics_table_note
import kmpclientplanner.sharedui.generated.resources.statistics_table_paid_count
import kmpclientplanner.sharedui.generated.resources.statistics_title
import kmpclientplanner.sharedui.generated.resources.tabs_current_month
import kmpclientplanner.sharedui.generated.resources.tabs_current_week
import kmpclientplanner.sharedui.generated.resources.tabs_custom_interval
import kmpclientplanner.sharedui.generated.resources.tabs_next_month
import kmpclientplanner.sharedui.generated.resources.tabs_next_week
import kmpclientplanner.sharedui.generated.resources.tabs_today
import kmpclientplanner.sharedui.generated.resources.tabs_tomorrow
import kotlin.math.roundToInt
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
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

/**
 * Экран 09 — статистика: три карточки-метрики и таблица клиентов по сумме выплат.
 *
 * Круговой диаграммы (`KufarPieChart`) в новом дизайне нет — процент оплаты показывает
 * полоса прогресса, а суммы разложены по валютам.
 */
@Composable
fun StatisticsScreenContent(
    state: StatisticsScreenState,
    onAction: (StatisticsScreenAction) -> Unit,
    onOpenPayServices: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (state.showDatePicker) {
        CustomIntervalPicker(onAction)
    }

    val spacing = OrganicTheme.spacing
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(OrganicTheme.colors.bg),
    ) {
        // Три метрики с числами 38 в ряд требуют места; на узком окне они встают колонкой,
        // иначе суммы переносятся по цифрам.
        val metricsInRow = maxWidth >= MetricsRowMinWidth
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 40.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            OrganicScreenHeader(
                title = stringResource(Res.string.statistics_title),
                subtitle = state.dateInterval?.let { (start, end) ->
                    "${start.toUIDate()} — ${end.toUIDate()}"
                },
                actions = {
                    SegmentedControl(
                        options = state.filters,
                        selected = state.currentFilter,
                        onSelect = { onAction(StatisticsScreenAction.OnFilterClicked(it)) },
                        optionLabel = { it.toTabLabel() },
                    )
                    OrganicButton(
                        text = stringResource(Res.string.statistics_prepay),
                        onClick = onOpenPayServices,
                        icon = OrganicIcons.Wallet,
                    )
                },
            )

            val received: @Composable (Modifier) -> Unit = { cardModifier ->
                MoneyCard(
                    title = stringResource(Res.string.statistics_received),
                    amounts = state.receivedTotalByCurrency,
                    modifier = cardModifier,
                )
            }
            val awaiting: @Composable (Modifier) -> Unit = { cardModifier ->
                MoneyCard(
                    title = stringResource(Res.string.statistics_awaiting),
                    amounts = state.expectedTotalByCurrency,
                    footer = stringResource(
                        Res.string.statistics_debt_summary,
                        state.servicesUnpaid,
                        state.clientsWithDebt,
                    ),
                    modifier = cardModifier,
                )
            }

            if (metricsInRow) {
                Row(
                    modifier = Modifier.height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space4),
                ) {
                    received(Modifier.weight(1.4f).fillMaxHeight())
                    PaidPercentageCard(state, Modifier.weight(1f).fillMaxHeight())
                    awaiting(Modifier.weight(1f).fillMaxHeight())
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    received(Modifier.fillMaxWidth())
                    PaidPercentageCard(state, Modifier.fillMaxWidth())
                    awaiting(Modifier.fillMaxWidth())
                }
            }

            ClientsTableCard(state)
        }
    }
}

/** Ниже этой ширины три карточки-метрики в ряд не читаются. */
private val MetricsRowMinWidth = 820.dp

/** Таблице нужна эта ширина, иначе заголовки колонок рвутся по буквам. */
private val ClientsTableMinWidth = 620.dp

/** Карточка с суммой: первая валюта крупно, остальные — строкой ниже. */
@Composable
private fun MoneyCard(
    title: String,
    amounts: List<StatisticsClientItem.StatisticsPaymentItem>,
    modifier: Modifier = Modifier,
    footer: String? = null,
) {
    OrganicCard(
        modifier = modifier.elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
        kicker = title,
        verticalGap = 6.dp,
    ) {
        val nonEmpty = amounts.filter { it.money > 0f }
        if (nonEmpty.isEmpty()) {
            OrganicText(
                text = 0f.toUIMoney(CurrencyItem.BYN),
                style = OrganicTheme.typography.numericLarge,
            )
        } else {
            OrganicText(
                text = nonEmpty.first().money.toUIMoney(nonEmpty.first().currency),
                style = OrganicTheme.typography.numericLarge,
            )
            nonEmpty.drop(1).forEach { item ->
                OrganicText(
                    text = "+ ${item.money.toUIMoney(item.currency)}",
                    style = OrganicTheme.typography.h3.copy(fontSize = 24.sp),
                    color = OrganicTheme.colors.muted,
                )
            }
        }
        if (footer != null) {
            OrganicText(
                text = footer,
                style = OrganicTheme.typography.label,
                color = OrganicTheme.colors.muted,
            )
        }
    }
}

@Composable
private fun PaidPercentageCard(state: StatisticsScreenState, modifier: Modifier = Modifier) {
    OrganicCard(
        modifier = modifier.elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
        kicker = stringResource(Res.string.statistics_paid_percentage),
        verticalGap = OrganicTheme.spacing.space2,
    ) {
        OrganicText(
            text = "${(state.paidPercentage * 100).roundToInt()}%",
            style = OrganicTheme.typography.numericLarge,
            color = OrganicTheme.colors.accentText,
        )
        OrganicProgressBar(progress = state.paidPercentage)
        OrganicText(
            text = stringResource(
                Res.string.statistics_paid_of_total,
                state.servicesPaid,
                state.servicesTotal,
            ),
            style = OrganicTheme.typography.label,
            color = OrganicTheme.colors.muted,
        )
    }
}

/** Таблица «Клиенты по сумме выплат»: колонка на каждую встреченную валюту. */
@Composable
private fun ClientsTableCard(state: StatisticsScreenState) {
    val currencies = state.itemsByClients
        .flatMap { it.income }
        .filter { it.money > 0f }
        .map { it.currency }
        .distinct()
        .ifEmpty { listOf(CurrencyItem.BYN) }
    val dash = stringResource(Res.string.statistics_empty_value)

    OrganicCard(
        modifier = Modifier.elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
        kicker = stringResource(Res.string.statistics_by_client),
        verticalGap = OrganicTheme.spacing.space3,
    ) {
        // Колонок минимум четыре: на узком окне таблица не сжимается, а прокручивается вбок.
        // Ширина задаётся явно — под `horizontalScroll` она бесконечна, а `weight` колонок
        // требует ограниченной.
        BoxWithConstraints {
            val available = maxWidth
            Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Column(
                    modifier = Modifier.width(maxOf(available, ClientsTableMinWidth)),
                    verticalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space3),
                ) {
                    OrganicTableHeader {
                        OrganicTableHeaderCell(
                            text = stringResource(Res.string.statistics_table_client),
                            modifier = Modifier.weight(1.6f),
                        )
                        OrganicTableHeaderCell(
                            text = stringResource(Res.string.statistics_table_paid_count),
                            modifier = Modifier.weight(1f),
                        )
                        currencies.forEach { currency ->
                            OrganicTableHeaderCell(currency.code, Modifier.weight(0.8f))
                        }
                    }
                    state.itemsByClients.forEach { item ->
                        OrganicTableRow {
                            OrganicTableCell(item.client.getFullName(), Modifier.weight(1.6f))
                            OrganicTableCell(
                                text = item.paidServicesCount.toString(),
                                modifier = Modifier.weight(1f),
                                numeric = true,
                            )
                            currencies.forEach { currency ->
                                val money =
                                    item.income.firstOrNull { it.currency == currency }?.money ?: 0f
                                OrganicTableCell(
                                    text = if (money > 0f) money.toUIAmount() else dash,
                                    modifier = Modifier.weight(0.8f),
                                    numeric = true,
                                )
                            }
                        }
                    }
                    OrganicText(
                        text = stringResource(Res.string.statistics_table_note),
                        style = OrganicTheme.typography.label,
                        color = OrganicTheme.colors.muted,
                        textAlign = TextAlign.Start,
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomIntervalPicker(onAction: (StatisticsScreenAction) -> Unit) {
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
        DateRangePicker(state = dateRangePickerState, modifier = Modifier.weight(1f))
    }
}

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

@Preview
@Composable
private fun StatisticsScreenContentPreview() {
    OrganicTheme {
        StatisticsScreenContent(
            state = StatisticsScreenState(
                isLoading = false,
                paidPercentage = 0.78f,
                servicesTotal = 34,
                servicesPaid = 26,
                servicesUnpaid = 8,
                clientsWithDebt = 4,
                receivedTotalByCurrency = listOf(
                    StatisticsClientItem.StatisticsPaymentItem(1240f, CurrencyItem.BYN),
                    StatisticsClientItem.StatisticsPaymentItem(275f, CurrencyItem.USD),
                ),
                expectedTotalByCurrency = listOf(
                    StatisticsClientItem.StatisticsPaymentItem(320f, CurrencyItem.BYN),
                ),
                itemsByClients = listOf(
                    StatisticsClientItem(
                        client = BaseClient(name = "Ирина", surname = "Мороз"),
                        income = listOf(
                            StatisticsClientItem.StatisticsPaymentItem(250f, CurrencyItem.USD),
                        ),
                        mustBePaid = emptyList(),
                    ),
                    StatisticsClientItem(
                        client = BaseClient(name = "Олег", surname = "Тарасов"),
                        income = listOf(
                            StatisticsClientItem.StatisticsPaymentItem(480f, CurrencyItem.BYN),
                        ),
                        mustBePaid = emptyList(),
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
