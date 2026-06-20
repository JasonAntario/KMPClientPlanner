package com.dsankovsky.kmpclientplanner.uinew.desktop.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily

private data class ProfessionOption(
    val label: String,
    val icon: ImageVector,
    val type: ServiceType,
)

private val professions = listOf(
    ProfessionOption("Репетитор", Icons.Filled.School, ServiceType.EDUCATION),
    ProfessionOption("Тату-мастер", Icons.Filled.Brush, ServiceType.TATTOO),
    ProfessionOption("Бьюти-мастер", Icons.Filled.Spa, ServiceType.BEAUTY),
    ProfessionOption("Тренер", Icons.Filled.FitnessCenter, ServiceType.SPORT),
    ProfessionOption("Другое", Icons.Filled.MoreHoriz, ServiceType.BASE),
)

@Composable
fun OnboardingScreen(onServiceTypeSelected: (ServiceType) -> Unit) {
    var selected by remember { mutableStateOf(professions.first()) }

    Box(
        modifier = Modifier.fillMaxSize().background(LessonsColors.RailBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.width(620.dp).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LessonsColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.School, null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "Чем вы занимаетесь?",
                color = LessonsColors.TextPrimary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Подберём поля и инструменты под вашу профессию",
                color = LessonsColors.TextSecondary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                professions.forEach { option ->
                    ProfessionCard(
                        option = option,
                        selected = option == selected,
                        onClick = { selected = option },
                    )
                }
            }
            Spacer(Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LessonsColors.Primary)
                    .clickable { onServiceTypeSelected(selected.type) }
                    .padding(horizontal = 36.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    "Продолжить",
                    color = Color.White,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                )
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun ProfessionCard(option: ProfessionOption, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .width(150.dp)
            .clip(shape)
            .background(LessonsColors.CardBackground)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) LessonsColors.Primary else LessonsColors.BorderNeutral,
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (selected) LessonsColors.PrimaryTint else LessonsColors.RailBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                option.icon,
                null,
                tint = if (selected) LessonsColors.Primary else LessonsColors.TextSecondary,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            option.label,
            color = LessonsColors.TextPrimary,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )
    }
}
