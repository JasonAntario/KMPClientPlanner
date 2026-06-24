package com.dsankovsky.kmpclientplanner.uinew.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily

/**
 * Segmented control (e.g. "Planned / Done"). A sunken track holds equal-width
 * segments; the selected one floats up as a white pill with terracotta text.
 */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LessonsColors.Track)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .then(if (selected) Modifier.background(LessonsColors.CardBackground) else Modifier)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelect(index) },
                    )
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (selected) LessonsColors.Primary else LessonsColors.TextMuted,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Money / payment-status palette for [StatusChip]. */
enum class PaymentStatus(
    val label: String,
    val contentColor: Color,
    val background: Color,
) {
    Paid("Paid", LessonsColors.Success, LessonsColors.SuccessTint),
    Unpaid("Unpaid", LessonsColors.Primary, LessonsColors.PrimaryTint),
    Pending("Pending", LessonsColors.WarningStrong, LessonsColors.WarningTint),
}

/**
 * Compact status chip: a small colored dot + label on a tinted rounded background.
 * Smaller and lighter than [StatusPill]; used inline in list rows and headers.
 */
@Composable
fun StatusChip(
    label: String,
    contentColor: Color,
    background: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 11.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(contentColor),
        )
        Text(
            text = label,
            color = contentColor,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
    }
}

/** Convenience overload that pulls colors from a [PaymentStatus]. */
@Composable
fun StatusChip(
    status: PaymentStatus,
    modifier: Modifier = Modifier,
    label: String = status.label,
) = StatusChip(
    label = label,
    contentColor = status.contentColor,
    background = status.background,
    modifier = modifier,
)

@Preview
@Composable
private fun SegmentedControlPreview() = ComponentPreview {
    var selected by remember { mutableStateOf(0) }
    SegmentedControl(
        options = listOf("Запланировано", "Проведено"),
        selectedIndex = selected,
        onSelect = { selected = it },
        modifier = Modifier.width(300.dp),
    )
}

@Preview
@Composable
private fun StatusChipPreview() = ComponentPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatusChip(PaymentStatus.Paid)
        StatusChip(PaymentStatus.Unpaid)
        StatusChip(PaymentStatus.Pending)
    }
}
