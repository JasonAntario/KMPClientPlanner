package com.dsankovsky.kmpclientplanner.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * `.washed` — фильтр, которым Organic «утапливает» фотографии в тёплый фон:
 * `saturate(0.6) contrast(0.85) brightness(1.1)`. Прозрачность 0.94 задаётся отдельно
 * параметром `alpha`, матрицей её не выразить.
 */
val WashedColorFilter: ColorFilter = ColorFilter.colorMatrix(
    ColorMatrix().apply {
        setToSaturation(WashedSaturation)
        // c' = ((c − 0.5) · contrast + 0.5) · brightness
        val scale = WashedContrast * WashedBrightness
        val offset = (0.5f - 0.5f * WashedContrast) * WashedBrightness * 255f
        this *= ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, offset,
                0f, scale, 0f, 0f, offset,
                0f, 0f, scale, 0f, offset,
                0f, 0f, 0f, 1f, 0f,
            ),
        )
    },
)

private const val WashedSaturation = 0.6f
private const val WashedContrast = 0.85f
private const val WashedBrightness = 1.1f
private const val WashedAlpha = 0.94f

/**
 * Плитка фотографии: 3:4, radius 20, изображение проходит через [WashedColorFilter].
 * Пока файла нет — плейсхолдер neutral-300 с иконкой, как в макете.
 */
@Composable
fun PhotoTile(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 3f / 4f,
    shape: Shape = OrganicTheme.shapes.photo,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colors = OrganicTheme.colors
    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(shape)
            .background(colors.neutralRamp.s300)
            .then(if (selected) Modifier.border(2.dp, colors.accent, shape) else Modifier)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        // Пустой слот — просто серая плитка neutral-300, как в макете.
        if (model != null) {
            AsyncImage(
                model = model,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = WashedColorFilter,
                alpha = WashedAlpha,
            )
        }
    }
}

/**
 * Карусель референсов с экрана 07: icon-кнопки ‹ ›, плитки в ряд и точки-индикаторы.
 * Листание — постраничное, по одной плитке; страницу держит вызывающий.
 */
@Composable
fun PhotoCarousel(
    models: List<Any?>,
    page: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    visibleCount: Int = 3,
    onPhotoClick: ((Int) -> Unit)? = null,
) {
    val spacing = OrganicTheme.spacing
    val pages = if (models.isEmpty()) 0 else (models.size + visibleCount - 1) / visibleCount
    Box(modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrganicIconButton(
                icon = OrganicIcons.ChevronLeft,
                onClick = { onPageChange(page - 1) },
                contentDescription = null,
                enabled = page > 0,
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing.space3),
            ) {
                val from = page * visibleCount
                repeat(visibleCount) { offset ->
                    val index = from + offset
                    PhotoTile(
                        model = models.getOrNull(index),
                        contentDescription = null,
                        modifier = Modifier.weight(1f),
                        onClick = if (index in models.indices && onPhotoClick != null) {
                            { onPhotoClick(index) }
                        } else {
                            null
                        },
                    )
                }
            }
            OrganicIconButton(
                icon = OrganicIcons.ChevronRight,
                onClick = { onPageChange(page + 1) },
                contentDescription = null,
                enabled = page < pages - 1,
            )
        }
    }
}

/** Точки-индикаторы под каруселью. */
@Composable
fun PhotoCarouselDots(
    count: Int,
    selected: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 6.dp,
) {
    val colors = OrganicTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            Box(
                Modifier
                    .padding(vertical = 4.dp)
                    .size(dotSize)
                    .background(
                        if (index == selected) colors.accent else colors.neutralRamp.s400,
                        CircleShape,
                    ),
            )
        }
    }
}

/** Миниатюра 56×56 в ленте просмотрщика фото (М12): активная — обводка accent. */
@Composable
fun PhotoThumbnail(
    model: Any?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PhotoTile(
        model = model,
        contentDescription = null,
        modifier = modifier.size(56.dp),
        aspectRatio = 1f,
        shape = OrganicTheme.shapes.sm,
        selected = selected,
        onClick = onClick,
    )
}

@Preview
@Composable
private fun PhotoPreview() {
    PreviewSurface(width = 520.dp) {
        var page by remember { mutableStateOf(0) }
        PhotoCarousel(
            models = List(5) { null },
            page = page,
            onPageChange = { page = it },
        )
        PhotoCarouselDots(count = 2, selected = page)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PhotoThumbnail(model = null, selected = true, onClick = {})
            PhotoThumbnail(model = null, selected = false, onClick = {})
            PhotoThumbnail(model = null, selected = false, onClick = {})
        }
    }
}
