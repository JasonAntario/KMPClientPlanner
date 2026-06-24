package com.dsankovsky.kmpclientplanner.uinew.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily

/**
 * Buttons from the "04 — Components" section of the Lessons design system.
 *
 * Variants:
 * - [LessonsPrimaryButton]   — terracotta, the single dominant call to action.
 * - [LessonsSecondaryButton] — white / bordered, neutral action.
 * - [LessonsDoneButton]      — soft green, confirms / completes.
 * - [LessonsDangerButton]    — soft red, destructive.
 * - [LessonsFab]             — 56dp round-rect floating action button.
 */

/** Filled terracotta primary action. Height 50, radius 14, soft brand glow. */
@Composable
fun LessonsPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .height(50.dp)
            .shadow(
                if (enabled) 12.dp else 0.dp,
                shape,
                spotColor = LessonsColors.Primary,
                ambientColor = LessonsColors.Primary
            )
            .clip(shape)
            .background(if (enabled) LessonsColors.Primary else LessonsColors.PrimaryTintBorder)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = label,
            color = Color.White,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
        )
    }
}

/** White, bordered neutral action. Height 50, radius 14. */
@Composable
fun LessonsSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .height(50.dp)
            .clip(shape)
            .background(LessonsColors.CardBackground)
            .border(1.5.dp, LessonsColors.Border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = LessonsColors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = label,
            color = LessonsColors.TextSecondary,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
        )
    }
}

/** Soft-green confirm/complete action. Height 46, radius 13. */
@Composable
fun LessonsDoneButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = tonalButton(
    label = label,
    icon = icon,
    onClick = onClick,
    modifier = modifier,
    contentColor = LessonsColors.Success,
    background = LessonsColors.SuccessTint,
    border = LessonsColors.SuccessTintBorder,
)

/** Soft-red destructive action. Height 46, radius 13. */
@Composable
fun LessonsDangerButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = tonalButton(
    label = label,
    icon = icon,
    onClick = onClick,
    modifier = modifier,
    contentColor = LessonsColors.Danger,
    background = LessonsColors.DangerTint,
    border = LessonsColors.DangerTintBorder,
)

@Composable
private fun tonalButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier,
    contentColor: Color,
    background: Color,
    border: Color,
) {
    val shape = RoundedCornerShape(13.dp)
    Row(
        modifier = modifier
            .height(46.dp)
            .clip(shape)
            .background(background)
            .border(1.5.dp, border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
        Text(
            text = label,
            color = contentColor,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
        )
    }
}

/** Round-rect floating action button. 56dp, radius 20, terracotta glow. */
@Composable
fun LessonsFab(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                16.dp,
                shape,
                spotColor = LessonsColors.Primary,
                ambientColor = LessonsColors.Primary
            )
            .clip(shape)
            .background(LessonsColors.Primary)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
    }
}

@Preview
@Composable
private fun LessonsPrimaryButtonPreview() = ComponentPreview {
    LessonsPrimaryButton(label = "Добавить", onClick = {}, icon = Icons.Filled.Add)
}

@Preview
@Composable
private fun LessonsSecondaryButtonPreview() = ComponentPreview {
    LessonsSecondaryButton(label = "Отмена", onClick = {})
}

@Preview
@Composable
private fun LessonsDoneButtonPreview() = ComponentPreview {
    LessonsDoneButton(label = "Проведено", icon = Icons.Filled.CheckCircle, onClick = {})
}

@Preview
@Composable
private fun LessonsDangerButtonPreview() = ComponentPreview {
    LessonsDangerButton(label = "Удалить", icon = Icons.Filled.Delete, onClick = {})
}

@Preview
@Composable
private fun LessonsFabPreview() = ComponentPreview {
    LessonsFab(icon = Icons.Filled.Add, onClick = {})
}
