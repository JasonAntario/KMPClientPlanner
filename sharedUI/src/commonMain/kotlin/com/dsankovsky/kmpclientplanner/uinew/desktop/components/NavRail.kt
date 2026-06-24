package com.dsankovsky.kmpclientplanner.uinew.desktop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
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

enum class NavSection(val label: String, val icon: ImageVector) {
    Home("Главная", Icons.Filled.Home),
    Clients("Клиенты", Icons.Filled.People),
    Statistics("Статистика", Icons.Filled.BarChart),
    Settings("Настройки", Icons.Filled.Settings),
}

/** Bottom primary action of the rail (Add lesson / Add client / Prepay). */
data class RailAction(val label: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun NavRail(
    selected: NavSection,
    onSelect: (NavSection) -> Unit,
    professionLabel: String,
    action: RailAction?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(236.dp)
            .fillMaxHeight()
            .background(LessonsColors.RailBackground)
            .padding(horizontal = 14.dp, vertical = 20.dp),
    ) {
        // Logo + profession
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(LessonsColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(
                    "Lessons",
                    color = LessonsColors.TextPrimary,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                )
                if (professionLabel.isNotBlank()) {
                    Text(
                        professionLabel,
                        color = LessonsColors.TextMuted,
                        fontFamily = nunitoFamily(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                    )
                }
            }
        }

        // Sections
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            NavSection.entries.forEach { section ->
                RailItem(
                    section = section,
                    selected = section == selected,
                    onClick = { onSelect(section) },
                )
            }
        }

        Spacer(Modifier.weight(1f))

        if (action != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LessonsColors.Primary)
                    .clickable(onClick = action.onClick),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(action.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    action.label,
                    color = Color.White,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun RailItem(section: NavSection, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) LessonsColors.PrimaryTint else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            section.icon,
            contentDescription = null,
            tint = if (selected) LessonsColors.Primary else LessonsColors.TextSecondary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            section.label,
            color = if (selected) LessonsColors.Primary else LessonsColors.TextSecondary,
            fontFamily = nunitoFamily(),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 14.sp,
        )
    }
}
