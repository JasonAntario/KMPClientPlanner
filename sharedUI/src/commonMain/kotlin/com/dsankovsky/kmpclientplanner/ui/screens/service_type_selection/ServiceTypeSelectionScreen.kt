package com.dsankovsky.kmpclientplanner.ui.screens.service_type_selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicCard
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.elevationSm
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcon
import com.dsankovsky.kmpclientplanner.ui.extensions.ServiceTypeOrder
import com.dsankovsky.kmpclientplanner.ui.extensions.organicIcon
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIHint
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_type_selection_auth_hint
import kmpclientplanner.sharedui.generated.resources.service_type_selection_continue
import kmpclientplanner.sharedui.generated.resources.service_type_selection_description
import kmpclientplanner.sharedui.generated.resources.service_type_selection_logo
import kmpclientplanner.sharedui.generated.resources.service_type_selection_title
import org.jetbrains.compose.resources.stringResource

/**
 * Экран 01 — приветствие и выбор категории.
 *
 * Отдельного приветственного экрана в новом дизайне нет: логотип, вопрос
 * «Чем вы занимаетесь?» и пять карточек живут на одном экране. Категория выбирается
 * кликом по карточке, а применяется кнопкой «Продолжить» — раньше клик по строке сразу
 * записывал тип и уводил дальше, передумать было нечем.
 */
@Composable
fun ServiceTypeSelectionScreen(
    onServiceTypeClicked: (ServiceType) -> Unit,
    modifier: Modifier = Modifier,
    initialSelection: ServiceType? = null,
) {
    var selected by remember { mutableStateOf(initialSelection) }
    val spacing = OrganicTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrganicTheme.colors.bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 120.dp, vertical = spacing.space8),
        verticalArrangement = Arrangement.spacedBy(spacing.space8, Alignment.CenterVertically),
    ) {
        Column(
            modifier = Modifier.widthIn(max = 640.dp),
            verticalArrangement = Arrangement.spacedBy(spacing.space2),
        ) {
            Box(
                Modifier.size(64.dp).background(OrganicTheme.colors.accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                OrganicText(
                    text = stringResource(Res.string.service_type_selection_logo),
                    style = OrganicTheme.typography.h3.copy(fontSize = 28.sp, lineHeight = 32.sp),
                    color = OrganicTheme.colors.bg,
                )
            }
            OrganicText(
                text = stringResource(Res.string.service_type_selection_title),
                style = OrganicTheme.typography.h1.copy(fontSize = 48.sp, lineHeight = 54.sp),
                modifier = Modifier.padding(top = spacing.space1),
            )
            OrganicText(
                text = stringResource(Res.string.service_type_selection_description),
                style = OrganicTheme.typography.body.copy(fontSize = 17.sp, lineHeight = 26.sp),
                color = OrganicTheme.colors.muted,
            )
        }

        ServiceTypeCards(
            selected = selected,
            onSelect = { selected = it },
            modifier = Modifier.widthIn(max = 1120.dp),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrganicButton(
                text = stringResource(Res.string.service_type_selection_continue),
                onClick = { selected?.let(onServiceTypeClicked) },
                enabled = selected != null,
                textStyle = OrganicTheme.typography.button.copy(fontSize = 16.sp),
            )
            OrganicText(
                text = stringResource(Res.string.service_type_selection_auth_hint),
                style = OrganicTheme.typography.bodyXs,
                color = OrganicTheme.colors.muted,
            )
        }
    }
}

/**
 * Сетка категорий. В макете это пять равных колонок; на узком окне карточки переносятся,
 * а не сжимаются в нечитаемые полоски.
 *
 * Ряды считаются руками через [BoxWithConstraints], а не отдаются `FlowRow`: у него
 * `Modifier.height(IntrinsicSize.Max)` с растянутыми по `weight` детьми давал нулевую
 * высоту, и карточки просто исчезали (тем вернее, чем шире окно). У `Row` intrinsic-высота
 * считается корректно, а равные по высоте карточки в ряду нужны — иначе «Репетитор»
 * с длинной подписью выпирает, а остальные висят обрезками.
 */
@Composable
private fun ServiceTypeCards(
    selected: ServiceType?,
    onSelect: (ServiceType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrganicTheme.spacing
    BoxWithConstraints(modifier) {
        val columns = ((maxWidth + spacing.space4) / (MinCardWidth + spacing.space4))
            .toInt()
            .coerceIn(1, ServiceTypeOrder.size)

        Column(verticalArrangement = Arrangement.spacedBy(spacing.space4)) {
            ServiceTypeOrder.chunked(columns).forEach { rowTypes ->
                Row(
                    modifier = Modifier.height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space4),
                ) {
                    rowTypes.forEach { type ->
                        ServiceTypeCard(
                            type = type,
                            selected = type == selected,
                            onClick = { onSelect(type) },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                    // Неполный последний ряд: пустые слоты держат ширину колонок,
                    // чтобы карточки не растягивались шире остальных.
                    repeat(columns - rowTypes.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/** Уже этого карточка категории становится нечитаемой — дальше сетка переносится. */
private val MinCardWidth = 180.dp

@Composable
private fun ServiceTypeCard(
    type: ServiceType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OrganicTheme.colors
    // Базовая категория в макете нейтральная — она ничего не добавляет к услуге.
    val iconCircle = when {
        selected -> colors.accent
        type == ServiceType.BASE -> colors.neutralRamp.s300
        else -> colors.accent2Ramp.s200
    }
    val iconTint = when {
        selected -> colors.bg
        type == ServiceType.BASE -> colors.neutralRamp.s800
        else -> colors.accent2Ramp.s800
    }
    OrganicCard(
        modifier = modifier
            .elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation)
            // Без ripple: в Organic состояния — тонирование, а выбор виден обводкой.
            .clickable(interactionSource = null, indication = null, onClick = onClick),
        background = if (selected) colors.accentRamp.s100 else colors.surface,
        border = if (selected) colors.accent else Color.Unspecified,
        borderWidth = 2.dp,
        verticalGap = OrganicTheme.spacing.space3,
    ) {
        Box(
            Modifier.size(48.dp).background(iconCircle, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            OrganicIcon(type.organicIcon, contentDescription = null, size = 22.dp, tint = iconTint)
        }
        OrganicText(type.toUIName(), style = OrganicTheme.typography.cardTitle)
        OrganicText(
            text = type.toUIHint(),
            style = OrganicTheme.typography.bodyXs,
            color = OrganicTheme.colors.muted,
        )
    }
}

@Preview
@Composable
private fun ServiceTypeCardPreview() {
    OrganicTheme {
        Row(
            modifier = Modifier
                .background(OrganicTheme.colors.bg)
                .padding(OrganicTheme.spacing.space4),
            horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space4),
        ) {
            ServiceTypeCard(ServiceType.EDUCATION, selected = true, onClick = {}, modifier = Modifier.width(200.dp))
            ServiceTypeCard(ServiceType.SPORT, selected = false, onClick = {}, modifier = Modifier.width(200.dp))
            ServiceTypeCard(ServiceType.BASE, selected = false, onClick = {}, modifier = Modifier.width(200.dp))
        }
    }
}
