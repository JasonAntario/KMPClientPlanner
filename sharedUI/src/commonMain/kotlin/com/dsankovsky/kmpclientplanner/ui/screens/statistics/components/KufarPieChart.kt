package com.dsankovsky.kmpclientplanner.ui.screens.statistics.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.statistics_paid
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun KufarPieChart(
    paidAmount: Float,
    expectedAmount: Float,
    modifier: Modifier = Modifier
) {

    val total = paidAmount + expectedAmount

    val percentage by animateFloatAsState(
        targetValue = when {
            total == 0f -> 0f
            paidAmount == 0f && total > 0f -> 0f
            else -> paidAmount / total
        },
        animationSpec = tween(durationMillis = 1000)
    )


    val coloredAngle = 360 * percentage
    val unfinishedTrackColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp), contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .rotate(90f)
        ) {
            drawArc(
                startAngle = 0f,
                sweepAngle = coloredAngle,
                useCenter = false,
                style = Stroke(
                    width = 20.dp.toPx(),
                    join = StrokeJoin.Round,
                    cap = StrokeCap.Butt
                ),
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0xFF00AD64),
                        Color(0xFF72D154),
                        Color(0xFFC0F936)
                    )
                )
            )
            drawArc(
                startAngle = coloredAngle,
                sweepAngle = 360f - coloredAngle,
                useCenter = false,
                style = Stroke(
                    width = 20.dp.toPx(),
                    join = StrokeJoin.Round,
                    cap = StrokeCap.Butt
                ),
                color = unfinishedTrackColor
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(Res.string.statistics_paid),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${(percentage * 100).roundToInt()} %",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewKufarPieChart() {
    ClientPlannerTheme {
        KufarPieChart(
            paidAmount = 80f,
            expectedAmount = 20f
        )
    }
}