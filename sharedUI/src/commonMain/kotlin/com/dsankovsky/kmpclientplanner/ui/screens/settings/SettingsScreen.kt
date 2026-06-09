package com.dsankovsky.kmpclientplanner.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.components.CardView
import com.dsankovsky.kmpclientplanner.ui.components.DropDownMenuView
import com.dsankovsky.kmpclientplanner.ui.components.HeaderView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.feedback_url
import kmpclientplanner.sharedui.generated.resources.settings_clear_date
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
            .fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            top = 24.dp,
            bottom = 16.dp,
            start = 16.dp,
            end = 16.dp
        ).withNavBarPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            HeaderView(stringResource(Res.string.settings_title))
        }

        item {
            val serviceTypeNames = screenState.serviceTypeList.associateWith { it.toUIName() }
            DropDownMenuView(
                currentItem = screenState.serviceType,
                items = screenState.serviceTypeList,
                transformItemToText = { serviceTypeNames[it] ?: it.name },
                label = stringResource(Res.string.settings_service_type),
                onItemSelected = { serviceType ->
                    onAction(SettingsScreenAction.OnServiceTypeSelected(serviceType.ordinal))
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SettingsItem(
                label = stringResource(Res.string.settings_version),
                interactionContent = {
                    Text(
                        text = screenState.version,
                    )
                },
                onClick = {}
            )
        }

        item {
            val uriHandler = LocalUriHandler.current
            val url = stringResource(Res.string.feedback_url)
            SettingsItem(
                label = stringResource(Res.string.settings_feedback),
                interactionContent = {},
                icon = Icons.Outlined.RateReview,
                onClick = { uriHandler.openUri(url) }
            )
        }

        item {
            SettingsItem(
                modifier = Modifier.padding(top = 24.dp),
                label = stringResource(Res.string.settings_clear_date),
                icon = Icons.Default.DeleteForever,
                iconColor = MaterialTheme.colorScheme.error,
                interactionContent = {},
                onClick = {
                    onAction(SettingsScreenAction.DeleteAllData)
                }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    label: String,
    interactionContent: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    CardView(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    tint = iconColor,
                    contentDescription = null
                )
            }

            Text(
                label,
                modifier = Modifier.weight(1f)
            )
            interactionContent()
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