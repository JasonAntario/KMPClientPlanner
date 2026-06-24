package com.dsankovsky.kmpclientplanner.uinew.desktop.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.LessonsPrimaryButton
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsDesktopTheme
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily

private data class WelcomeFeature(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
)

private val welcomeFeatures = listOf(
    WelcomeFeature(Icons.Filled.Groups, "Клиенты", "Карточки, история и контакты"),
    WelcomeFeature(Icons.Filled.CalendarMonth, "Занятия", "Расписание и статусы"),
    WelcomeFeature(Icons.Filled.Payments, "Оплаты", "Долги, предоплаты и доходы"),
)

/**
 * Desktop greeting screen shown before the service-type selection.
 * Styled to match the Lessons desktop design system (terracotta / cream / Nunito).
 */
@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(LessonsColors.RailBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.width(560.dp).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(LessonsColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.MenuBook,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp),
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Добро пожаловать в Блокнот!",
                color = LessonsColors.TextPrimary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Приложение позволяет вести учёт клиентов, услуг и оплат за оказываемые услуги",
                color = LessonsColors.TextSecondary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                welcomeFeatures.forEach { feature ->
                    FeatureRow(feature)
                }
            }
            Spacer(Modifier.height(32.dp))
            LessonsPrimaryButton(
                label = "Начать",
                onClick = onStart,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
            )
        }
    }
}

@Composable
private fun FeatureRow(feature: WelcomeFeature) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(LessonsColors.CardBackground)
            .border(1.dp, LessonsColors.BorderCard, shape)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LessonsColors.PrimaryTint),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                feature.icon,
                null,
                tint = LessonsColors.Primary,
                modifier = Modifier.size(20.dp),
            )
        }
        Column {
            Text(
                feature.title,
                color = LessonsColors.TextPrimary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                feature.subtitle,
                color = LessonsColors.TextSecondary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            )
        }
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    LessonsDesktopTheme {
        WelcomeScreen(onStart = {})
    }
}
