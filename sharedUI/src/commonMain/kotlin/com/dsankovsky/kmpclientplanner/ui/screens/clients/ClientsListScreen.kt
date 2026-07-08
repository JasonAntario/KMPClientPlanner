package com.dsankovsky.kmpclientplanner.ui.screens.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.components.ShortNameBoxView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIName
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.nav_bar_clients
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ClientsListScreen(
    onEvent: (ClientsListScreenEvent) -> Unit,
    showFab: Boolean = true,
    modifier: Modifier = Modifier
) {
    val viewModel: ClientsScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.collectWithLifecycle {
        onEvent(it)
    }

    LaunchedEffect(Unit) {
        viewModel.handleAction(ClientsListScreenAction.LoadClientsList)
    }
    when {
        state.isLoading -> {
            LoadingScreen()
        }

        else -> {
            ClientsListScreenContent(
                screenState = state,
                onAction = viewModel::handleAction,
                showFab = showFab,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ClientsListScreenContent(
    screenState: ClientsListScreenState,
    onAction: (ClientsListScreenAction) -> Unit,
    showFab: Boolean = true,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Scaffold(
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        onAction(ClientsListScreenAction.AddClientClicked)
                    }
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp).withNavBarPadding(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            item {
                ClientsHeader(
                    onAddClicked = { onAction(ClientsListScreenAction.AddClientClicked) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            items(screenState.clients) { item ->
                when (item) {
                    is ClientListItem.Client -> {
                        ClientItem(
                            client = item.client,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onClientClicked = {
                                onAction(ClientsListScreenAction.OnClientItemClicked(item.client))
                            }
                        )
                    }

                    is ClientListItem.LetterDivider -> {
                        LetterDividerItem(item.letter, Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientsHeader(
    onAddClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(Res.string.nav_bar_clients),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onAddClicked),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun ClientItem(
    client: BaseClient,
    modifier: Modifier = Modifier,
    onClientClicked: () -> Unit
) {
    val meta = listOfNotNull(
        client.serviceSubtype?.takeIf { it.isNotBlank() },
        client.serviceType.toUIName()
    ).joinToString(" · ")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClientClicked)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShortNameBoxView(
            text = client.getShortName(),
            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier.size(42.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = client.getFullName(),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (meta.isNotEmpty()) {
                Text(
                    text = meta,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LetterDividerItem(
    letter: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 2.dp),
    ) {
        Text(
            text = letter,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewClientsListScreen() {
    ClientPlannerTheme {
        ClientsListScreenContent(
            screenState = ClientsListScreenState(
                isLoading = false,
                clients = listOf(
                    ClientListItem.LetterDivider("A"),
                    ClientListItem.Client(
                        BaseClient(
                            name = "Андрей",
                            price = 35.232903f,
                            serviceType = ServiceType.EDUCATION,
                            serviceSubtype = "Английский",
                            currency = CurrencyItem.BYN
                        )
                    ),
                )
            ),
            {}
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewClientItem() {
    ClientPlannerTheme {
        ClientItem(
            BaseClient(
                name = "Андрей",
                serviceSubtype = "Танцы",
                price = 35.232903f,
                serviceType = ServiceType.EDUCATION,
                currency = CurrencyItem.BYN
            ),
            onClientClicked = {}
        )
    }
}
