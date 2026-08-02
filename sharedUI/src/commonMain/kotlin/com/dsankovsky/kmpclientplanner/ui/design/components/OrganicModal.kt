package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.cancel
import kmpclientplanner.sharedui.generated.resources.modal_close
import org.jetbrains.compose.resources.stringResource

/** Ширины панелей из макета: формы М1/М3 — 600, средние М5–М7/М11 — 520, короткие М8–М10 — 460. */
object OrganicModalWidth {
    val Form: Dp = 600.dp
    val Medium: Dp = 520.dp
    val Small: Dp = 460.dp
}

/** Скруглённая плашка внутри модалки (М5–М7) — единственные 22 в системе. */
private val PanelShape = RoundedCornerShape(22.dp)

/**
 * Слой модального окна: скрим `neutral-900 @50%` на весь экран, панель по центру.
 *
 * Это не `androidx.compose.ui.window.Dialog`: нужен точный скрим и одинаковое поведение
 * на всех таргетах, а платформенное окно на десктопе даёт свою рамку и свой скрим.
 *
 * Закрытие — Esc и клик по скриму; сама панель клики не пропускает.
 */
@Composable
fun OrganicModalHost(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val scrimInteraction = remember { MutableInteractionSource() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            // Превью-обработчик: Esc работает и когда фокус внутри поля формы.
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.Escape) {
                    onDismissRequest()
                    true
                } else {
                    false
                }
            }
            .background(OrganicTheme.colors.scrim)
            .clickable(
                interactionSource = scrimInteraction,
                indication = null,
                onClick = onDismissRequest,
            )
            .padding(OrganicTheme.spacing.space4),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

/**
 * `.dialog` — панель модального окна: `surface`, radius 32, `shadow-lg`, паддинг 17.6, gap 13.2.
 *
 * @param fullScreen для compact-окна: панель растягивается вместо фиксированной ширины
 * @param contentPadding 0 для «переходных» модалок, внутрь которых кладётся ещё не
 *   переписанный экран со своим `Scaffold`
 */
@Composable
fun OrganicModal(
    modifier: Modifier = Modifier,
    width: Dp = OrganicModalWidth.Small,
    fullScreen: Boolean = false,
    contentPadding: Dp = OrganicTheme.spacing.space4,
    verticalGap: Dp = OrganicTheme.spacing.space3,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = OrganicTheme.shapes.card
    Column(
        modifier = modifier
            .then(if (fullScreen) Modifier.fillMaxSize() else Modifier.width(width))
            // Клик по панели не должен доходить до скрима и закрывать окно.
            .pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } }
            .dropShadow(shape, OrganicTheme.elevation.lg)
            .background(OrganicTheme.colors.surface, shape)
            // Клип по форме: содержимое (в переходных модалках — целый экран со своим фоном)
            // не должно вылезать за скругление.
            .clip(shape)
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(verticalGap),
        content = content,
    )
}

/** Шапка модалки: заголовок 20, необязательное пояснение 12 и кнопка-крестик. */
@Composable
fun OrganicModalHeader(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            OrganicText(title, style = OrganicTheme.typography.h4)
            if (subtitle != null) {
                OrganicText(
                    text = subtitle,
                    style = OrganicTheme.typography.label,
                    color = OrganicTheme.colors.muted,
                )
            }
        }
        OrganicIconButton(
            icon = OrganicIcons.X,
            onClick = onClose,
            contentDescription = stringResource(Res.string.modal_close),
            iconSize = 17.dp,
        )
    }
}

/**
 * `.dialog-actions` — кнопки внизу справа.
 *
 * @param destructive деструктивное действие («Удалить услугу») — оно уходит влево,
 *   чтобы его нельзя было нажать по инерции вместо «Сохранить»
 */
@Composable
fun OrganicModalActions(
    modifier: Modifier = Modifier,
    destructive: (@Composable () -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(top = OrganicTheme.spacing.space2),
        horizontalArrangement = if (destructive != null) Arrangement.SpaceBetween else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        destructive?.invoke()
        Row(
            horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

/**
 * Плашка на кремовом фоне внутри модалки: список занятий к оплате (М5), свободные слоты (М6),
 * будущие занятия автозаполнения (М7).
 */
@Composable
fun OrganicModalPanel(
    modifier: Modifier = Modifier,
    kicker: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(OrganicTheme.colors.bg, PanelShape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2),
    ) {
        if (kicker != null) {
            OrganicText(
                text = kicker.uppercase(),
                style = OrganicTheme.typography.tableHeader,
                color = OrganicTheme.colors.muted,
            )
        }
        content()
    }
}

/**
 * Короткая модалка «вопрос — два действия»: М8 (удаление), М9 (несохранённые изменения),
 * М10 (сброс). Отличаются только текстами и тем, деструктивно ли подтверждение.
 */
@Composable
fun ConfirmModal(
    title: String,
    text: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String = stringResource(Res.string.cancel),
    destructive: Boolean = false,
    confirmEnabled: Boolean = true,
    fullScreen: Boolean = false,
    extraContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    OrganicModal(modifier = modifier, width = OrganicModalWidth.Small, fullScreen = fullScreen) {
        OrganicText(title, style = OrganicTheme.typography.h4)
        // `.dialog-body` — 14 с прозрачностью 0.85; берём тот же вес через muted-производную.
        OrganicText(text, style = OrganicTheme.typography.bodySm, color = OrganicTheme.colors.label)
        extraContent?.invoke(this)
        OrganicModalActions {
            OrganicButton(dismissText, onDismiss, colors = OrganicButtonDefaults.secondary())
            OrganicButton(
                text = confirmText,
                onClick = onConfirm,
                colors = if (destructive) {
                    OrganicButtonDefaults.destructive()
                } else {
                    OrganicButtonDefaults.primary()
                },
                enabled = confirmEnabled,
            )
        }
    }
}

@Preview
@Composable
private fun ConfirmModalPreview() {
    PreviewSurface(width = 560.dp) {
        Box(Modifier.background(OrganicTheme.colors.scrim).padding(OrganicTheme.spacing.space4)) {
            ConfirmModal(
                title = "Удалить клиента?",
                text = "Анна Ковалёва и все её занятия (12) будут удалены. " +
                    "Оплаты пропадут из статистики. Действие необратимо.",
                confirmText = "Удалить",
                onConfirm = {},
                onDismiss = {},
                destructive = true,
            )
        }
    }
}

@Preview
@Composable
private fun OrganicModalPreview() {
    PreviewSurface(width = 620.dp) {
        Box(Modifier.background(OrganicTheme.colors.scrim).padding(OrganicTheme.spacing.space4)) {
            OrganicModal(width = OrganicModalWidth.Medium) {
                OrganicModalHeader(
                    title = "Предоплата",
                    subtitle = "Выбираются самые ранние занятия со статусом «Не оплачено»",
                    onClose = {},
                )
                OrganicField(label = "Клиент") {
                    OrganicTextField(value = "Дмитрий Лис — 4 неоплаченных занятия", onValueChange = {})
                }
                OrganicModalPanel(kicker = "Будут отмечены оплаченными") {
                    listOf("20 июля, 19:30" to "60,00 BYN", "22 июля, 19:30" to "60,00 BYN").forEach {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            OrganicText(it.first, style = OrganicTheme.typography.bodySm)
                            OrganicText(it.second, style = OrganicTheme.typography.bodySm)
                        }
                    }
                }
                OrganicModalActions {
                    OrganicButton("Отмена", {}, colors = OrganicButtonDefaults.secondary())
                    OrganicButton("Оплатить", {})
                }
            }
        }
    }
}
