package com.dsankovsky.kmpclientplanner.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.feedback_url
import kmpclientplanner.sharedui.generated.resources.settings_feedback
import kmpclientplanner.sharedui.generated.resources.settings_service_type
import kmpclientplanner.sharedui.generated.resources.settings_title
import kmpclientplanner.sharedui.generated.resources.settings_version
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onEvent: (SettingsScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.collectWithLifecycle {
        onEvent(it)
    }

    LaunchedEffect(Unit) {
        viewModel.handleActions(SettingsScreenAction.LoadData)
    }

    SettingsScreenContent(
        screenState = state,
        onAction = viewModel::handleActions,
        modifier = modifier
    )
}

@Composable
fun SettingsScreenContent(
    screenState: SettingsScreenState,
    onAction: (SettingsScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            top = 24.dp,
            bottom = 16.dp,
            start = 16.dp,
            end = 16.dp
        ).withNavBarPadding(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = stringResource(Res.string.settings_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            SettingsListContainer {
                ServiceTypeRow(screenState = screenState, onAction = onAction)
                RowDivider()
                ThemeRow()
                RowDivider()
                VersionRow(version = screenState.version)
                RowDivider()
                FeedbackRow()
            }
        }

        item {
            DangerZone(onClearAll = { onAction(SettingsScreenAction.DeleteAllData) })
        }
    }
}

@Composable
private fun SettingsListContainer(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        content()
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        trailing()
    }
}

@Composable
private fun ServiceTypeRow(
    screenState: SettingsScreenState,
    onAction: (SettingsScreenAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        SettingsRow(
            icon = Icons.Rounded.School,
            title = stringResource(Res.string.settings_service_type),
            subtitle = screenState.serviceType.toUIName(),
            onClick = { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            screenState.serviceTypeList.forEach { serviceType ->
                DropdownMenuItem(
                    text = { Text(serviceType.toUIName()) },
                    onClick = {
                        onAction(SettingsScreenAction.OnServiceTypeSelected(serviceType.ordinal))
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeRow() {
    SettingsRow(
        icon = Icons.Rounded.Palette,
        title = "Тема оформления",
        subtitle = "Как в системе",
        onClick = {}
    )
}

@Composable
private fun VersionRow(version: String) {
    SettingsRow(
        icon = Icons.Rounded.Info,
        title = stringResource(Res.string.settings_version),
        subtitle = null,
        trailing = {
            Text(
                text = version,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
private fun FeedbackRow() {
    val uriHandler = LocalUriHandler.current
    val url = stringResource(Res.string.feedback_url)
    SettingsRow(
        icon = Icons.Rounded.RateReview,
        title = stringResource(Res.string.settings_feedback),
        subtitle = null,
        onClick = { uriHandler.openUri(url) }
    )
}

@Composable
private fun DangerZone(onClearAll: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.DeleteForever,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Сбросить приложение",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
            )
        }
        Text(
            text = "Удалит всех клиентов, занятия и платежи без возможности восстановления.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(2.dp))
        OutlinedButton(
            onClick = onClearAll,
            shape = RoundedCornerShape(14.dp),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.DeleteForever,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.size(8.dp))
            Text("Сбросить")
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSettingScreen() {
    ClientPlannerTheme {
        SettingsScreenContent(screenState = SettingsScreenState(), {})
    }
}
