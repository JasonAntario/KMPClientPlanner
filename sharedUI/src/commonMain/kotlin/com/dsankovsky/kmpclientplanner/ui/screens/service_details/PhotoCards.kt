package com.dsankovsky.kmpclientplanner.ui.screens.service_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonDefaults
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicCard
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicIconButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.PhotoCarousel
import com.dsankovsky.kmpclientplanner.ui.design.components.PhotoCarouselDots
import com.dsankovsky.kmpclientplanner.ui.design.components.PhotoTile
import com.dsankovsky.kmpclientplanner.ui.design.components.PreviewSurface
import com.dsankovsky.kmpclientplanner.ui.design.elevationSm
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_note_short
import kmpclientplanner.sharedui.generated.resources.service_photo_add
import kmpclientplanner.sharedui.generated.resources.service_photo_delete
import kmpclientplanner.sharedui.generated.resources.service_references
import kmpclientplanner.sharedui.generated.resources.service_references_empty
import kmpclientplanner.sharedui.generated.resources.service_references_note
import kmpclientplanner.sharedui.generated.resources.service_results
import org.jetbrains.compose.resources.stringResource

/**
 * Экран 07 — «Референсы и эскизы» и «Результат работы».
 *
 * Референсы приходят из активного проекта клиента и здесь только показываются;
 * результат — фотографии самой услуги, их можно удалять.
 *
 * @param onAddResult `null` — кнопка «Добавить фото» выключена: выбора файлов в проекте
 *   пока нет (см. §7 плана миграции)
 */
@Composable
internal fun PhotoCards(
    references: List<String>,
    results: List<String>,
    note: String?,
    onDeleteResult: (Int) -> Unit,
    onAddResult: (() -> Unit)? = null,
) {
    BoxWithConstraints {
        // В макете карточки стоят в ряд; на узкой панели — друг под другом.
        val inRow = maxWidth >= PhotoCardsRowMinWidth
        val referencesCard: @Composable (Modifier) -> Unit = { cardModifier ->
            ReferencesCard(references, cardModifier)
        }
        val resultsCard: @Composable (Modifier) -> Unit = { cardModifier ->
            ResultsCard(results, note, onDeleteResult, onAddResult, cardModifier)
        }

        if (inRow) {
            // В макете обе карточки `flex:1` — тянутся до высоты соседа.
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space3),
            ) {
                referencesCard(Modifier.weight(1f).fillMaxHeight())
                resultsCard(Modifier.weight(1f).fillMaxHeight())
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space3)) {
                referencesCard(Modifier.fillMaxWidth())
                resultsCard(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ReferencesCard(references: List<String>, modifier: Modifier) {
    var page by remember { mutableStateOf(0) }
    val pages = if (references.isEmpty()) 0 else (references.size + 2) / VisiblePhotos

    OrganicCard(
        modifier = modifier.elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
        kicker = stringResource(Res.string.service_references),
        verticalGap = 14.dp,
    ) {
        if (references.isEmpty()) {
            OrganicText(
                text = stringResource(Res.string.service_references_empty),
                style = OrganicTheme.typography.bodySm,
                color = OrganicTheme.colors.muted,
            )
        } else {
            PhotoCarousel(
                models = references,
                page = page.coerceIn(0, (pages - 1).coerceAtLeast(0)),
                onPageChange = { page = it.coerceIn(0, (pages - 1).coerceAtLeast(0)) },
                visibleCount = VisiblePhotos,
            )
            if (pages > 1) {
                PhotoCarouselDots(
                    count = pages,
                    selected = page,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
        OrganicText(
            text = stringResource(Res.string.service_references_note),
            style = OrganicTheme.typography.label,
            color = OrganicTheme.colors.muted,
        )
    }
}

@Composable
private fun ResultsCard(
    results: List<String>,
    note: String?,
    onDelete: (Int) -> Unit,
    onAdd: (() -> Unit)?,
    modifier: Modifier,
) {
    OrganicCard(
        modifier = modifier.elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
        verticalGap = 14.dp,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OrganicText(
                text = stringResource(Res.string.service_results, results.size).uppercase(),
                style = OrganicTheme.typography.kicker,
                color = OrganicTheme.colors.accent,
                modifier = Modifier.weight(1f),
            )
            OrganicButton(
                text = stringResource(Res.string.service_photo_add),
                onClick = { onAdd?.invoke() },
                colors = OrganicButtonDefaults.ghost(),
                enabled = onAdd != null,
            )
        }

        // Сетка 2 в ряд, как в макете; строки добираются по две плитки.
        results.chunked(2).forEachIndexed { rowIndex, rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEachIndexed { columnIndex, image ->
                    val index = rowIndex * 2 + columnIndex
                    Box(Modifier.weight(1f)) {
                        PhotoTile(model = image, contentDescription = null)
                        OrganicIconButton(
                            icon = OrganicIcons.Trash,
                            onClick = { onDelete(index) },
                            contentDescription = stringResource(Res.string.service_photo_delete),
                            size = 28.dp,
                            iconSize = 14.dp,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(OrganicTheme.spacing.space2),
                        )
                    }
                }
                // Одинокая плитка в последней строке не должна растягиваться на всю ширину.
                if (rowItems.size == 1) Box(Modifier.weight(1f))
            }
        }

        note?.takeIf { it.isNotBlank() }?.let {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OrganicText(
                    text = stringResource(Res.string.service_note_short).uppercase(),
                    style = OrganicTheme.typography.kicker,
                    color = OrganicTheme.colors.accent,
                )
                OrganicText(text = it, style = OrganicTheme.typography.bodySm)
            }
        }
    }
}

private const val VisiblePhotos = 3

/**
 * Порог раскладки в два столбца. В макете детали при списке 400 занимают ~640, и карточки
 * там стоят рядом — плитки получаются мелкими, но это и есть эталон.
 */
private val PhotoCardsRowMinWidth = 560.dp

@Preview
@Composable
private fun PhotoCardsPreview() {
    PreviewSurface(width = 900.dp) {
        PhotoCards(
            references = listOf("sketch.jpg", "lines.jpg", "shadows.png", "extra.jpg"),
            results = listOf("session1.jpg", "session2.jpg"),
            note = "Осталась проработка теней в верхней части. Третий сеанс — через 3 недели.",
            onDeleteResult = {},
        )
    }
}
