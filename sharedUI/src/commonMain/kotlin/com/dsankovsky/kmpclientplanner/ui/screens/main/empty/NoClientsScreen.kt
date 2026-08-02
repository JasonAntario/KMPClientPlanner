package com.dsankovsky.kmpclientplanner.ui.screens.main.empty

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.EmptyState
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_add_client
import kmpclientplanner.sharedui.generated.resources.clients_list_no_clients
import kmpclientplanner.sharedui.generated.resources.clients_list_no_clients_description
import kmpclientplanner.sharedui.generated.resources.service_type_change
import org.jetbrains.compose.resources.stringResource

/**
 * Экран 02 — клиентов ещё нет. Рейл при этом виден: навигация не прячется, просто
 * идти пока некуда.
 */
@Composable
fun NoClientsScreen(
    modifier: Modifier = Modifier,
    onAddClientCLicked: () -> Unit,
    onChangeServiceTypeCLicked: () -> Unit,
) {
    EmptyState(
        icon = OrganicIcons.UserPlus,
        title = stringResource(Res.string.clients_list_no_clients),
        modifier = modifier.fillMaxSize().background(OrganicTheme.colors.bg),
        description = stringResource(Res.string.clients_list_no_clients_description),
        actionText = stringResource(Res.string.client_add_client),
        onAction = onAddClientCLicked,
        secondaryActionText = stringResource(Res.string.service_type_change),
        onSecondaryAction = onChangeServiceTypeCLicked,
    )
}

@Preview
@Composable
private fun NoClientsScreenPreview() {
    OrganicTheme {
        NoClientsScreen(onAddClientCLicked = {}, onChangeServiceTypeCLicked = {})
    }
}
