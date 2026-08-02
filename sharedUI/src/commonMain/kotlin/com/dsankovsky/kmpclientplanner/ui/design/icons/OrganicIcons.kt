package com.dsankovsky.kmpclientplanner.ui.design.icons

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme

/**
 * Иконки Organic — ровно тот набор, что нарисован инлайн-SVG в макете
 * (`design-handoff/Куфар Блокнот - Desktop MVP.dc.html`): 23 глифа в стиле Lucide
 * на сетке 24×24 со stroke-width 2.75 и скруглёнными концами.
 *
 * Контуры перенесены из макета один в один, поэтому их можно сверять с ним построчно.
 * Если экрану понадобится глиф, которого в макете нет (поиск, телефон, Telegram),
 * он рисуется здесь же в той же манере.
 */
object OrganicIcons {

    /** «Добавить», плюс в кнопках и категория «Другое». */
    val Plus: ImageVector by lazy { icon("Plus", "M12 5v14M5 12h14") }

    /** Степпер — уменьшить. */
    val Minus: ImageVector by lazy { icon("Minus", "M5 12h14") }

    /** Редактирование (icon-кнопка). */
    val Pencil: ImageVector by lazy { icon("Pencil", "M4 20h4l12-12-4-4L4 16v4Z") }

    /** Удаление. */
    val Trash: ImageVector by lazy { icon("Trash", "M4 7h16M9 7V4.5h6V7M6.5 7l1 13h9l1-13") }

    /** «Оплачено» / подтверждение. */
    val Check: ImageVector by lazy { icon("Check", "M4 12.5 9.5 18 20 6.5") }

    /** Закрыть модальное окно. */
    val X: ImageVector by lazy { icon("X", "M6 6l12 12M18 6L6 18") }

    /** «Проведено», время. */
    val Clock: ImageVector by lazy {
        icon("Clock", circle(12f, 12f, 8.5f), "M12 7.5V12l3.5 2")
    }

    /** Категория «Репетитор». */
    val Monitor: ImageVector by lazy {
        icon("Monitor", "M3 6.5h18v11H3z", "M8 20h8")
    }

    /** Категория «Тренер». */
    val Dumbbell: ImageVector by lazy {
        icon("Dumbbell", "M4 12h16", "M6.5 8.5v7M17.5 8.5v7")
    }

    /** Категория «Тату-мастер». */
    val PenTool: ImageVector by lazy {
        icon("PenTool", "M6 20 18 8l-2-2L4 18v2h2Z", "M14 4l6 6")
    }

    /** Категория «Бьюти-мастер». */
    val Award: ImageVector by lazy {
        icon("Award", circle(12f, 9f, 5f), "M9 14l-2 6 5-2.5L17 20l-2-6")
    }

    /** Рейл — «Главная». */
    val Home: ImageVector by lazy {
        icon("Home", "M3 10.5 12 3l9 7.5", "M5 9.5V20h14V9.5")
    }

    /** Рейл — «Клиенты». */
    val Users: ImageVector by lazy {
        icon(
            "Users",
            circle(9f, 8f, 3.2f),
            "M3 20c0-3.3 2.7-5 6-5s6 1.7 6 5",
            "M16 5.5a3.2 3.2 0 0 1 0 5",
        )
    }

    /** Рейл — «Статистика». */
    val BarChart: ImageVector by lazy {
        icon("BarChart", "M4 20V10", "M10 20V4", "M16 20v-7")
    }

    /** Рейл — «Настройки». */
    val Settings: ImageVector by lazy {
        icon(
            "Settings",
            circle(12f, 12f, 3f),
            "M12 3v2.5M12 18.5V21M3 12h2.5M18.5 12H21" +
                "M5.6 5.6l1.8 1.8M16.6 16.6l1.8 1.8M18.4 5.6l-1.8 1.8M7.4 16.6l-1.8 1.8",
        )
    }

    /** Пустое состояние «Начнём с первого клиента». */
    val UserPlus: ImageVector by lazy {
        icon(
            "UserPlus",
            circle(9f, 8f, 3.2f),
            "M3 20c0-3.3 2.7-5 6-5s6 1.7 6 5",
            "M16 5.5a3.2 3.2 0 0 1 0 5",
            "M19 18h4M21 16v4",
        )
    }

    /** Одиночный человек: аватар-заглушка, «Вы не вошли». */
    val User: ImageVector by lazy {
        icon("User", circle(12f, 8f, 3.4f), "M5 20c0-3.6 3.1-5.5 7-5.5s7 1.9 7 5.5")
    }

    /** Статус занятия. */
    val Calendar: ImageVector by lazy {
        icon("Calendar", "M3.5 5.5h17v15h-17z", "M3.5 10h17M8 3v4M16 3v4")
    }

    /** Пустое состояние «Занятий пока нет». */
    val CalendarDays: ImageVector by lazy {
        icon("CalendarDays", "M3 6.5h18v14H3z", "M3 11h18M8 3.5v4M16 3.5v4")
    }

    /** Статус оплаты. */
    val Banknote: ImageVector by lazy {
        icon("Banknote", "M2.5 7.5h19v9h-19z", circle(12f, 12f, 2.6f))
    }

    /** «Предоплатить». */
    val Wallet: ImageVector by lazy {
        icon("Wallet", "M3.5 8.5h17v11h-17z", "M3.5 8.5 17 5v3.5", circle(16.5f, 14f, 1.3f))
    }

    /** Карусель фотографий — назад. */
    val ChevronLeft: ImageVector by lazy { icon("ChevronLeft", "M15 5l-7 7 7 7") }

    /** Карусель фотографий — вперёд. */
    val ChevronRight: ImageVector by lazy { icon("ChevronRight", "M9 5l7 7-7 7") }
}

/** Размеры иконок из макета: 16 в строках, 18 в кнопках и рейле, 24 в крупных блоках. */
object OrganicIconSize {
    val Small: Dp = 16.dp
    val Medium: Dp = 18.dp
    val Large: Dp = 24.dp
}

/**
 * Отрисовка иконки. Своя, а не `material3.Icon`: нужен размер из макета и тонирование
 * текущим content-цветом Organic.
 */
@Composable
fun OrganicIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = OrganicIconSize.Medium,
    tint: Color = OrganicTheme.colors.text,
) {
    Image(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(tint),
    )
}

private const val StrokeWidth = 2.75f
private const val ViewportSize = 24f

/**
 * Собирает иконку из SVG-контуров макета. Строки парсятся один раз на глиф
 * (значения ленивые), зато остаются дословной копией разметки хендоффа —
 * ручной перевод в `PathBuilder` было бы не с чем сверять.
 */
private fun icon(name: String, vararg pathData: String): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = ViewportSize.dp,
        defaultHeight = ViewportSize.dp,
        viewportWidth = ViewportSize,
        viewportHeight = ViewportSize,
    ).apply {
        pathData.forEach { data ->
            addPath(
                pathData = PathParser().parsePathString(data).toNodes(),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = StrokeWidth,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()

/** `<circle>` из макета в виде пути: две полуокружности. */
private fun circle(cx: Float, cy: Float, r: Float): String =
    "M${cx - r} $cy a$r $r 0 1 0 ${r * 2} 0 a$r $r 0 1 0 ${-r * 2} 0"
