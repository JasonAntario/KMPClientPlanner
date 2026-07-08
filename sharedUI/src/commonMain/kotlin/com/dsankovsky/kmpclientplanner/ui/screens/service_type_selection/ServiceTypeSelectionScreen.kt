package com.dsankovsky.kmpclientplanner.ui.screens.service_type_selection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Gesture
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.ui.extensions.edgeToEdgeBottomPadding
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_type_selection_description
import kmpclientplanner.sharedui.generated.resources.service_type_selection_title
import kmpclientplanner.sharedui.generated.resources.start
import org.jetbrains.compose.resources.stringResource

private fun ServiceType.icon(): ImageVector = when (this) {
    ServiceType.EDUCATION -> Icons.Rounded.School
    ServiceType.SPORT -> Icons.Rounded.FitnessCenter
    ServiceType.BEAUTY -> Icons.Rounded.Face
    ServiceType.TATTOO -> Icons.Rounded.Gesture
    ServiceType.BASE -> Icons.Rounded.Category
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServiceTypeSelectionScreen(
    onServiceTypeClicked: (ServiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf<ServiceType?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp)
            .edgeToEdgeBottomPadding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(6.dp, RoundedCornerShape(20.dp), clip = false)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.EditNote,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = stringResource(Res.string.service_type_selection_title),
            style = MaterialTheme.typography.displaySmall,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.service_type_selection_description),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 520.dp)
        )

        Spacer(Modifier.height(32.dp))

        FlowRow(
            modifier = Modifier.widthIn(max = 560.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ServiceType.entries.sortedDescending().forEach { serviceType ->
                ServiceTypeCard(
                    name = serviceType.toUIName(),
                    icon = serviceType.icon(),
                    selected = selected == serviceType,
                    onClick = { selected = serviceType }
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = { selected?.let(onServiceTypeClicked) },
            enabled = selected != null,
            shape = CircleShape,
            modifier = Modifier.height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(Res.string.start),
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun ServiceTypeCard(
    name: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val border = if (selected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
    val tileColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val tileContent = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val labelColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier
            .width(176.dp)
            .clip(shape)
            .background(containerColor)
            .border(border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(tileColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tileContent,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = name,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = labelColor,
            textAlign = TextAlign.Center
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewServiceTypeSelectionScreen() {
    ClientPlannerTheme {
        ServiceTypeSelectionScreen({})
    }
}
