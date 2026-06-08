package com.dsankovsky.kmpclientplanner.ui.screens.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.components.CardView
import com.dsankovsky.kmpclientplanner.ui.components.HeaderView
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
                modifier = modifier
            )
        }
    }
}

@Composable
fun ClientsListScreenContent(
    screenState: ClientsListScreenState,
    onAction: (ClientsListScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) { listState.scrollToItem(20) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(ClientsListScreenAction.AddClientClicked)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp).withNavBarPadding(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            item {
                HeaderView(
                    stringResource(Res.string.nav_bar_clients),
                    modifier = Modifier.padding(horizontal = 16.dp)
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
                        LetterDividerItem(item.letter, modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
fun ClientItem(
    client: BaseClient,
    modifier: Modifier = Modifier,
    onClientClicked: () -> Unit
) {
    CardView(
        modifier = modifier.fillMaxWidth(),
        onClick = onClientClicked,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = client.getShortName(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Text(
                text = client.getFullName(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        val serviceName = client.serviceType.toUIName()

        Text(
            text = serviceName,
            modifier = Modifier.padding(start = 40.dp),
        )
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
            .padding(horizontal = 16.dp).padding(bottom = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
        ) {
            Text(
                text = letter,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
            )
        }
        HorizontalDivider()
    }
}

@PreviewLightDark
@Composable
private fun PreviewClientsListScreen() {
    ClientPlannerTheme {
        ClientsListScreenContent(
            screenState = ClientsListScreenState(
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