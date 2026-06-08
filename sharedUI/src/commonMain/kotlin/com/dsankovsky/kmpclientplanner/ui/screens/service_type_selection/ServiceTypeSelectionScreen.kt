package com.dsankovsky.kmpclientplanner.ui.screens.service_type_selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.ui.components.HeaderView
import com.dsankovsky.kmpclientplanner.ui.extensions.edgeToEdgeBottomPadding
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_type_selection_description
import kmpclientplanner.sharedui.generated.resources.service_type_selection_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ServiceTypeSelectionScreen(
    onServiceTypeClicked: (ServiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            top = 24.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ).withNavBarPadding(),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .edgeToEdgeBottomPadding(0.dp)
    ) {
        item {
            HeaderView(stringResource(Res.string.service_type_selection_title))
        }

        item {
            Text(
                text = stringResource(Res.string.service_type_selection_description),
                modifier = Modifier.padding(bottom = 24.dp, top = 12.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(ServiceType.entries.sortedDescending()) { serviceType ->
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onServiceTypeClicked(serviceType)
                }
            ) {
                Text(serviceType.toUIName(), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewServiceTypeSelectionScreen() {
    ClientPlannerTheme {
        ServiceTypeSelectionScreen({})
    }
}