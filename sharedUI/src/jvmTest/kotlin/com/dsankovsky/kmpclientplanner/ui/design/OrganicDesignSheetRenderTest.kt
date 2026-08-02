package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcon
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import org.jetbrains.skia.EncodedImageFormat

/**
 * Офскрин-рендер листа с токенами: единственный способ увидеть, что шрифты реально
 * подхватились, иконки нарисовались, а тени легли — до того, как появятся экраны.
 *
 * PNG остаётся в `sharedUI/build/design/organic-tokens.png` как справочный лист.
 */
class OrganicDesignSheetRenderTest {

    @Test
    fun `renders the token sheet`() {
        val scene = ImageComposeScene(
            width = SheetWidth,
            height = SheetHeight,
            density = Density(2f),
        ) {
            OrganicTheme { TokenSheet() }
        }
        // Ресурсы (шрифты) грузятся асинхронно — первый кадр рисуется системным фолбэком.
        var image = scene.render()
        repeat(20) {
            Thread.sleep(50)
            image = scene.render()
        }
        scene.close()

        val file = File("build/design/organic-tokens.png")
        file.parentFile.mkdirs()
        file.writeBytes(
            checkNotNull(image.encodeToData(EncodedImageFormat.PNG)) { "не удалось закодировать PNG" }
                .bytes,
        )

        val bitmap = requireNotNull(org.jetbrains.skia.Bitmap.makeFromImage(image))
        assertEquals(
            Color(0xFFF5EAD8).toArgb(),
            bitmap.getColor(4, 4),
            "фон листа должен быть --color-bg",
        )
    }
}

private const val SheetWidth = 1200
private const val SheetHeight = 1320

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TokenSheet() {
    val colors = OrganicTheme.colors
    val spacing = OrganicTheme.spacing
    val type = OrganicTheme.typography
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(spacing.space6),
        verticalArrangement = Arrangement.spacedBy(spacing.space4),
    ) {
        BasicText("Nunito Black · заголовок H2", style = type.h2)
        BasicText("Nunito Regular · основной текст 15", style = type.body)
        BasicText(
            "Nunito SemiBold · 14",
            style = type.bodySm.copy(fontWeight = FontWeight.SemiBold),
        )
        BasicText("Nunito Bold · 14", style = type.bodySm.copy(fontWeight = FontWeight.Bold))
        BasicText("1 234,00 BYN · 09:30 (tnum)", style = type.numeric)

        Ramp(listOf(colors.bg, colors.surface, colors.accent, colors.accent2, colors.text))
        Ramp(
            with(colors.accentRamp) {
                listOf(s100, s200, s300, s400, s500, s600, s700, s800, s900)
            },
        )
        Ramp(
            with(colors.accent2Ramp) {
                listOf(s100, s200, s300, s400, s500, s600, s700, s800, s900)
            },
        )
        Ramp(
            with(colors.neutralRamp) {
                listOf(s100, s200, s300, s400, s500, s600, s700, s800, s900)
            },
        )

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.space6)) {
            ShadowSample("sm", OrganicTheme.shapes.pill, OrganicTheme.elevation.sm)
            ShadowSample("md", OrganicTheme.shapes.card, OrganicTheme.elevation.md)
            ShadowSample("lg", OrganicTheme.shapes.card, OrganicTheme.elevation.lg)
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.space3),
            verticalArrangement = Arrangement.spacedBy(spacing.space3),
        ) {
            listOf(
                OrganicIcons.Plus, OrganicIcons.Minus, OrganicIcons.Pencil, OrganicIcons.Trash,
                OrganicIcons.Check, OrganicIcons.X, OrganicIcons.Clock, OrganicIcons.Monitor,
                OrganicIcons.Dumbbell, OrganicIcons.PenTool, OrganicIcons.Award, OrganicIcons.Home,
                OrganicIcons.Users, OrganicIcons.BarChart, OrganicIcons.Settings,
                OrganicIcons.UserPlus, OrganicIcons.User, OrganicIcons.Calendar,
                OrganicIcons.CalendarDays, OrganicIcons.Banknote, OrganicIcons.Wallet,
                OrganicIcons.ChevronLeft, OrganicIcons.ChevronRight,
            ).forEach { icon ->
                OrganicIcon(icon, contentDescription = null, size = 24.dp, tint = colors.accentText)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.space3)) {
            StatusChip("Оплачено", colors.positive, type.tag)
            StatusChip("Не оплачено", colors.negative, type.tag)
            StatusChip("Удалить", colors.destructive, type.tag)
        }
    }
}

@Composable
private fun Ramp(steps: List<Color>) {
    Row(horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space1)) {
        steps.forEach { step ->
            Box(
                Modifier
                    .size(44.dp, 26.dp)
                    .background(step, OrganicTheme.shapes.sm),
            )
        }
    }
}

@Composable
private fun ShadowSample(label: String, shape: Shape, shadow: Shadow) {
    Box(
        modifier = Modifier
            .dropShadow(shape, shadow)
            .width(150.dp)
            .height(58.dp)
            .background(OrganicTheme.colors.surface, shape),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        BasicText("shadow-$label", style = OrganicTheme.typography.bodySm)
    }
}

@Composable
private fun StatusChip(label: String, status: StatusColors, style: TextStyle) {
    Box(
        modifier = Modifier
            .background(status.fill, OrganicTheme.shapes.pill)
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        BasicText(label, style = style.copy(color = status.content))
    }
}
