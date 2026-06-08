@file:OptIn(ExperimentalFoundationApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.services

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.components.HeaderView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.main_title
import kmpclientplanner.sharedui.generated.resources.services_list_no_services_description
import kmpclientplanner.sharedui.generated.resources.tabs_month
import kmpclientplanner.sharedui.generated.resources.tabs_today
import kmpclientplanner.sharedui.generated.resources.tabs_tomorrow
import kmpclientplanner.sharedui.generated.resources.tabs_week
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onEvent: (ServicesListScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel: ServicesScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleAction(ServicesListScreenAction.LoadData)
    }

    viewModel.event.collectWithLifecycle {
        onEvent(it)
    }

    when {
        state.isLoading -> LoadingScreen()
        else -> {
            HomeScreenContent(
                state = state,
                onAction = viewModel::handleAction,
                modifier = modifier
            )
        }
    }
}

@Composable
fun HomeScreenContent(
    state: ServicesListScreenState,
    onAction: (ServicesListScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(ServicesListScreenAction.OnAddServiceClicked)
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
            contentPadding = PaddingValues(
                top = 24.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 100.dp
            ).withNavBarPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                HeaderView(stringResource(Res.string.main_title))
            }

            item {
                val selectedIndex = state.filtersList.indexOf(state.currentFilter)
                PrimaryScrollableTabRow(selectedTabIndex = selectedIndex) {
                    state.filtersList.forEachIndexed { index, filter ->
                        Tab(
                            selected = index == selectedIndex,
                            onClick = { onAction(ServicesListScreenAction.OnFilterClicked(filter)) },
                            text = { Text(filter.toTabLabel()) }
                        )
                    }
                }
            }

            if (state.items.isEmpty()) {
                item {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        text = stringResource(Res.string.services_list_no_services_description),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            items(state.items, key = { it }) { item ->
                when (item) {
                    is ServicesListScreenItem.DateDivider -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = item.getUIDate()
                            )
                            HorizontalDivider(thickness = 2.dp)
                        }
                    }

                    is ServicesListScreenItem.ServiceItem -> {
                        ServiceItemView(
                            serviceItem = item,
                            onAction = onAction
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceItemView(
    serviceItem: ServicesListScreenItem.ServiceItem,
    onAction: (ServicesListScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { onAction(ServicesListScreenAction.OnServiceClicked(serviceItem)) }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = serviceItem.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (serviceItem.isPaid) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        modifier = Modifier.size(25.dp),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }

            serviceItem.comment?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Row(
                modifier = Modifier
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = serviceItem.timeInterval,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person, modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = serviceItem.client.getFullName(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            serviceItem.client.address?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


@Composable
private fun ServicesFilter.toTabLabel(): String = when (this) {
    ServicesFilter.TODAY -> stringResource(Res.string.tabs_today)
    ServicesFilter.TOMORROW -> stringResource(Res.string.tabs_tomorrow)
    ServicesFilter.NEXT_WEEK -> stringResource(Res.string.tabs_week)
    ServicesFilter.NEXT_MONTH -> stringResource(Res.string.tabs_month)
    else -> name
}

@PreviewLightDark
@Composable
private fun PreviewHomeScreen() {
    ClientPlannerTheme {
        HomeScreenContent(
            ServicesListScreenState(
                items = listOf(
                    ServicesListScreenItem.DateDivider(
                        date = getCurrentDateTime().date
                    ),
                    ServicesListScreenItem.ServiceItem(
                        title = "Playing videogames",
                        client = BaseClient(
                            name = "Otis",
                            surname = "Pes",
                            address = "Kolasa street"
                        ),
                        isFinished = true,
                        isPaid = true,
                        comment = "Помыть собаку",
                        timeInterval = "12:00 - 13:30"
                    ),
                    ServicesListScreenItem.ServiceItem(
                        title = "Playing videogames",
                        client = BaseClient(
                            name = "Otis",
                            surname = "Pes",
                            address = "Kolasa street"
                        ),
                        timeInterval = "12:00 - 13:30"
                    ),
                    ServicesListScreenItem.DateDivider(
                        date = getCurrentDateTime().date
                    ),
                    ServicesListScreenItem.ServiceItem(
                        title = "Playing videogames",
                        client = BaseClient(name = "Otis", surname = "Pes"),
                        timeInterval = "12:00 - 13:30"
                    ),
                    ServicesListScreenItem.ServiceItem(
                        title = "Playing videogames",
                        client = BaseClient(name = "Otis", surname = "Pes"),
                        timeInterval = "12:00 - 13:30"
                    )
                )
            ), {})
    }
}