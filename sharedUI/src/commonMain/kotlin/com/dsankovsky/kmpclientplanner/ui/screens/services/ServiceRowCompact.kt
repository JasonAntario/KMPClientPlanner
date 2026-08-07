package com.dsankovsky.kmpclientplanner.ui.screens.services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonColors
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicClickableSurface
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.PaddingValuesOf
import com.dsankovsky.kmpclientplanner.ui.design.components.PreviewSurface
import com.dsankovsky.kmpclientplanner.ui.design.components.Tag
import com.dsankovsky.kmpclientplanner.ui.design.components.TagDefaults
import com.dsankovsky.kmpclientplanner.ui.design.elevationMd
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.extensions.toTime
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_finished
import kmpclientplanner.sharedui.generated.resources.service_not_paid
import kmpclientplanner.sharedui.generated.resources.service_paid
import kmpclientplanner.sharedui.generated.resources.service_planned
import org.jetbrains.compose.resources.stringResource

/** Ширина списка занятий в деталях (экраны 05–07). */
val ServicesListPaneWidth = 400.dp

/**
 * Компактная строка занятия для списка рядом с деталями: время 22, имя 16 и статусы
 * тегами, без суммы. Выбранная поднимается на `surface` с `shadow-md` и обводкой accent.
 *
 * Это не та же строка, что в ленте на весь экран ([ServiceItemView]): там статусы —
 * кнопки-переключатели, здесь только метки, а переключать статус можно в деталях справа.
 */
@Composable
fun ServiceRowCompact(
    serviceItem: ServicesListScreenItem.ServiceItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OrganicTheme.colors
    val shape = OrganicTheme.shapes.rowLarge
    OrganicClickableSurface(
        onClick = onClick,
        colors = OrganicButtonColors(
            container = if (selected) colors.surface else Color.Transparent,
            content = colors.text,
            border = if (selected) colors.accent else Color.Unspecified,
            hoverContainer = if (selected) colors.surface else colors.hoverSubtle,
            pressedContainer = if (selected) colors.surface else colors.hover,
        ),
        shape = shape,
        borderWidth = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .then(if (selected) Modifier.elevationMd(shape, OrganicTheme.elevation) else Modifier),
        contentPadding = PaddingValuesOf(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OrganicText(
                text = serviceItem.startDate.toTime(),
                style = OrganicTheme.typography.numeric.copy(fontSize = 22.sp, lineHeight = 26.sp),
                modifier = Modifier.width(TimeColumnWidth),
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OrganicText(
                    text = serviceItem.client.getFullName(),
                    style = OrganicTheme.typography.body.copy(fontSize = 16.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Tag(
                        text = stringResource(
                            if (serviceItem.isPaid) Res.string.service_paid
                            else Res.string.service_not_paid,
                        ),
                        colors = if (serviceItem.isPaid) TagDefaults.accent2() else TagDefaults.accent(),
                        icon = OrganicIcons.Banknote,
                    )
                    Tag(
                        text = stringResource(
                            if (serviceItem.isFinished) Res.string.service_finished
                            else Res.string.service_planned,
                        ),
                        colors = if (serviceItem.isFinished) TagDefaults.accent2() else TagDefaults.accent(),
                        icon = OrganicIcons.Calendar,
                    )
                }
            }
        }
    }
}

private val TimeColumnWidth = 70.dp

@Preview
@Composable
private fun ServiceRowCompactPreview() {
    PreviewSurface(width = ServicesListPaneWidth) {
        val item = ServicesListScreenItem.ServiceItem(
            title = "Английский",
            client = BaseClient(name = "Анна", surname = "Ковалёва"),
            service = BaseService(price = 40f),
        )
        ServiceRowCompact(item.copy(isPaid = true, isFinished = true), selected = false, onClick = {})
        ServiceRowCompact(item.copy(isFinished = true), selected = true, onClick = {})
        ServiceRowCompact(item, selected = false, onClick = {})
    }
}
