package com.dsankovsky.kmpclientplanner.ui.screens.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.model.StatisticsClientItem.StatisticsPaymentItem
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme

@Composable
fun StatisticsCurrencyCardView(
    title: String,
    items: List<StatisticsPaymentItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider()
            items.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = it.currency.code,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${it.money} ${it.currency.code}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewStatisticsCurrencyCardView() {
    ClientPlannerTheme {
        StatisticsCurrencyCardView(
            title = "Income in period",
            items = listOf(
                StatisticsPaymentItem(100f, CurrencyItem.BYN),
                StatisticsPaymentItem(200f, CurrencyItem.EUR)
            )
        )
    }
}
