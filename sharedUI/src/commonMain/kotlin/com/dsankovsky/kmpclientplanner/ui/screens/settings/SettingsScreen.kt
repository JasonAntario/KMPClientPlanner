package com.dsankovsky.kmpclientplanner.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.AppInfo
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonDefaults
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicCard
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicDivider
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicScreenHeader
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicSelect
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.elevationSm
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcon
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.feedback_url
import kmpclientplanner.sharedui.generated.resources.settings_app
import kmpclientplanner.sharedui.generated.resources.settings_category
import kmpclientplanner.sharedui.generated.resources.settings_danger_zone
import kmpclientplanner.sharedui.generated.resources.settings_feedback
import kmpclientplanner.sharedui.generated.resources.settings_feedback_action
import kmpclientplanner.sharedui.generated.resources.settings_local_only
import kmpclientplanner.sharedui.generated.resources.settings_not_signed_in
import kmpclientplanner.sharedui.generated.resources.settings_profile
import kmpclientplanner.sharedui.generated.resources.settings_reset_action
import kmpclientplanner.sharedui.generated.resources.settings_reset_row
import kmpclientplanner.sharedui.generated.resources.settings_reset_row_description
import kmpclientplanner.sharedui.generated.resources.settings_sign_in
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

/**
 * Экран 10 — настройки. Колонка максимум 820: «Профиль» (пока заглушка входа),
 * «Приложение» и «Опасная зона» на accent-100.
 */
@Composable
fun SettingsScreenContent(
    screenState: SettingsScreenState,
    onAction: (SettingsScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrganicTheme.colors.bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp, vertical = 32.dp),
    ) {
        // Колонка настроек в макете не шире 820 — иначе строки «подпись … значение»
        // разъезжаются на всю ширину окна.
        Column(
            modifier = Modifier.widthIn(max = 820.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            OrganicScreenHeader(title = stringResource(Res.string.settings_title))

            ProfileCard()
            AppCard(screenState, onAction)
            DangerZoneCard(onAction)
        }
    }
}

@Composable
private fun ProfileCard() {
    val colors = OrganicTheme.colors
    OrganicCard(
        modifier = Modifier.elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
        kicker = stringResource(Res.string.settings_profile),
        verticalGap = 14.dp,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(48.dp).background(colors.neutralRamp.s300, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                OrganicIcon(
                    OrganicIcons.User,
                    contentDescription = null,
                    size = 22.dp,
                    tint = colors.neutralRamp.s700,
                )
            }
            Column(Modifier.weight(1f)) {
                OrganicText(stringResource(Res.string.settings_not_signed_in))
                OrganicText(
                    text = stringResource(Res.string.settings_local_only),
                    style = OrganicTheme.typography.label,
                    color = colors.muted,
                )
            }
            // Вход появится в следующей итерации — кнопка есть в макете, но нажимать нечего.
            OrganicButton(stringResource(Res.string.settings_sign_in), {}, enabled = false)
        }
    }
}

@Composable
private fun AppCard(
    screenState: SettingsScreenState,
    onAction: (SettingsScreenAction) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val feedbackUrl = stringResource(Res.string.feedback_url)
    // Подписи категорий берутся из ресурсов, а `OrganicSelect.itemLabel` — обычная лямбда.
    val serviceTypeNames = screenState.serviceTypeList.associateWith { it.toUIName() }

    OrganicCard(
        modifier = Modifier.elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
        kicker = stringResource(Res.string.settings_app),
        verticalGap = 14.dp,
    ) {
        SettingsRow(label = stringResource(Res.string.settings_category)) {
            OrganicSelect(
                value = screenState.serviceType,
                items = screenState.serviceTypeList,
                onSelect = { onAction(SettingsScreenAction.OnServiceTypeSelected(it.ordinal)) },
                itemLabel = { serviceTypeNames[it].orEmpty() },
                // Ширина фиксирована: селект тянется на всю доступную и иначе
                // сжимает подпись строки до одной буквы в строке.
                modifier = Modifier.width(260.dp),
            )
        }
        OrganicDivider()
        SettingsRow(label = stringResource(Res.string.settings_version)) {
            OrganicText(AppInfo.VERSION, color = OrganicTheme.colors.muted)
        }
        OrganicDivider()
        SettingsRow(label = stringResource(Res.string.settings_feedback)) {
            OrganicButton(
                text = stringResource(Res.string.settings_feedback_action),
                onClick = { uriHandler.openUri(feedbackUrl) },
                colors = OrganicButtonDefaults.secondary(),
            )
        }
    }
}

@Composable
private fun DangerZoneCard(onAction: (SettingsScreenAction) -> Unit) {
    OrganicCard(
        modifier = Modifier.elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
        kicker = stringResource(Res.string.settings_danger_zone),
        background = OrganicTheme.colors.accentRamp.s100,
        verticalGap = OrganicTheme.spacing.space3,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                OrganicText(stringResource(Res.string.settings_reset_row))
                OrganicText(
                    text = stringResource(Res.string.settings_reset_row_description),
                    style = OrganicTheme.typography.label,
                    color = OrganicTheme.colors.muted,
                )
            }
            OrganicButton(
                text = stringResource(Res.string.settings_reset_action),
                onClick = { onAction(SettingsScreenAction.OnResetClicked) },
                colors = OrganicButtonDefaults.dangerOutlined(),
            )
        }
    }
}

/**
 * Строка «подпись — контрол». На узком окне контрол уходит под подпись: иначе селект
 * категории (260) и кнопки выдавливают текст до переноса по буквам.
 */
@Composable
private fun SettingsRow(label: String, control: @Composable () -> Unit) {
    BoxWithConstraints {
        if (maxWidth >= SettingsRowMinWidth) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OrganicText(label, modifier = Modifier.weight(1f))
                control()
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2)) {
                OrganicText(label)
                control()
            }
        }
    }
}

private val SettingsRowMinWidth = 480.dp

@Preview
@Composable
private fun SettingsScreenContentPreview() {
    OrganicTheme {
        SettingsScreenContent(screenState = SettingsScreenState(), onAction = {})
    }
}
