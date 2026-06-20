package com.dsankovsky.kmpclientplanner.uinew.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily
import kotlin.math.abs

/** A rounded, bordered surface used for cards/panels throughout the design. */
@Composable
fun LessonsCard(
    modifier: Modifier = Modifier,
    background: Color = LessonsColors.PanelBackground,
    border: Color = LessonsColors.BorderCard,
    radius: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(background)
            .border(1.dp, border, shape),
    ) { content() }
}

/** Circular avatar showing the initials, with a deterministic color from the palette. */
@Composable
fun InitialsAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    fontSize: Int = 13,
    color: Color = colorForInitials(initials),
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = fontSize.sp,
        )
    }
}

fun colorForInitials(initials: String): Color {
    val palette = LessonsColors.AvatarColors
    if (initials.isEmpty()) return palette.first()
    val idx = abs(initials.sumOf { it.code }) % palette.size
    return palette[idx]
}

/** Horizontal progress bar with rounded track + filled portion. */
@Composable
fun LessonsProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    fill: Color = LessonsColors.Success,
    track: Color = LessonsColors.Track,
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height))
            .background(track),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(height))
                .background(fill),
        )
    }
}

/** A small stat block: big value + caption. */
@Composable
fun StatTile(
    value: String,
    caption: String,
    modifier: Modifier = Modifier,
    valueColor: Color = LessonsColors.TextPrimary,
) {
    LessonsCard(modifier = modifier, radius = 14.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                color = valueColor,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
            )
            Text(
                text = caption,
                color = LessonsColors.TextMuted,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Uppercase section caption used above grouped content. */
@Composable
fun SectionCaption(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = LessonsColors.TextMuted,
        fontFamily = nunitoFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 0.6.sp,
    )
}
