package com.dsankovsky.kmpclientplanner.ui.screens.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.model.StatisticsClientItem.StatisticsPaymentItem
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.statistics_expected
import kmpclientplanner.sharedui.generated.resources.statistics_paid
import org.jetbrains.compose.resources.stringResource

@Composable
fun KufarStatisticPaymentItem(
    clientName: String,
    income: List<StatisticsPaymentItem>,
    mustBePaid: List<StatisticsPaymentItem>,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(clientName)
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(Res.string.statistics_paid),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    income.forEach {
                        Text(
                            "${it.money} ${it.currency.code}",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(Res.string.statistics_expected),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    mustBePaid.forEach {
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
}

@PreviewLightDark
@Composable
private fun PreviewKufarStatisticPaymentItem() {
    ClientPlannerTheme {
        KufarStatisticPaymentItem(
            clientName = "Marshall",
            income = listOf(
                StatisticsPaymentItem(100f, CurrencyItem.BYN),
                StatisticsPaymentItem(200f, CurrencyItem.USD)
            ),
            mustBePaid = listOf(
                StatisticsPaymentItem(300f, CurrencyItem.USD),
                StatisticsPaymentItem(400f, CurrencyItem.EUR)
            )
        )
    }
}