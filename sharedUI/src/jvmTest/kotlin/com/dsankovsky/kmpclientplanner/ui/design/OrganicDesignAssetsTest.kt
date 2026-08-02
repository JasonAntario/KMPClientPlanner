package com.dsankovsky.kmpclientplanner.ui.design

import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import java.awt.Font as AwtFont
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Ассеты дизайн-системы ломаются молча: битый SVG-контур даёт пустую иконку,
 * а не загрузившийся шрифт — системный фолбэк. Поэтому проверяем их отдельно.
 */
class OrganicDesignAssetsTest {

    @Test
    fun `every icon parses into a non-empty vector`() {
        val icons = listOf(
            "Plus" to OrganicIcons.Plus,
            "Minus" to OrganicIcons.Minus,
            "Pencil" to OrganicIcons.Pencil,
            "Trash" to OrganicIcons.Trash,
            "Check" to OrganicIcons.Check,
            "X" to OrganicIcons.X,
            "Clock" to OrganicIcons.Clock,
            "Monitor" to OrganicIcons.Monitor,
            "Dumbbell" to OrganicIcons.Dumbbell,
            "PenTool" to OrganicIcons.PenTool,
            "Award" to OrganicIcons.Award,
            "Home" to OrganicIcons.Home,
            "Users" to OrganicIcons.Users,
            "BarChart" to OrganicIcons.BarChart,
            "Settings" to OrganicIcons.Settings,
            "UserPlus" to OrganicIcons.UserPlus,
            "User" to OrganicIcons.User,
            "Calendar" to OrganicIcons.Calendar,
            "CalendarDays" to OrganicIcons.CalendarDays,
            "Banknote" to OrganicIcons.Banknote,
            "Wallet" to OrganicIcons.Wallet,
            "ChevronLeft" to OrganicIcons.ChevronLeft,
            "ChevronRight" to OrganicIcons.ChevronRight,
        )
        assertEquals(23, icons.size, "в макете 23 уникальных глифа")
        icons.forEach { (name, vector) ->
            assertEquals(name, vector.name)
            assertEquals(24f, vector.viewportWidth, "иконки нарисованы на сетке 24×24: $name")
            assertTrue(vector.root.iterator().hasNext(), "пустой контур: $name")
        }
    }

    @Test
    fun `font ships in resources and is valid truetype`() {
        assertTrue(familyNameOf(readFont("nunito_variable.ttf")).startsWith("Nunito"))
    }

    /** Хендофф-гарнитуры кириллицу не покрывали; для русского интерфейса это обязательное свойство. */
    @Test
    fun `font covers cyrillic`() {
        val font = awtFontOf(readFont("nunito_variable.ttf"))
        assertEquals(-1, font.canDisplayUpTo("Занятие оплачено ЁёЙй"))
    }

    /**
     * Все веса — от 400 до Black — берутся из одного вариативного файла осью `wght`.
     * Дефолтный инстанс у него ExtraLight, так что без оси текст поедет в тонкий.
     */
    @Test
    fun `font carries a variable weight axis`() {
        assertTrue("fvar" in tableTagsOf(readFont("nunito_variable.ttf")))
    }

    private fun readFont(fileName: String): ByteArray {
        val path = "composeResources/kmpclientplanner.sharedui.generated.resources/font/$fileName"
        val stream = checkNotNull(javaClass.classLoader.getResourceAsStream(path)) {
            "шрифт не попал в ресурсы: $path"
        }
        return stream.use { it.readBytes() }
    }

    private fun familyNameOf(bytes: ByteArray): String = awtFontOf(bytes).family

    private fun awtFontOf(bytes: ByteArray): AwtFont =
        bytes.inputStream().use { AwtFont.createFont(AwtFont.TRUETYPE_FONT, it) }

    /** Каталог таблиц TrueType: 4-байтные теги начиная с 12-го байта, по 16 байт на запись. */
    private fun tableTagsOf(bytes: ByteArray): List<String> {
        val count = ((bytes[4].toInt() and 0xFF) shl 8) or (bytes[5].toInt() and 0xFF)
        return (0 until count).map { i ->
            val offset = 12 + i * 16
            bytes.decodeToString(offset, offset + 4)
        }
    }
}
