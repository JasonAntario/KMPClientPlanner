package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcon
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons

/**
 * Размеры навигации из макета (экраны 02–09): рейл 248 с паддингом 24/16,
 * пункт — иконка 18 + текст 14 в pill'е с паддингом 11/14.
 */
object OrganicNavigationDefaults {
    val RailWidth: Dp = 248.dp
    val RailVerticalPadding: Dp = 24.dp
    val RailHorizontalPadding: Dp = 16.dp
    val ItemGap: Dp = 8.dp
    val IconSize: Dp = 18.dp

    /** Нижняя панель — только для compact-окна, в макете её нет. */
    val BarHeight: Dp = 60.dp
}

/**
 * Боковая навигация (`<nav>` в макете): полоса `surface` шириной 248 с брендом сверху,
 * пунктами-pill'ами и подписью «<категория> · <версия>», прижатой к низу.
 *
 * Своя, а не `NavigationRail` / `WideNavigationRail` / `NavigationSuiteScaffold`: ни у одной
 * из них не параметризуются ширина, слот футера и pill-заливка активного пункта во всю ширину.
 *
 * @param brand подпись бренда в шапке (`app_name`)
 * @param footer нижняя строка; `null` — футера нет и пункты не прижимаются к верху
 */
@Composable
fun OrganicNavigationRail(
    brand: String,
    modifier: Modifier = Modifier,
    footer: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .width(OrganicNavigationDefaults.RailWidth)
            .fillMaxHeight()
            .background(OrganicTheme.colors.surface)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start),
            )
            .padding(
                vertical = OrganicNavigationDefaults.RailVerticalPadding,
                horizontal = OrganicNavigationDefaults.RailHorizontalPadding,
            ),
        verticalArrangement = Arrangement.spacedBy(OrganicNavigationDefaults.ItemGap),
    ) {
        OrganicText(
            text = brand,
            style = OrganicTheme.typography.brand,
            maxLines = 1,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 6.dp),
        )
        content()
        if (footer != null) {
            Spacer(Modifier.weight(1f))
            OrganicText(
                text = footer,
                style = OrganicTheme.typography.meta,
                color = OrganicTheme.colors.muted,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 14.dp),
            )
        }
    }
}

/** Пункт рейла: pill во всю ширину, активный — заливка accent и контент цветом земли. */
@Composable
fun OrganicNavigationRailItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = navigationItemColors(selected)
    OrganicClickableSurface(
        onClick = onClick,
        colors = colors,
        shape = OrganicTheme.shapes.pill,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValuesOf(horizontal = 14.dp, vertical = 11.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrganicIcon(
                imageVector = icon,
                contentDescription = null,
                size = OrganicNavigationDefaults.IconSize,
                tint = colors.content,
            )
            OrganicText(
                text = label,
                style = OrganicTheme.typography.bodySm,
                color = colors.content,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

/**
 * Нижняя навигация для compact-окна. В макете её нет (десктопный приоритет — рейл),
 * поэтому она собрана из тех же токенов: `surface`, hairline сверху, те же pill-пункты.
 */
@Composable
fun OrganicNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(OrganicTheme.colors.surface)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        OrganicDivider(color = OrganicTheme.colors.rowLine)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(OrganicNavigationDefaults.BarHeight)
                .padding(horizontal = OrganicTheme.spacing.space2),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

/** Пункт нижней навигации: иконка над подписью, активный — тот же pill accent. */
@Composable
fun RowScope.OrganicNavigationBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = navigationItemColors(selected)
    OrganicClickableSurface(
        onClick = onClick,
        colors = colors,
        shape = OrganicTheme.shapes.pill,
        modifier = modifier.weight(1f),
        contentPadding = PaddingValuesOf(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            OrganicIcon(
                imageVector = icon,
                contentDescription = null,
                size = OrganicNavigationDefaults.IconSize,
                tint = colors.content,
            )
            OrganicText(
                text = label,
                style = OrganicTheme.typography.meta,
                color = colors.content,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

/** Активный пункт — как `.btn-primary`, неактивный — как `.btn-secondary` без бордера. */
@Composable
private fun navigationItemColors(selected: Boolean): OrganicButtonColors =
    with(OrganicTheme.colors) {
        if (selected) {
            OrganicButtonColors(
                container = accent,
                content = bg,
                hoverContainer = accentRamp.s600,
                pressedContainer = accentRamp.s700,
            )
        } else {
            OrganicButtonColors(
                container = Color.Transparent,
                content = text,
                hoverContainer = hover,
                pressedContainer = pressed,
            )
        }
    }

@Preview
@Composable
private fun OrganicNavigationRailPreview() {
    PreviewSurface(width = 280.dp) {
        Column(Modifier.height(420.dp)) {
            OrganicNavigationRail(brand = "Куфар Блокнот", footer = "Репетитор · v1.0.0") {
                OrganicNavigationRailItem(OrganicIcons.Home, "Главная", selected = false, onClick = {})
                OrganicNavigationRailItem(OrganicIcons.Users, "Клиенты", selected = true, onClick = {})
                OrganicNavigationRailItem(OrganicIcons.BarChart, "Статистика", selected = false, onClick = {})
                OrganicNavigationRailItem(OrganicIcons.Settings, "Настройки", selected = false, onClick = {})
            }
        }
    }
}

@Preview
@Composable
private fun OrganicNavigationBarPreview() {
    PreviewSurface(width = 400.dp) {
        OrganicNavigationBar {
            OrganicNavigationBarItem(OrganicIcons.Home, "Главная", selected = true, onClick = {})
            OrganicNavigationBarItem(OrganicIcons.Users, "Клиенты", selected = false, onClick = {})
            OrganicNavigationBarItem(OrganicIcons.BarChart, "Статистика", selected = false, onClick = {})
            OrganicNavigationBarItem(OrganicIcons.Settings, "Настройки", selected = false, onClick = {})
        }
    }
}
