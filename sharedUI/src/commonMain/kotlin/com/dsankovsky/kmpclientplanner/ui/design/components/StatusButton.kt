package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_finished
import kmpclientplanner.sharedui.generated.resources.service_not_paid
import kmpclientplanner.sharedui.generated.resources.service_paid
import kmpclientplanner.sharedui.generated.resources.service_planned
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.dp

/**
 * Кнопка-переключатель статуса оплаты: иконка «купюра» + «Оплачено» / «Не оплачено».
 *
 * Заменяет свайп по строке из мобильной версии — на десктопе статусы переключаются
 * прямо в строке ленты и в деталях. Внешний вид **не зависит** от выделения строки.
 */
@Composable
fun PaymentStatusButton(
    isPaid: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = OrganicTheme.typography.bodyXs,
) {
    val colors = OrganicTheme.colors
    OrganicButton(
        text = stringResource(if (isPaid) Res.string.service_paid else Res.string.service_not_paid),
        onClick = onClick,
        modifier = modifier,
        colors = OrganicButtonDefaults.status(if (isPaid) colors.positive else colors.negative),
        icon = OrganicIcons.Banknote,
        enabled = enabled,
        textStyle = textStyle,
    )
}

/** Кнопка-переключатель статуса занятия: иконка «календарь» + «Проведено» / «Запланировано». */
@Composable
fun SessionStatusButton(
    isDone: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = OrganicTheme.typography.bodyXs,
) {
    val colors = OrganicTheme.colors
    OrganicButton(
        text = stringResource(if (isDone) Res.string.service_finished else Res.string.service_planned),
        onClick = onClick,
        modifier = modifier,
        colors = OrganicButtonDefaults.status(if (isDone) colors.positive else colors.negative),
        icon = OrganicIcons.Calendar,
        enabled = enabled,
        textStyle = textStyle,
    )
}

@Preview
@Composable
private fun StatusButtonPreview() {
    PreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PaymentStatusButton(isPaid = true, onClick = {})
            PaymentStatusButton(isPaid = false, onClick = {})
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SessionStatusButton(isDone = true, onClick = {})
            SessionStatusButton(isDone = false, onClick = {})
        }
    }
}
