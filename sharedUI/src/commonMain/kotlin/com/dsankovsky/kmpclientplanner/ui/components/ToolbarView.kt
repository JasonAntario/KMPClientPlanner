package com.dsankovsky.kmpclientplanner.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme

@Composable
fun ToolbarView(
    title: String,
    showBackButton: Boolean = true,
    actionIcon: ImageVector? = null,
    actionIconColor: Color = MaterialTheme.colorScheme.onSurface,
    onActionClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(
                    onClick = onBackClicked
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = {
            actionIcon?.let {
                IconButton(
                    onClick = onActionClicked
                ) {
                    Icon(it, contentDescription = null, tint = actionIconColor)
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    ClientPlannerTheme {
        ToolbarView(
            title = "Otis",
            actionIcon = Icons.Default.Edit,
            onActionClicked = {},
            onBackClicked = {}
        )
    }
}