package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.components.EmptyState
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationBar
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationBarItem
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationDefaults
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationRail
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationRailItem
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Каркас навигации в двух раскладках: десктопное окно с рейлом (экран 02 макета)
 * и compact-окно с нижней панелью. PNG — `sharedUI/build/design/organic-navigation.png`.
 */
class OrganicNavigationRenderTest {

    @Test
    fun `renders both navigation layouts`() {
        val scene = ImageComposeScene(
            width = (SheetWidth + CompactWidth) * 2,
            height = SheetHeight * 2,
            density = Density(2f),
        ) {
            OrganicTheme { NavigationSheet() }
        }
        // Шрифт грузится асинхронно — первый кадр рисуется системным фолбэком.
        var image = scene.render()
        repeat(20) {
            Thread.sleep(50)
            image = scene.render()
        }
        scene.close()

        val file = File("build/design/organic-navigation.png")
        file.parentFile.mkdirs()
        file.writeBytes(
            checkNotNull(image.encodeToData(EncodedImageFormat.PNG)) { "не удалось закодировать PNG" }.bytes,
        )
        assertTrue(file.length() > 0)

        val bitmap = requireNotNull(org.jetbrains.skia.Bitmap.makeFromImage(image))
        val colors = OrganicColors()
        // Полоса рейла — сплошной surface во всю высоту окна.
        assertEquals(
            colors.surface.toArgb(),
            bitmap.getColor(8, SheetHeight),
            "рейл должен быть залит --color-surface",
        )
        // Правее рейла — кремовая земля контента.
        assertEquals(
            colors.bg.toArgb(),
            bitmap.getColor(
                OrganicNavigationDefaults.RailWidth.value.toInt() * 2 + 40,
                SheetHeight
            ),
            "контент за рейлом должен лежать на --color-bg",
        )
    }
}

private const val SheetWidth = 1360
private const val SheetHeight = 660
private const val CompactWidth = 420

@Composable
private fun NavigationSheet() {
    Row(Modifier.fillMaxSize().background(OrganicTheme.colors.bg)) {
        DesktopFrame(Modifier.width(SheetWidth.dp).fillMaxHeight())
        CompactFrame(Modifier.width(CompactWidth.dp).fillMaxHeight())
    }
}

/** Экран 02 макета: рейл 248 + пустое состояние. */
@Composable
private fun DesktopFrame(modifier: Modifier) {
    Row(modifier.background(OrganicTheme.colors.bg)) {
        OrganicNavigationRail(brand = "Куфар Блокнот", footer = "Репетитор · v1.0.0") {
            OrganicButton("Добавить", {}, icon = OrganicIcons.Plus, fillMaxWidth = true)
            OrganicNavigationRailItem(OrganicIcons.Home, "Главная", selected = false, onClick = {})
            OrganicNavigationRailItem(OrganicIcons.Users, "Клиенты", selected = true, onClick = {})
            OrganicNavigationRailItem(
                OrganicIcons.BarChart,
                "Статистика",
                selected = false,
                onClick = {})
            OrganicNavigationRailItem(
                OrganicIcons.Settings,
                "Настройки",
                selected = false,
                onClick = {})
        }
        EmptyState(
            icon = OrganicIcons.UserPlus,
            title = "Начнём с первого клиента",
            description = "Пока нет ни клиентов, ни занятий. Добавьте клиента — сразу можно " +
                    "указать день и время первого занятия.",
            actionText = "Добавить клиента",
            onAction = {},
        )
    }
}

/** Та же навигация в compact-окне: нижняя панель вместо рейла. */
@Composable
private fun CompactFrame(modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(start = OrganicTheme.spacing.space6)
            .background(OrganicTheme.colors.bg),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Box(Modifier.fillMaxWidth().height(120.dp))
        OrganicNavigationBar {
            OrganicNavigationBarItem(OrganicIcons.Home, "Главная", selected = true, onClick = {})
            OrganicNavigationBarItem(OrganicIcons.Users, "Клиенты", selected = false, onClick = {})
            OrganicNavigationBarItem(
                OrganicIcons.BarChart,
                "Статистика",
                selected = false,
                onClick = {})
            OrganicNavigationBarItem(
                OrganicIcons.Settings,
                "Настройки",
                selected = false,
                onClick = {})
        }
    }
}
