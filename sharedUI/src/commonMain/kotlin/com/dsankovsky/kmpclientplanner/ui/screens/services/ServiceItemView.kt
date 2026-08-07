package com.dsankovsky.kmpclientplanner.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.PaymentStatusButton
import com.dsankovsky.kmpclientplanner.ui.design.components.PreviewSurface
import com.dsankovsky.kmpclientplanner.ui.design.components.SessionStatusButton
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIMoney
import com.dsankovsky.kmpclientplanner.ui.extensions.toTime
import kotlinx.datetime.LocalDateTime

/**
 * Строка занятия из ленты (экран 04): сетка «время · клиент · сумма · статусы»,
 * radius 26, паддинг 24/28.
 *
 * Контекстного меню по правому клику больше нет — статусы стали кнопками прямо в строке,
 * а удаление живёт в форме услуги.
 *
 * @param dimmed строки следующих дней в макете приглушены, чтобы сегодняшний читался первым
 * @param selected выбранная строка: тень и обводка акцентом
 * @param compact узкое окно: сетка макета не влезает, поэтому статусы уходят на вторую
 *   строку — иначе колонка с именем сжимается в ноль и текст переносится по буквам
 */
@Composable
fun ServiceItemView(
    serviceItem: ServicesListScreenItem.ServiceItem,
    onAction: (ServicesListScreenAction) -> Unit,
    modifier: Modifier = Modifier,
    dimmed: Boolean = false,
    selected: Boolean = false,
    compact: Boolean = false,
) {
    val colors = OrganicTheme.colors
    val shape = OrganicTheme.shapes.row
    val container = modifier
        .fillMaxWidth()
        .alpha(if (dimmed) DimmedAlpha else 1f)
        .then(if (selected) Modifier.dropShadow(shape, OrganicTheme.elevation.md) else Modifier)
        .background(colors.surface, shape)
        .then(if (selected) Modifier.border(2.dp, colors.accent, shape) else Modifier)
        .clickable(
            interactionSource = null,
            indication = null,
            onClick = { onAction(ServicesListScreenAction.OnServiceClicked(serviceItem)) },
        )
        .padding(horizontal = 28.dp, vertical = 24.dp)

    if (compact) {
        Column(
            modifier = container,
            verticalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space3),
        ) {
            // Сжимается только колонка с именем — у неё многоточие; время, сумма и статусы
            // всегда получают свою ширину целиком.
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Time(serviceItem)
                ClientAndService(serviceItem, Modifier.weight(1f))
                Price(serviceItem)
            }
            StatusButtons(serviceItem, onAction)
        }
    } else {
        Row(
            modifier = container,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Time(serviceItem, Modifier.width(TimeColumn))
            ClientAndService(serviceItem, Modifier.weight(1f))
            Price(serviceItem, Modifier.width(PriceColumn))
            StatusButtons(serviceItem, onAction)
        }
    }
}

@Composable
private fun Time(
    serviceItem: ServicesListScreenItem.ServiceItem,
    modifier: Modifier = Modifier,
) {
    OrganicText(
        text = serviceItem.startDate.toTime(),
        style = OrganicTheme.typography.numeric,
        modifier = modifier,
    )
}

@Composable
private fun ClientAndService(
    serviceItem: ServicesListScreenItem.ServiceItem,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        OrganicText(
            text = serviceItem.client.getFullName(),
            style = OrganicTheme.typography.body.copy(fontSize = 18.sp, lineHeight = 24.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        OrganicText(
            text = serviceItem.subtitle(),
            style = OrganicTheme.typography.label,
            color = OrganicTheme.colors.muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun Price(
    serviceItem: ServicesListScreenItem.ServiceItem,
    modifier: Modifier = Modifier,
) {
    OrganicText(
        text = serviceItem.service.price.toUIMoney(serviceItem.service.currency),
        style = OrganicTheme.typography.body.copy(fontSize = 17.sp),
        maxLines = 1,
        softWrap = false,
        modifier = modifier,
    )
}

@Composable
private fun StatusButtons(
    serviceItem: ServicesListScreenItem.ServiceItem,
    onAction: (ServicesListScreenAction) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2)) {
        PaymentStatusButton(
            isPaid = serviceItem.isPaid,
            onClick = { onAction(ServicesListScreenAction.OnPaidStatusChanged(serviceItem)) },
        )
        SessionStatusButton(
            isDone = serviceItem.isFinished,
            onClick = { onAction(ServicesListScreenAction.OnFinishStatusChanged(serviceItem)) },
        )
    }
}

/** «Английский · 60 мин · онлайн» — название услуги, длительность и адрес через точку. */
private fun ServicesListScreenItem.ServiceItem.subtitle(): String = buildList {
    if (title.isNotBlank()) add(title)
    val minutes = startDate.minutesUntil(endDate)
    if (minutes > 0) add("$minutes мин")
    service.address?.takeIf { it.isNotBlank() }?.let(::add)
}.joinToString(" · ")

private fun LocalDateTime.minutesUntil(other: LocalDateTime): Int {
    val start = date.toEpochDays() * MinutesInDay + hour * 60 + minute
    val end = other.date.toEpochDays() * MinutesInDay + other.hour * 60 + other.minute
    return (end - start).toInt()
}

private const val MinutesInDay = 24 * 60
private const val DimmedAlpha = 0.75f
private val TimeColumn = 124.dp
private val PriceColumn = 176.dp

@Preview
@Composable
private fun ServiceItemViewPreview() {
    PreviewSurface(width = 1000.dp) {
        val item = ServicesListScreenItem.ServiceItem(
            title = "Английский",
            client = BaseClient(name = "Анна", surname = "Ковалёва"),
            service = BaseService(price = 40f, address = "онлайн"),
        )
        ServiceItemView(item.copy(isPaid = true, isFinished = true), {})
        ServiceItemView(item, {}, selected = true)
        ServiceItemView(item, {}, dimmed = true)
    }
}
