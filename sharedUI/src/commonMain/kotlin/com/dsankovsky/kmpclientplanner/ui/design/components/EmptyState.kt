package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcon
import androidx.compose.ui.tooling.preview.Preview
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons

/**
 * Пустое состояние (экраны 02 и 03): кружок 132 с иконкой, h2, текст 16 muted
 * и primary-кнопка действия.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    circleColor: Color = OrganicTheme.colors.accent2Ramp.s200,
    iconColor: Color = OrganicTheme.colors.accent2Ramp.s800,
) {
    val spacing = OrganicTheme.spacing
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.space4),
        ) {
            Box(
                Modifier.size(132.dp).background(circleColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                OrganicIcon(icon, contentDescription = null, size = 52.dp, tint = iconColor)
            }
            OrganicText(title, style = OrganicTheme.typography.h2, textAlign = TextAlign.Center)
            if (description != null) {
                OrganicText(
                    text = description,
                    style = OrganicTheme.typography.body.copy(fontSize = 16.sp),
                    color = OrganicTheme.colors.muted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 420.dp),
                )
            }
            if (actionText != null && onAction != null) {
                OrganicButton(text = actionText, onClick = onAction)
            }
        }
    }
}

@Preview
@Composable
private fun EmptyStatePreview() {
    PreviewSurface(width = 640.dp) {
        Box(Modifier.size(600.dp, 420.dp)) {
            EmptyState(
                icon = OrganicIcons.UserPlus,
                title = "Начнём с первого клиента",
                description = "Добавьте клиента — и занятия можно будет ставить в один клик.",
                actionText = "Добавить клиента",
                onAction = {},
            )
        }
    }
}

@Preview
@Composable
private fun EmptyStateServicesPreview() {
    PreviewSurface(width = 640.dp) {
        Box(Modifier.size(600.dp, 420.dp)) {
            EmptyState(
                icon = OrganicIcons.CalendarDays,
                title = "Занятий пока нет",
                actionText = "Добавить услугу",
                onAction = {},
                circleColor = OrganicTheme.colors.accentRamp.s200,
                iconColor = OrganicTheme.colors.accentRamp.s800,
            )
        }
    }
}
