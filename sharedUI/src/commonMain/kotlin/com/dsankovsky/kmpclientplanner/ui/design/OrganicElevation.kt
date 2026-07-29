package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/**
 * Тени Organic — прямой перенос `--shadow-sm/md/lg` из `styles.css`
 * (`0 1px 2px #2e2b25@14%`, `0 3px 10px @16%`, `0 12px 32px @22%`).
 *
 * Используем `Modifier.dropShadow` (стабильный в CMP 1.11), а не `Modifier.shadow`:
 * тень рисуется снаружи геометрии по CSS-модели, без M3-elevation и без тонирования
 * поверхности.
 *
 * Радиус блюра CSS и Compose совпадают не идеально — числа взяты из макета
 * и при сверке с референсом подбираются здесь, в одном месте.
 */
@Immutable
data class OrganicElevation(
    val sm: Shadow = Shadow(
        radius = 2.dp,
        color = ShadowInk,
        offset = DpOffset(0.dp, 1.dp),
        alpha = 0.14f,
    ),
    val md: Shadow = Shadow(
        radius = 10.dp,
        color = ShadowInk,
        offset = DpOffset(0.dp, 3.dp),
        alpha = 0.16f,
    ),
    val lg: Shadow = Shadow(
        radius = 32.dp,
        color = ShadowInk,
        offset = DpOffset(0.dp, 12.dp),
        alpha = 0.22f,
    ),
)

/** `#2e2b25` — `--color-neutral-900`, к которому привязаны все тени. */
private val ShadowInk = Color(0xFF2E2B25)

/** `.elev-sm` */
fun Modifier.elevationSm(shape: Shape, elevation: OrganicElevation): Modifier =
    dropShadow(shape, elevation.sm)

/** `.elev-md` */
fun Modifier.elevationMd(shape: Shape, elevation: OrganicElevation): Modifier =
    dropShadow(shape, elevation.md)

/** `.elev-lg` — модальные окна. */
fun Modifier.elevationLg(shape: Shape, elevation: OrganicElevation): Modifier =
    dropShadow(shape, elevation.lg)
