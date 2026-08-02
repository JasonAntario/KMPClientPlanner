package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.components.Avatar
import com.dsankovsky.kmpclientplanner.ui.design.components.EmptyState
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonDefaults
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicCard
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicDivider
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicField
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicIconButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicProgressBar
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicSelect
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicStepper
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableCell
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableHeader
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableHeaderCell
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTableRow
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTextArea
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTextField
import com.dsankovsky.kmpclientplanner.ui.design.components.PaymentStatusButton
import com.dsankovsky.kmpclientplanner.ui.design.components.PhotoCarousel
import com.dsankovsky.kmpclientplanner.ui.design.components.PhotoCarouselDots
import com.dsankovsky.kmpclientplanner.ui.design.components.SegmentedControl
import com.dsankovsky.kmpclientplanner.ui.design.components.SessionStatusButton
import com.dsankovsky.kmpclientplanner.ui.design.components.Tag
import com.dsankovsky.kmpclientplanner.ui.design.components.TagDefaults
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import org.jetbrains.skia.EncodedImageFormat

/**
 * Каталог атомов: единственный способ посмотреть на компоненты до того, как появятся
 * экраны. PNG остаётся в `sharedUI/build/design/organic-components.png`.
 *
 * Состояния hover/pressed статичным рендером не снять — они проверяются в приложении.
 */
class OrganicComponentsSheetRenderTest {

    @Test
    fun `renders the components sheet`() {
        val scene = ImageComposeScene(width = 1760, height = 2480, density = Density(2f)) {
            OrganicTheme { ComponentsSheet() }
        }
        var image = scene.render()
        repeat(20) {
            Thread.sleep(50)
            image = scene.render()
        }
        scene.close()

        val file = File("build/design/organic-components.png")
        file.parentFile.mkdirs()
        file.writeBytes(
            checkNotNull(image.encodeToData(EncodedImageFormat.PNG)) { "не удалось закодировать PNG" }.bytes,
        )
        assertTrue(file.length() > 0)
    }
}

@Composable
private fun ComponentsSheet() {
    val spacing = OrganicTheme.spacing
    val type = OrganicTheme.typography
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(OrganicTheme.colors.bg)
            .padding(spacing.space6),
        horizontalArrangement = Arrangement.spacedBy(spacing.space6),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.space4),
        ) {
            OrganicText("Кнопки", style = type.h3)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                OrganicButton("Добавить услугу", {}, icon = OrganicIcons.Plus)
                OrganicButton("Отмена", {}, colors = OrganicButtonDefaults.secondary())
                OrganicButton("Удалить", {}, colors = OrganicButtonDefaults.ghost())
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                OrganicButton("Сбросить", {}, colors = OrganicButtonDefaults.destructive())
                OrganicButton("Опасная зона", {}, colors = OrganicButtonDefaults.dangerOutlined())
                OrganicButton("Оплатить", {}, enabled = false)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                OrganicIconButton(OrganicIcons.Pencil, {}, null)
                OrganicIconButton(OrganicIcons.Trash, {}, null)
                OrganicIconButton(OrganicIcons.ChevronLeft, {}, null)
                OrganicIconButton(OrganicIcons.ChevronRight, {}, null)
            }

            OrganicText("Статусы и теги", style = type.h3)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                PaymentStatusButton(isPaid = true, onClick = {})
                PaymentStatusButton(isPaid = false, onClick = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                SessionStatusButton(isDone = true, onClick = {})
                SessionStatusButton(isDone = false, onClick = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                Tag("Проект: рукав", colors = TagDefaults.accent())
                Tag("+5 кг", colors = TagDefaults.accent2())
                Tag("Предоплата 4", colors = TagDefaults.neutral())
                Tag("Онлайн", colors = TagDefaults.outline())
            }

            OrganicText("Поля", style = type.h3)
            OrganicField("Имя клиента") {
                OrganicTextField(value = "Мария Петрова", onValueChange = {})
            }
            OrganicField("Адрес проведения") {
                OrganicSelect(
                    value = "Немига, 12",
                    items = listOf("Немига, 12", "Онлайн"),
                    onSelect = {},
                    itemLabel = { it },
                )
            }
            OrganicField("Стоимость") {
                OrganicTextField(value = "", onValueChange = {}, placeholder = "0,00")
            }
            OrganicField("Домашнее задание") {
                OrganicTextArea(
                    value = "Упражнения 4–7, повторить времена.",
                    onValueChange = {},
                    minHeight = 90.dp,
                    background = OrganicTheme.colors.bg,
                )
            }

            OrganicText("Фильтр периода", style = type.h3)
            SegmentedControl(
                options = listOf("Сегодня", "Завтра", "Неделя", "Месяц"),
                selected = "Неделя",
                onSelect = {},
                optionLabel = { it },
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.space4),
        ) {
            OrganicText("Карточка, аватары, степпер", style = type.h3)
            OrganicCard(kicker = "услуга · 1 ч", title = "Заметка о занятии") {
                OrganicText(
                    "Разобрали Present Perfect, к следующему разу — упражнения 4–7.",
                    style = type.bodySm,
                )
                OrganicDivider()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                ) {
                    Avatar("МП", size = 40.dp)
                    Avatar("ИК", size = 40.dp)
                    Avatar("АС", size = 48.dp)
                }
            }
            OrganicStepper(value = 3, onValueChange = {}, range = 1..8)

            OrganicText("Таблица", style = type.h3)
            Column {
                OrganicTableHeader {
                    OrganicTableHeaderCell("Клиент", Modifier.weight(1.4f))
                    OrganicTableHeaderCell("Оплачено", Modifier.weight(1f))
                    OrganicTableHeaderCell("BYN", Modifier.weight(1f))
                }
                listOf(
                    Triple("Мария Петрова", "12", "1 080,00"),
                    Triple("Игорь Ковалёв", "8", "760,00"),
                ).forEach { (name, count, sum) ->
                    OrganicTableRow {
                        OrganicTableCell(name, Modifier.weight(1.4f))
                        OrganicTableCell(count, Modifier.weight(1f), numeric = true)
                        OrganicTableCell(sum, Modifier.weight(1f), numeric = true)
                    }
                }
            }

            OrganicText("Процент оплаты", style = type.h3)
            OrganicText("76%", style = type.numericLarge, color = OrganicTheme.colors.accentText)
            OrganicProgressBar(progress = 0.76f, modifier = Modifier.width(320.dp))
            OrganicText("26 из 34", style = type.meta, color = OrganicTheme.colors.muted)

            OrganicText("Фотографии", style = type.h3)
            PhotoCarousel(
                models = listOf(null, null, null, null),
                page = 0,
                onPageChange = {},
                modifier = Modifier.width(420.dp),
            )
            PhotoCarouselDots(count = 2, selected = 0)

            Box(Modifier.fillMaxWidth().height(360.dp)) {
                EmptyState(
                    icon = OrganicIcons.UserPlus,
                    title = "Начнём с первого клиента",
                    description = "Добавьте клиента — и занятия можно будет ставить в один клик.",
                    actionText = "Добавить клиента",
                    onAction = {},
                )
            }
        }
    }
}
