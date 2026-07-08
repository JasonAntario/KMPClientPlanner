package com.dsankovsky.kmpclientplanner.ui.screens.client_details.specific_fields

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsActions
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_current_project
import kmpclientplanner.sharedui.generated.resources.client_finish_project
import kmpclientplanner.sharedui.generated.resources.client_finished_projects
import org.jetbrains.compose.resources.stringResource

@Composable
fun TattooClientFieldsView(
    fields: ClientSpecificFields.TattooClientSpecificFields,
    onAction: (ClientDetailsActions) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard(title = stringResource(Res.string.client_current_project)) {
            SketchGrid(count = fields.currentProject.imageUrls.size)

            TextButton(
                onClick = {
                    onAction(ClientDetailsActions.TattooClientAction.OnFinishProjectClicked)
                },
                enabled = fields.currentProject.imageUrls.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(Res.string.client_finish_project),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (fields.finishedProjects.isNotEmpty()) {
            SectionCard(title = stringResource(Res.string.client_finished_projects)) {
                fields.finishedProjects.forEach { project ->
                    SketchGrid(count = project.imageUrls.size)
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        content()
    }
}

@Composable
private fun SketchGrid(
    count: Int,
    modifier: Modifier = Modifier
) {
    val tiles = count.coerceAtLeast(1)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (0 until tiles).toList().chunked(4).forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTiles.forEach { _ ->
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun PreviewAddEditClientScreenTattoo() {
    ClientPlannerTheme {
        TattooClientFieldsView(
            fields = ClientSpecificFields.TattooClientSpecificFields(
                finishedProjects = listOf(
                    ClientSpecificFields.TattooClientSpecificFields.TattooProject(
                        listOf("asfdsf")
                    )
                )
            ),
            {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
