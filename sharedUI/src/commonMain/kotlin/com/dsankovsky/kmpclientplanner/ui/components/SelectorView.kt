package com.dsankovsky.kmpclientplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_offline
import kmpclientplanner.sharedui.generated.resources.client_online
import org.jetbrains.compose.resources.stringResource

/**
 * Fully-rounded Онлайн/Оффлайн segmented control from the lavender/pink M3 redesign.
 * The active segment fills [secondaryContainer], inactive segments are transparent inside a
 * 1dp outline pill. Signature is unchanged so existing bindings keep working.
 */
@Composable
fun BooleanSelectorView(
    value: Boolean,
    falseLabel: String,
    trueLabel: String,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SegmentItem(
            selected = !value,
            label = falseLabel,
            icon = Icons.Rounded.Place,
            onClick = { onChange(false) }
        )
        SegmentItem(
            selected = value,
            label = trueLabel,
            icon = Icons.Rounded.Videocam,
            onClick = { onChange(true) }
        )
    }
}

@Composable
private fun RowScope.SegmentItem(
    selected: Boolean,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = Modifier
            .weight(1f)
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewBooleanSelectorView() {
    ClientPlannerTheme {
        BooleanSelectorView(
            value = false,
            falseLabel = stringResource(Res.string.client_offline),
            trueLabel = stringResource(Res.string.client_online),
            onChange = {}
        )
    }
}
