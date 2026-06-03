package com.dsankovsky.kmpclientplanner.ui.screens.main.empty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.dsankovsky.kmpclientplanner.ui.extensions.edgeToEdgeBottomPadding
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_add_client
import kmpclientplanner.sharedui.generated.resources.clients_list_no_clients_description
import kmpclientplanner.sharedui.generated.resources.nav_bar_clients
import kmpclientplanner.sharedui.generated.resources.service_type_change
import org.jetbrains.compose.resources.stringResource

@Composable
fun NoClientsScreen(
    modifier: Modifier = Modifier,
    onAddClientCLicked: () -> Unit,
    onChangeServiceTypeCLicked: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = 24.dp,
                start = 16.dp,
                end = 16.dp
            )
            .edgeToEdgeBottomPadding(),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            text = stringResource(Res.string.nav_bar_clients),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.clients_list_no_clients_description),
        )
        Spacer(modifier = Modifier.weight(1f))
        TextButton(
            onClick = onAddClientCLicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.client_add_client))
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(
            onClick = onChangeServiceTypeCLicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.service_type_change))
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewNoClientsScreen() {
    ClientPlannerTheme {
        NoClientsScreen(onAddClientCLicked = {}, onChangeServiceTypeCLicked = {})
    }
}