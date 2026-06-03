@file:OptIn(ExperimentalFoundationApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.services_history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.theme.ClientPlannerTheme
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.services_list_no_services_description
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ServicesHistoryScreen(
    clientId: Long,
    onEvent: (ServicesHistoryScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel: ServicesHistoryScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleAction(ServicesHistoryScreenAction.LoadData(clientId))
    }

    viewModel.event.collectWithLifecycle {
        onEvent(it)
    }

    when {
        state.isLoading -> LoadingScreen()
        else -> {
            ServicesHistoryScreenContent(
                state = state,
                onAction = viewModel::handleAction,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ServicesHistoryScreenContent(
    state: ServicesHistoryScreenState,
    onAction: (ServicesHistoryScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = 24.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = 100.dp
        ).withNavBarPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

//        item {
//            KufarDetailsScreenHeader(
//                title = "История услуг",
//                onBackClicked = {
//                    onAction(ServicesHistoryScreenAction.OnCloseScreenClicked)
//                }
//            )
//        }

        if (state.items.isEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(Res.string.services_list_no_services_description),
                )
            }
        }

        state.items.forEachIndexed { index, item ->
            when (item) {
                is ServicesHistoryScreenItem.DateDivider -> {
//                    stickyHeader {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(16.dp),
//                            modifier = Modifier
//                                .background(MaterialTheme.colorScheme.background)
//                                .padding(vertical = 4.dp)
//                        ) {
//                            Text(
//                                text = "${stringResource(item.dayOfWeek)}, ${item.day} ${
//                                    stringResource(
//                                        item.month
//                                    )
//                                }",
//                                textStyle = KufarTheme.typography.H3
//                            )
//                            HorizontalDivider(thickness = 2.dp)
//                        }
//                    }
                }

                is ServicesHistoryScreenItem.ServiceItem -> {
                    item(item.id) {
//                        var isRevealed by remember { mutableStateOf(false) }
//                        KufarSwipeableItemWithActions(
//                            isRevealed = isRevealed,
//                            onExpanded = {
//                                isRevealed = true
//                            },
//                            onCollapsed = {
//                                isRevealed = false
//                            },
//                            actions = {
//                                Row(
//                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
//                                    modifier = Modifier.padding(start = 4.dp)
//                                ) {
//                                    ActionIcon(
//                                        onClick = {
//                                            onAction(
//                                                ServicesHistoryScreenAction.OnPaidStatusChanged(
//                                                    item
//                                                )
//                                            )
//                                            isRevealed = false
//                                        },
//                                        backgroundColor = if (item.isPaid) KufarTheme.colors.bg.accent.green.main else KufarTheme.colors.bg.grey.tertiary,
//                                        icon = Icons.Outlined.AttachMoney,
//                                        iconColor = if (item.isPaid) KufarTheme.colors.icon.grey.lighter else KufarTheme.colors.icon.grey.darkest,
//                                        modifier = Modifier.fillMaxHeight()
//                                    )
//                                    ActionIcon(
//                                        onClick = {
//                                            onAction(
//                                                ServicesHistoryScreenAction.OnFinishStatusChanged(
//                                                    item
//                                                )
//                                            )
//                                            isRevealed = false
//                                        },
//                                        backgroundColor = if (item.isFinished) KufarTheme.colors.bg.accent.green.main else KufarTheme.colors.bg.grey.tertiary,
//                                        icon = Icons.Outlined.CalendarMonth,
//                                        iconColor = if (item.isFinished) KufarTheme.colors.icon.grey.lighter else KufarTheme.colors.icon.grey.darkest,
//                                        modifier = Modifier.fillMaxHeight()
//                                    )
//                                    ActionIcon(
//                                        onClick = {
//                                            onAction(
//                                                ServicesHistoryScreenAction.OnDeleteService(
//                                                    item
//                                                )
//                                            )
//                                            isRevealed = false
//                                        },
//                                        backgroundColor = KufarTheme.colors.bg.accent.red.main,
//                                        icon = Icons.Outlined.Delete,
//                                        iconColor = KufarTheme.colors.icon.grey.lighter,
//                                        modifier = Modifier.fillMaxHeight()
//                                    )
//                                }
//                            }
//                        ) {
                        ServiceItemView(
                            serviceItem = item,
                            onAction = onAction
                        )
//                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceItemView(
    serviceItem: ServicesHistoryScreenItem.ServiceItem,
    onAction: (ServicesHistoryScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { onAction(ServicesHistoryScreenAction.OnServiceClicked(serviceItem)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = serviceItem.title,
                    modifier = Modifier.weight(1f)
                )
                if (serviceItem.isPaid) {
                    Image(
                        contentDescription = null,
                        imageVector = Icons.Default.AttachMoney,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            serviceItem.comment?.let {
                Text(it)
            }

            Row(
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    contentDescription = null,
                    imageVector = Icons.Default.AccessTime,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = serviceItem.timeInterval)
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun PreviewServicesHistoryScreenContent() {
    ClientPlannerTheme {
        ServicesHistoryScreenContent(
            ServicesHistoryScreenState(
                items = listOf(
                    ServicesHistoryScreenItem.DateDivider(
                        day = 2,
                        dayOfWeek = 1,
                        month = 1
                    ),
                    ServicesHistoryScreenItem.ServiceItem(
                        title = "Playing videogames",

                        isFinished = true,
                        isPaid = true,
                        comment = "Помыть собаку",
                        timeInterval = "12:00 - 13:30"
                    ),
                    ServicesHistoryScreenItem.ServiceItem(
                        title = "Playing videogames",

                        timeInterval = "12:00 - 13:30"
                    ),
                    ServicesHistoryScreenItem.DateDivider(
                        11,
                        1,
                        1
                    ),
                    ServicesHistoryScreenItem.ServiceItem(
                        title = "Playing videogames",
                        timeInterval = "12:00 - 13:30"
                    ),
                    ServicesHistoryScreenItem.ServiceItem(
                        title = "Playing videogames",
                        timeInterval = "12:00 - 13:30"
                    )
                )
            ), {})
    }
}