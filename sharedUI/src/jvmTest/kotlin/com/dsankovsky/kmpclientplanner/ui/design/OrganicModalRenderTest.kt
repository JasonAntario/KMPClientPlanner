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
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicField
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalActions
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHost
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTextField
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.ServiceFormModalContent
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.ClientFormModalContent
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServiceScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PrepayClient
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PrepayModalContent
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
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

private const val SheetWidth = 2500
private const val SheetHeight = 1000

@Composable
private fun ModalSheet() {
    Row(Modifier.fillMaxSize().background(OrganicTheme.colors.bg)) {
        Box(Modifier.width(700.dp).fillMaxHeight()) {
            OrganicModalHost(onDismissRequest = {}) { ServiceForm() }
        }
        Box(Modifier.width(700.dp).fillMaxHeight()) {
            OrganicModalHost(onDismissRequest = {}) { ClientForm() }
        }
        Box(Modifier.width(620.dp).fillMaxHeight()) {
            OrganicModalHost(onDismissRequest = {}) { PrepayModal() }
        }
        Box(Modifier.width((SheetWidth - 700 - 700 - 620).dp).fillMaxHeight()) {
            OrganicModalHost(onDismissRequest = {}) { ResetModal() }
        }
    }
}

/** М1 — форма услуги, 600. */
@Composable
private fun ServiceForm() {
    val client = BaseClient(id = 1, name = "Анна", surname = "Ковалёва")
    ServiceFormModalContent(
        state = AddEditServiceScreenState(
            isLoading = false,
            isEdit = true,
            title = "Английский язык",
            client = client,
            clientsList = listOf(client),
            startDateTime = LocalDateTime(2026, 7, 29, 15, 0),
            endDateTime = LocalDateTime(2026, 7, 29, 16, 0),
            durationText = "60",
            address = "Онлайн, Zoom",
            addressList = listOf("Онлайн, Zoom", "Немига 12"),
            price = "40",
            comment = "",
            serviceType = ServiceType.EDUCATION,
            serviceSpecificFields = ServiceSpecificFields.EducationServiceSpecificFields(),
        ),
        onAction = {},
    )
}

/** М3 — форма клиента, 600: реальная форма на заполненном состоянии. */
@Composable
private fun ClientForm() {
    ClientFormModalContent(
        state = AddEditClientScreenState(
            isLoading = false,
            isEdit = true,
            name = "Анна",
            surname = "Ковалёва",
            phone = "+375 29 123-45-67",
            address = "Онлайн, Zoom",
            price = "40",
            comment = "Готовится к экзамену в декабре. Домашние задания просит присылать в Telegram.",
            serviceType = ServiceType.EDUCATION,
            clientSpecificFields = ClientSpecificFields.EducationClientSpecificFields(
                level = "B1",
                lessonDateTimeList = listOf(
                    ServiceDateTime(dayOfWeek = DayOfWeek.MONDAY, time = LocalTime(12, 0)),
                    ServiceDateTime(dayOfWeek = DayOfWeek.WEDNESDAY, time = LocalTime(12, 0)),
                ),
            ),
        ),
        onAction = {},
    )
}

/** М5 — предоплата: реальная модалка на подготовленном состоянии. */
@Composable
private fun PrepayModal() {
    val client = BaseClient(id = 1, name = "Дмитрий", surname = "Лис")
    // Даты разные: в кадре должно быть видно, что плашка перечисляет конкретные занятия.
    val start = LocalDateTime(2026, 7, 20, 19, 30)
    val unpaid = listOf(0, 2, 7, 9).mapIndexed { index, dayShift ->
        val date = start.date.plus(dayShift, DateTimeUnit.DAY)
        BaseService(
            id = index + 1L,
            clientId = 1,
            startDate = LocalDateTime(date, start.time),
            endDate = LocalDateTime(date, start.time),
            price = 60f,
            currency = CurrencyItem.BYN,
        )
    }
    PrepayModalContent(
        state = PayServiceScreenState(
            isLoading = false,
            clients = listOf(PrepayClient(client, unpaid.size)),
            selectedClientId = client.id,
            amount = 3,
            unpaidServices = unpaid,
        ),
        onAction = {},
    )
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
