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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsScreenAction
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsScreenEvent
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsViewModel
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.RailContentScaffold
import com.dsankovsky.kmpclientplanner.uinew.desktop.components.SectionCaption
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.LessonsColors
import com.dsankovsky.kmpclientplanner.uinew.desktop.theme.nunitoFamily
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DesktopSettingsScreen(
    rail: @Composable () -> Unit,
    onDataCleared: () -> Unit,
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.handleActions(SettingsScreenAction.LoadData) }

    viewModel.event.collectWithLifecycle { event ->
        when (event) {
            SettingsScreenEvent.AllDataCleared -> onDataCleared()
        }
    }

    RailContentScaffold(
        rail = rail,
        content = {
            Column(
                Modifier.fillMaxSize().background(LessonsColors.CardBackground)
                    .padding(horizontal = 32.dp, vertical = 28.dp),
            ) {
                Text(
                    "Настройки",
                    color = LessonsColors.TextPrimary,
                    fontFamily = nunitoFamily(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                Spacer(Modifier.height(18.dp))
                Column(
                    Modifier.widthIn(max = 560.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    AccountCard()

                    // "Приложение" group
                    Column {
                        SectionCaption("Приложение", modifier = Modifier.padding(bottom = 8.dp))
                        GroupCard {
                            SettingsRow(
                                icon = Icons.Filled.Brush,
                                title = "Тема",
                                value = "Светлая",
                                showCaret = true,
                                showDivider = true,
                            )
                            ServiceTypeRow(
                                currentLabel = state.serviceType.toUIName(),
                                serviceTypes = state.serviceTypeList,
                                currentType = state.serviceType,
                                onSelect = { index ->
                                    viewModel.handleActions(
                                        SettingsScreenAction.OnServiceTypeSelected(
                                            index
                                        )
                                    )
                                },
                            )
                            SettingsRow(
                                icon = Icons.Filled.Info,
                                title = "Версия",
                                value = "${state.version} (MVP)",
                                showCaret = false,
                                showDivider = false,
                            )
                        }
                    }

                    // "Данные" group
                    Column {
                        SectionCaption("Данные", modifier = Modifier.padding(bottom = 8.dp))
                        GroupCard {
                            SettingsRow(
                                icon = Icons.Filled.Delete,
                                title = "Сбросить приложение",
                                value = "удалит все данные",
                                titleColor = LessonsColors.Danger,
                                iconColor = LessonsColors.Danger,
                                caretColor = LessonsColors.DangerTintBorder,
                                showCaret = true,
                                showDivider = false,
                                onClick = { viewModel.handleActions(SettingsScreenAction.DeleteAllData) },
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun AccountCard() {
    val shape = RoundedCornerShape(18.dp)
    Row(
        Modifier.fillMaxWidth()
            .clip(shape)
            .background(LessonsColors.PanelBackground)
            .border(1.dp, LessonsColors.PrimaryTintBorder, shape)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                .background(LessonsColors.CardBackground)
                .border(1.dp, LessonsColors.BorderNeutral, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Person,
                null,
                tint = LessonsColors.Primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                "Вы не вошли",
                color = LessonsColors.TextPrimary,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
            Text(
                "Войдите, чтобы синхронизировать данные между устройствами",
                color = LessonsColors.TextMuted,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
        AuthButton(
            label = "Apple ID",
            background = LessonsColors.TextPrimary,
            contentColor = Color.White
        )
        AuthButton(
            label = "Google",
            background = LessonsColors.CardBackground,
            contentColor = LessonsColors.TextBody,
            border = LessonsColors.Border
        )
    }
}

@Composable
private fun AuthButton(
    label: String,
    background: Color,
    contentColor: Color,
    border: Color? = null
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        Modifier
            .height(42.dp)
            .clip(shape)
            .background(background)
            .then(if (border != null) Modifier.border(1.5.dp, border, shape) else Modifier)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = contentColor,
            fontFamily = nunitoFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun GroupCard(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        Modifier.fillMaxWidth()
            .clip(shape)
            .background(LessonsColors.PanelBackground)
            .border(1.dp, LessonsColors.BorderCard, shape),
    ) { content() }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String?,
    showCaret: Boolean,
    showDivider: Boolean,
    titleColor: Color = LessonsColors.TextPrimary,
    iconColor: Color = LessonsColors.TextSecondary,
    caretColor: Color = LessonsColors.Border,
    onClick: (() -> Unit)? = null,
) {
    Row(
        Modifier.fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
        Text(
            title,
            color = titleColor,
            fontFamily = nunitoFamily(),
            fontWeight = if (titleColor == LessonsColors.Danger) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        value?.let {
            Text(
                it,
                color = LessonsColors.TextMuted,
                fontFamily = nunitoFamily(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
        if (showCaret) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                null,
                tint = caretColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
    if (showDivider) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(LessonsColors.BorderSoft))
    }
}

@Composable
private fun ServiceTypeRow(
    currentLabel: String,
    serviceTypes: List<com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType>,
    currentType: com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType,
    onSelect: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        SettingsRow(
            icon = Icons.Filled.School,
            title = "Тип услуг",
            value = currentLabel,
            showCaret = true,
            showDivider = true,
            onClick = { expanded = true },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            serviceTypes.forEachIndexed { index, type ->
                DropdownMenuItem(
                    text = {
                        Text(
                            type.toUIName(),
                            fontFamily = nunitoFamily(),
                            fontWeight = if (type == currentType) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (type == currentType) LessonsColors.Primary else LessonsColors.TextPrimary,
                        )
                    },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    },
                )
            }
        }
    }
}
