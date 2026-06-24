package com.dsankovsky.kmpclientplanner.uinew.desktop.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily

/** Rounded selectable filter chip (Today / Tomorrow / Week / Month, statistics periods, …). */
@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .then(
                if (selected) Modifier.background(LessonsColors.Primary)
                else Modifier.background(LessonsColors.CardBackground).border(1.dp, LessonsColors.Border, shape)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else LessonsColors.TextSecondary,
            fontFamily = nunitoFamily(),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 12.sp,
        )
    }
}

/** Status pill (e.g. "Проведено" / "Не оплачено") with icon + tinted background. */
@Composable
fun StatusPill(
    label: String,
    icon: ImageVector,
    contentColor: Color,
    background: Color,
    border: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(13.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(background)
            .border(1.dp, border, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
        Text(
            text = label,
            color = contentColor,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
        )
    }
}

/** Small colored status dot used in list rows. */
@Composable
fun StatusDot(color: Color, modifier: Modifier = Modifier, size: Int = 9) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
    )
}

/** Pill-style icon button (square, rounded) used for edit/delete actions. */
@Composable
fun IconActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LessonsColors.Primary,
    background: Color = LessonsColors.CardBackground,
    border: BorderStroke = BorderStroke(1.dp, LessonsColors.BorderNeutral),
) {
    val shape = RoundedCornerShape(12.dp)
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .size(42.dp)
            .clip(shape)
            .background(background)
            .border(border, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
    }
}
