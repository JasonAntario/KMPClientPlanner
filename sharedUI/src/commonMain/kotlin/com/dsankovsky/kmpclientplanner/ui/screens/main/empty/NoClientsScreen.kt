package com.dsankovsky.kmpclientplanner.ui.screens.main.empty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.ui.extensions.edgeToEdgeBottomPadding
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_add_client
import kmpclientplanner.sharedui.generated.resources.clients_list_no_clients
import kmpclientplanner.sharedui.generated.resources.clients_list_no_clients_description
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
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp)
            .edgeToEdgeBottomPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.PersonAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.clients_list_no_clients),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.clients_list_no_clients_description),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 340.dp)
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onAddClientCLicked,
            shape = CircleShape,
            modifier = Modifier.height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(20.dp)
            )
            Text(
                text = stringResource(Res.string.client_add_client),
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onChangeServiceTypeCLicked) {
            Text(
                text = stringResource(Res.string.service_type_change),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
