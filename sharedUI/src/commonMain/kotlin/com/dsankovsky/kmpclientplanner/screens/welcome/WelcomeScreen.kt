package com.dsankovsky.kmpclientplanner.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.ui.description
import com.dsankovsky.kmpclientplanner.ui.displayName
import com.dsankovsky.kmpclientplanner.ui.emoji
import org.koin.compose.viewmodel.koinViewModel

private val serviceTypes = listOf(
    ServiceType.EDUCATION,
    ServiceType.SPORT,
    ServiceType.BEAUTY,
    ServiceType.TATTOO,
    ServiceType.BASE,
)

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    val viewModel: WelcomeViewModel = koinViewModel()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isCompact = maxWidth < 600.dp
        val contentMaxWidth = if (isCompact) maxWidth else 520.dp
        val horizontalPadding = if (isCompact) 24.dp else 32.dp
        val verticalPadding = if (isCompact) 24.dp else 48.dp

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = contentMaxWidth)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            ) {
                WelcomeHeader(isCompact = isCompact)

                Spacer(modifier = Modifier.height(24.dp))

                serviceTypes.forEach { type ->
                    ServiceTypeOption(
                        type = type,
                        isSelected = type == selectedType,
                        onClick = { viewModel.selectType(type) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { viewModel.saveAndContinue(onContinue) },
                        enabled = selectedType != null
                    ) {
                        Text("Продолжить")
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeHeader(isCompact: Boolean) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "📋", style = MaterialTheme.typography.headlineMedium)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "KMP Планировщик",
        style = if (isCompact) MaterialTheme.typography.headlineMedium
        else MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Выберите тип услуг",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ServiceTypeOption(
    type: ServiceType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surface

    val borderColor = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.outlineVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(text = type.emoji(), style = MaterialTheme.typography.titleLarge)
        }

        Column {
            Text(
                text = type.displayName(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = type.description(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
