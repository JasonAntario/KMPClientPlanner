package com.dsankovsky.kmpclientplanner.uinew.desktop.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.StatisticsScreenAction
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.StatisticsScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.StatisticsScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.model.StatisticsClientItem
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.FilterChip
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.LessonsCard
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.LessonsProgressBar
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.RailContentScaffold
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily
import kotlin.math.roundToInt
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DesktopStatisticsScreen(rail: @Composable () -> Unit) {
    val viewModel: StatisticsScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.handleAction(StatisticsScreenAction.LoadData) }

    RailContentScaffold(
        rail = rail,
        content = {
            Column(
                Modifier.fillMaxSize().background(LessonsColors.CardBackground).padding(horizontal = 28.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Статистика", color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        state.filters.forEach { filter ->
                            FilterChip(
                                label = filter.toLabel(),
                                selected = filter == state.currentFilter,
                                onClick = { viewModel.handleAction(StatisticsScreenAction.OnFilterClicked(filter)) },
                            )
                        }
                    }
                }

                SummaryRow(state)

                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CurrencyPanel(state, modifier = Modifier.weight(1.25f).fillMaxHeight())
                    ClientsPanel(state.itemsByClients, modifier = Modifier.weight(1f).fillMaxHeight())
                }
            }
        },
    )
}

@Composable
private fun SummaryRow(state: StatisticsScreenState) {
    val total = state.receivedTotal + state.expectedTotal
    val paidPct = if (total > 0f) state.receivedTotal / total else 0f
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        LessonsCard(modifier = Modifier.weight(1f), radius = 18.dp) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                Column {
                    Text("ОПЛАЧЕНО", color = LessonsColors.TextMuted, fontFamily = nunitoFamily(), fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                    Text("${(paidPct * 100).roundToInt()}%", color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 40.sp)
                }
                Column(Modifier.weight(1f)) {
                    LessonsProgressBar(progress = paidPct, modifier = Modifier.fillMaxWidth(), height = 10.dp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Получено ${state.receivedTotal.roundToInt()} · ожидается ${state.expectedTotal.roundToInt()}",
                        color = LessonsColors.TextMuted, fontFamily = nunitoFamily(), fontWeight = FontWeight.SemiBold, fontSize = 12.sp,
                    )
                }
            }
        }
        MiniStat("${state.receivedTotalByCurrency.size}", "валют за период")
        MiniStat("${state.itemsByClients.size}", "клиентов за период")
    }
}

@Composable
private fun MiniStat(value: String, caption: String) {
    LessonsCard(modifier = Modifier.width(150.dp), radius = 18.dp) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            Text(value, color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
            Spacer(Modifier.height(4.dp))
            Text(caption, color = LessonsColors.TextMuted, fontFamily = nunitoFamily(), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun CurrencyPanel(state: StatisticsScreenState, modifier: Modifier = Modifier) {
    LessonsCard(modifier = modifier, radius = 18.dp) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Доход по валютам", color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                Text("${state.receivedTotalByCurrency.size} валют", color = LessonsColors.TextMuted, fontFamily = nunitoFamily(), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
            LazyColumn(contentPadding = PaddingValues(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.receivedTotalByCurrency) { item ->
                    val expected = state.expectedTotalByCurrency.firstOrNull { it.currency == item.currency }?.money ?: 0f
                    val denom = item.money + expected
                    val pct = if (denom > 0f) item.money / denom else 1f
                    CurrencyRow(code = item.currency.code, money = item.money.roundToInt(), pct = pct)
                }
            }
        }
    }
}

@Composable
private fun CurrencyRow(code: String, money: Int, pct: Float) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp)).background(LessonsColors.CardBackground)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.height(34.dp).width(52.dp).clip(RoundedCornerShape(9.dp)).background(LessonsColors.RailBackground),
            contentAlignment = Alignment.Center,
        ) {
            Text(code, color = LessonsColors.TextSecondary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
        Text("$money", color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, modifier = Modifier.width(110.dp))
        LessonsProgressBar(
            progress = pct,
            modifier = Modifier.weight(1f),
            fill = if (pct >= 0.7f) LessonsColors.Success else LessonsColors.Warning,
        )
        Text(
            "${(pct * 100).roundToInt()}%",
            color = if (pct >= 0.7f) LessonsColors.Success else LessonsColors.WarningStrong,
            fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp,
            modifier = Modifier.width(46.dp),
        )
    }
}

@Composable
private fun ClientsPanel(items: List<StatisticsClientItem>, modifier: Modifier = Modifier) {
    val ranked = items
        .map { it to it.income.sumOf { p -> p.money.toDouble() } }
        .sortedByDescending { it.second }
    LessonsCard(modifier = modifier, radius = 18.dp) {
        Column(Modifier.fillMaxSize()) {
            Text(
                "Клиенты по выплатам",
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 12.dp),
                color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp,
            )
            LazyColumn(contentPadding = PaddingValues(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(ranked) { index, (clientItem, total) ->
                    val code = clientItem.income.firstOrNull()?.currency?.code ?: clientItem.client.currency.code
                    ClientRankRow(rank = index + 1, name = clientItem.client.getFullName(), lessons = clientItem.income.size, total = total.roundToInt(), code = code)
                }
            }
        }
    }
}

@Composable
private fun ClientRankRow(rank: Int, name: String, lessons: Int, total: Int, code: String) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp)).background(LessonsColors.CardBackground)
            .padding(horizontal = 13.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Box(
            Modifier.size(34.dp).clip(CircleShape).background(if (rank == 1) LessonsColors.Success else LessonsColors.TextMuted),
            contentAlignment = Alignment.Center,
        ) {
            Text("$rank", color = Color.White, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
        }
        Column(Modifier.weight(1f)) {
            Text(name, color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
            Text("$lessons занятий", color = LessonsColors.TextMuted, fontFamily = nunitoFamily(), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
        }
        Text("$total $code", color = LessonsColors.TextPrimary, fontFamily = nunitoFamily(), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
    }
}
