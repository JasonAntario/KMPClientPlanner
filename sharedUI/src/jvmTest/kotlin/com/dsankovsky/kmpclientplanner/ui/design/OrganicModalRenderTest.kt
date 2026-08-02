package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.components.ConfirmModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonDefaults
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicField
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalActions
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHeader
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHost
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalPanel
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalWidth
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicStepper
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTextField
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Модальный слой: М5 (предоплата) и М10 (сброс) поверх кремовой земли.
 * PNG — `sharedUI/build/design/organic-modals.png`.
 */
class OrganicModalRenderTest {

    @Test
    fun `renders the modal layer`() {
        val scene = ImageComposeScene(
            width = SheetWidth * 2,
            height = SheetHeight * 2,
            density = Density(2f)
        ) {
            OrganicTheme { ModalSheet() }
        }
        var image = scene.render()
        repeat(20) {
            Thread.sleep(50)
            image = scene.render()
        }
        scene.close()

        val file = File("build/design/organic-modals.png")
        file.parentFile.mkdirs()
        file.writeBytes(
            checkNotNull(image.encodeToData(EncodedImageFormat.PNG)) { "не удалось закодировать PNG" }.bytes,
        )
        assertTrue(file.length() > 0)

        // Угол кадра занят скримом, а не кремовой землёй: слой действительно перекрывает экран.
        val bitmap = requireNotNull(org.jetbrains.skia.Bitmap.makeFromImage(image))
        val colors = OrganicColors()
        val expected = colors.scrim.compositeOverOpaque(colors.bg).toArgb()
        val actual = bitmap.getColor(8, 8)
        // Сравниваем поканально с допуском 1: скрим полупрозрачный, и сведение в sRGB округляет.
        listOf(16, 8, 0).forEach { shift ->
            val diff = ((expected shr shift) and 0xFF) - ((actual shr shift) and 0xFF)
            assertTrue(
                diff in -1..1,
                "модалку должен закрывать скрим: канал $shift разошёлся на $diff",
            )
        }
        assertEquals(0xFF, (actual shr 24) and 0xFF, "кадр должен быть непрозрачным")
    }
}

private const val SheetWidth = 1200
private const val SheetHeight = 720

@Composable
private fun ModalSheet() {
    Row(Modifier.fillMaxSize().background(OrganicTheme.colors.bg)) {
        Box(Modifier.width(680.dp).fillMaxHeight()) {
            OrganicModalHost(onDismissRequest = {}) { PrepayModal() }
        }
        Box(Modifier.width((SheetWidth - 680).dp).fillMaxHeight()) {
            OrganicModalHost(onDismissRequest = {}) { ResetModal() }
        }
    }
}

/** М5 — предоплата, 520. */
@Composable
private fun PrepayModal() {
    OrganicModal(width = OrganicModalWidth.Medium) {
        OrganicModalHeader(title = "Предоплата", onClose = {})
        OrganicField(label = "Клиент") {
            OrganicTextField(
                value = "Дмитрий Лис — 4 неоплаченных занятия",
                onValueChange = {},
                readOnly = true,
            )
        }
        OrganicField(label = "Количество оплаченных занятий") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space3),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                OrganicStepper(value = 3, onValueChange = {}, range = 1..4)
                OrganicText(
                    "от 1 до 4 — по числу неоплаченных занятий клиента",
                    style = OrganicTheme.typography.label,
                    color = OrganicTheme.colors.muted,
                )
            }
        }
        OrganicModalPanel(kicker = "Будут отмечены оплаченными") {
            listOf(
                "20 июля, 19:30" to "60,00 BYN",
                "22 июля, 19:30" to "60,00 BYN",
                "27 июля, 19:30" to "60,00 BYN",
            ).forEach { (date, sum) ->
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    OrganicText(date, style = OrganicTheme.typography.bodySm)
                    OrganicText(sum, style = OrganicTheme.typography.bodySm)
                }
            }
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                OrganicText("Итого", style = OrganicTheme.typography.cardTitle)
                OrganicText("180,00 BYN", style = OrganicTheme.typography.cardTitle)
            }
        }
        OrganicModalActions {
            OrganicButton("Отмена", {}, colors = OrganicButtonDefaults.secondary())
            OrganicButton("Оплатить", {})
        }
    }
}

/** М10 — сброс приложения, 460, с контрольным словом. */
@Composable
private fun ResetModal() {
    ConfirmModal(
        title = "Сбросить приложение?",
        text = "Будут удалены все клиенты, занятия и вся статистика оплат. " +
                "Восстановить данные будет невозможно.",
        confirmText = "Удалить всё",
        onConfirm = {},
        onDismiss = {},
        destructive = true,
        confirmEnabled = false,
        extraContent = {
            OrganicField(label = "Введите СБРОС для подтверждения") {
                OrganicTextField(value = "", onValueChange = {}, placeholder = "СБРОС")
            }
        },
    )
}

/** Скрим полупрозрачен — цвет под ним нужно смешать вручную, `getColor` отдаёт уже сведённый. */
private fun androidx.compose.ui.graphics.Color.compositeOverOpaque(
    background: androidx.compose.ui.graphics.Color,
): androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(
    red = red * alpha + background.red * (1 - alpha),
    green = green * alpha + background.green * (1 - alpha),
    blue = blue * alpha + background.blue * (1 - alpha),
    alpha = 1f,
)
