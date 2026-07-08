@file:OptIn(ExperimentalFoundationApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.services

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIDate
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.main_title
import kmpclientplanner.sharedui.generated.resources.service_add_service
import kmpclientplanner.sharedui.generated.resources.services_list_no_services_description
import kmpclientplanner.sharedui.generated.resources.tabs_current_month
import kmpclientplanner.sharedui.generated.resources.tabs_current_week
import kmpclientplanner.sharedui.generated.resources.tabs_next_month
import kmpclientplanner.sharedui.generated.resources.tabs_next_week
import kmpclientplanner.sharedui.generated.resources.tabs_today
import kmpclientplanner.sharedui.generated.resources.tabs_tomorrow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onEvent: (ServicesListScreenEvent) -> Unit,
    showFab: Boolean = true,
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
                showFab = showFab,
                modifier = modifier
            )
        }
    }
}

@Composable
fun HomeScreenContent(
    state: ServicesListScreenState,
    onAction: (ServicesListScreenAction) -> Unit,
    showFab: Boolean = true,
    modifier: Modifier = Modifier
) {

    Scaffold(
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        onAction(ServicesListScreenAction.OnAddServiceClicked)
                    },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
            }
        }
    ) { paddingValues ->

        val lessonsCount = state.items.count { it is ServicesListScreenItem.ServiceItem }

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
                HomeHeader(
                    lessonsCount = lessonsCount,
                    showAddButton = !showFab,
                    onAddClicked = { onAction(ServicesListScreenAction.OnAddServiceClicked) }
                )
            }

            item {
                PeriodTabRow(
                    filters = state.filtersList,
                    current = state.currentFilter,
                    onFilterClicked = { onAction(ServicesListScreenAction.OnFilterClicked(it)) }
                )
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

            items(state.items) { item ->
                when (item) {
                    is ServicesListScreenItem.DateDivider -> {
                        DateDividerRow(item.getUIDate())
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
private fun HomeHeader(
    lessonsCount: Int,
    showAddButton: Boolean,
    onAddClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.main_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${getCurrentDateTime().date.toUIDate()} · $lessonsCount",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (showAddButton) {
            Button(
                onClick = onAddClicked,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(Res.string.service_add_service),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PeriodTabRow(
    filters: List<ServicesFilter>,
    current: ServicesFilter,
    onFilterClicked: (ServicesFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        filters.forEach { filter ->
            val selected = filter == current
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onFilterClicked(filter) }
            ) {
                Text(
                    text = filter.toTabLabel(),
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(3.dp)
                        .background(
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                            },
                            shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun DateDividerRow(text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun ServicesFilter.toTabLabel(): String = when (this) {
    ServicesFilter.TODAY -> stringResource(Res.string.tabs_today)
    ServicesFilter.TOMORROW -> stringResource(Res.string.tabs_tomorrow)
    ServicesFilter.CURRENT_WEEK -> stringResource(Res.string.tabs_current_week)
    ServicesFilter.NEXT_WEEK -> stringResource(Res.string.tabs_next_week)
    ServicesFilter.CURRENT_MONTH -> stringResource(Res.string.tabs_current_month)
    ServicesFilter.NEXT_MONTH -> stringResource(Res.string.tabs_next_month)
    ServicesFilter.CUSTOM_INTERVAL -> name
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
