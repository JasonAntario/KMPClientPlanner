package com.dsankovsky.kmpclientplanner.ui.screens.services_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.components.ToolbarView
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.withNavBarPadding
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServiceItemView
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenItem
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.services_list_history
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

    Scaffold(
        topBar = {
            ToolbarView(
                title = stringResource(Res.string.services_list_history),
                onBackClicked = {
                    viewModel.handleAction(ServicesHistoryScreenAction.OnCloseScreenClicked)
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingScreen()
            else -> ServicesHistoryScreenContent(
                state = state,
                onAction = viewModel::handleAction,
                modifier = modifier.padding(paddingValues)
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
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            top = 24.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = 100.dp
        ).withNavBarPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                        Text(text = item.getUIDate())
                        HorizontalDivider(thickness = 2.dp)
                    }
                }

                is ServicesListScreenItem.ServiceItem -> {
                    ServiceItemView(
                        serviceItem = item,
                        onAction = { action ->
                            when (action) {
                                is com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenAction.OnServiceClicked ->
                                    onAction(ServicesHistoryScreenAction.OnServiceClicked(action.serviceItem))
                                is com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenAction.OnDeleteService ->
                                    onAction(ServicesHistoryScreenAction.OnDeleteService(action.serviceItem))
                                is com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenAction.OnPaidStatusChanged ->
                                    onAction(ServicesHistoryScreenAction.OnPaidStatusChanged(action.serviceItem))
                                is com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenAction.OnFinishStatusChanged ->
                                    onAction(ServicesHistoryScreenAction.OnFinishStatusChanged(action.serviceItem))
                                else -> Unit
                            }
                        }
                    )
                }
            }
        }
    }
}
