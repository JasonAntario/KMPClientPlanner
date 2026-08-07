@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.services

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsPane
import kotlinx.coroutines.launch
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.EmptyState
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicScreenHeader
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.SegmentedControl
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIWeekdayAndDate
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.main_title
import kmpclientplanner.sharedui.generated.resources.service_add_service
import kmpclientplanner.sharedui.generated.resources.services_list_no_services
import kmpclientplanner.sharedui.generated.resources.services_list_no_services_description
import kmpclientplanner.sharedui.generated.resources.services_list_subtitle
import kmpclientplanner.sharedui.generated.resources.services_list_subtitle_unpaid
import kmpclientplanner.sharedui.generated.resources.tabs_current_month
import kmpclientplanner.sharedui.generated.resources.tabs_current_week
import kmpclientplanner.sharedui.generated.resources.tabs_next_month
import kmpclientplanner.sharedui.generated.resources.tabs_next_week
import kmpclientplanner.sharedui.generated.resources.tabs_today
import kmpclientplanner.sharedui.generated.resources.tabs_tomorrow
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Экраны 04 и 05–07 — одна страница: лента занятий и детали выбранного.
 *
 * Пока ничего не выбрано, лента занимает всю ширину (04). После выбора занятия появляется
 * вторая панель, а лента сжимается до 400 и переходит на компактные строки (05–07).
 */
@Composable
fun HomeScreen(
    onEvent: (ServicesListScreenEvent) -> Unit,
    onEditService: (serviceId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {

    val viewModel: ServicesScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.handleAction(ServicesListScreenAction.LoadData)
    }

    viewModel.event.collectWithLifecycle { event ->
        when (event) {
            is ServicesListScreenEvent.OpenServiceInfo -> scope.launch {
                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, event.serviceId)
            }

            else -> onEvent(event)
        }
    }

    if (state.isLoading) {
        LoadingScreen()
        return
    }

    // Выделение живёт в состоянии, а не в истории навигатора: крестик в деталях должен
    // снимать его сразу, а `navigateBack` вернул бы предыдущее занятие, а не ленту.
    val selectedServiceId = state.selectedServiceId

    // Пока занятие не выбрано, экран 04 — это одна лента во всю ширину. Scaffold в этом
    // состоянии всё равно показал бы две панели и сжал бы строку до многоточий.
    if (selectedServiceId == null) {
        HomeScreenContent(
            state = state,
            onAction = viewModel::handleAction,
            modifier = modifier,
        )
        return
    }

    ListDetailPaneScaffold(
        // Панели стоят вплотную: список отделён своим фоном, а не воздухом.
        directive = navigator.scaffoldDirective.copy(horizontalPartitionSpacerSize = 0.dp),
        value = navigator.scaffoldValue,
        modifier = modifier,
        listPane = {
            AnimatedPane(Modifier.preferredWidth(ServicesListPaneWidth)) {
                HomeScreenContent(
                    state = state,
                    onAction = viewModel::handleAction,
                    selectedServiceId = selectedServiceId,
                    compactList = true,
                )
            }
        },
        detailPane = {
            AnimatedPane {
                ServiceDetailsPane(
                    serviceId = selectedServiceId,
                    onEditService = { onEditService(selectedServiceId) },
                    onStatusUpdated = { onEvent(ServicesListScreenEvent.StatusUpdated) },
                    onServiceDeleted = {
                        onEvent(ServicesListScreenEvent.ServiceDeleted)
                        viewModel.handleAction(ServicesListScreenAction.OnCloseDetailsClicked)
                    },
                    // Крестик работает на любой ширине: детали закрываются, лента
                    // разворачивается обратно на весь экран.
                    onClose = {
                        viewModel.handleAction(ServicesListScreenAction.OnCloseDetailsClicked)
                    },
                )
            }
        },
    )
}

/**
 * Лента занятий (экран 04) и она же — список рядом с деталями (05–07).
 *
 * Шапка: «Занятия», подзаголовок с сегодняшней датой и счётчиками, сегмент-контрол периода
 * и primary-кнопка добавления. Кнопка теперь в шапке, а не FAB'ом поверх ленты — так в макете.
 *
 * @param compactList режим панели рядом с деталями: узкие строки со статусами-тегами,
 *   без шапки экрана — она остаётся на широкой ленте
 */
@Composable
fun HomeScreenContent(
    state: ServicesListScreenState,
    onAction: (ServicesListScreenAction) -> Unit,
    modifier: Modifier = Modifier,
    selectedServiceId: Long? = null,
    compactList: Boolean = false,
) {
    if (compactList) {
        ServicesListPane(
            state = state,
            onAction = onAction,
            selectedServiceId = selectedServiceId,
            modifier = modifier,
        )
        return
    }
    val spacing = OrganicTheme.spacing
    // Раскладка строк зависит от ширины контента, а не окна: рейл забирает 248,
    // поэтому «широкое» окно ещё не значит, что строка занятия влезает в одну линию.
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(OrganicTheme.colors.bg),
    ) {
        val compactRows = maxWidth < ServiceRowSingleLineWidth
        Column(
            modifier = Modifier.padding(horizontal = 40.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            FeedHeader(state = state, onAction = onAction)

            if (state.items.isEmpty()) {
                EmptyState(
                    icon = OrganicIcons.CalendarDays,
                    title = stringResource(Res.string.services_list_no_services),
                    description = stringResource(Res.string.services_list_no_services_description),
                    actionText = stringResource(Res.string.service_add_service),
                    onAction = { onAction(ServicesListScreenAction.OnAddServiceClicked) },
                    circleColor = OrganicTheme.colors.accentRamp.s200,
                    iconColor = OrganicTheme.colors.accentRamp.s800,
                )
            } else {
                // Первая группа — ближайший день, остальные приглушены (см. `dimmed`).
                var groupIndex = -1
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(spacing.space2),
                    contentPadding = PaddingValues(bottom = spacing.space6),
                ) {
                    items(state.items) { item ->
                        when (item) {
                            is ServicesListScreenItem.DateDivider -> {
                                groupIndex++
                                OrganicText(
                                    text = item.date.toUIWeekdayAndDate().uppercase(),
                                    style = OrganicTheme.typography.tableHeader,
                                    color = OrganicTheme.colors.muted,
                                    modifier = Modifier.padding(
                                        top = if (groupIndex == 0) 0.dp else spacing.space3,
                                        bottom = 2.dp,
                                    ),
                                )
                            }

                            is ServicesListScreenItem.ServiceItem -> ServiceItemView(
                                serviceItem = item,
                                onAction = onAction,
                                dimmed = groupIndex > 0,
                                compact = compactRows,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Ниже этой ширины строка занятия не влезает в одну линию и складывается в две. */
private val ServiceRowSingleLineWidth = 760.dp

/**
 * Список занятий рядом с деталями (левая колонка 400 на экранах 05–07): киккер с периодом,
 * компактные строки, выбранная — на `surface` с обводкой.
 */
@Composable
private fun ServicesListPane(
    state: ServicesListScreenState,
    onAction: (ServicesListScreenAction) -> Unit,
    selectedServiceId: Long?,
    modifier: Modifier = Modifier,
) {
    val spacing = OrganicTheme.spacing
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrganicTheme.colors.surface.copy(alpha = ListPaneBackgroundAlpha))
            .padding(horizontal = 20.dp, vertical = 26.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Сегмент-контрол здесь тоже нужен: шапка ленты уехала вместе с широкой раскладкой,
        // а период менять надо и с открытыми деталями. Шире панели — прокручивается.
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SegmentedControl(
                options = state.filtersList,
                selected = state.currentFilter,
                onSelect = { onAction(ServicesListScreenAction.OnFilterClicked(it)) },
                optionLabel = { it.toTabLabel() },
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(spacing.space1),
            contentPadding = PaddingValues(bottom = spacing.space4),
        ) {
            items(state.items) { item ->
                when (item) {
                    is ServicesListScreenItem.DateDivider -> OrganicText(
                        text = item.date.toUIWeekdayAndDate().uppercase(),
                        style = OrganicTheme.typography.tableHeader,
                        color = OrganicTheme.colors.muted,
                        modifier = Modifier.padding(start = 18.dp, top = spacing.space2, bottom = 2.dp),
                    )

                    is ServicesListScreenItem.ServiceItem -> ServiceRowCompact(
                        serviceItem = item,
                        selected = item.id == selectedServiceId,
                        onClick = { onAction(ServicesListScreenAction.OnServiceClicked(item)) },
                    )
                }
            }
        }
    }
}

private const val ListPaneBackgroundAlpha = 0.45f

@Composable
private fun FeedHeader(
    state: ServicesListScreenState,
    onAction: (ServicesListScreenAction) -> Unit,
) {
    val services = state.items.filterIsInstance<ServicesListScreenItem.ServiceItem>()
    OrganicScreenHeader(
        title = stringResource(Res.string.main_title),
        subtitle = feedSubtitle(services),
        actions = {
            SegmentedControl(
                options = state.filtersList,
                selected = state.currentFilter,
                onSelect = { onAction(ServicesListScreenAction.OnFilterClicked(it)) },
                optionLabel = { it.toTabLabel() },
            )
            OrganicButton(
                text = stringResource(Res.string.service_add_service),
                onClick = { onAction(ServicesListScreenAction.OnAddServiceClicked) },
                icon = OrganicIcons.Plus,
            )
        },
    )
}

/** «Среда, 29 июля · 5 занятий, 2 не оплачены». */
@Composable
private fun feedSubtitle(services: List<ServicesListScreenItem.ServiceItem>): String {
    val today = getCurrentDateTime().date.toUIWeekdayAndDate()
    val total = pluralStringResource(Res.plurals.services_list_subtitle, services.size, services.size)
    val unpaid = services.count { !it.isPaid }
    return if (unpaid > 0) {
        val unpaidText = pluralStringResource(Res.plurals.services_list_subtitle_unpaid, unpaid, unpaid)
        "$today · $total, $unpaidText"
    } else {
        "$today · $total"
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

@Preview
@Composable
private fun HomeScreenContentPreview() {
    OrganicTheme {
        HomeScreenContent(
            state = ServicesListScreenState(
                isLoading = false,
                items = listOf(
                    ServicesListScreenItem.DateDivider(date = getCurrentDateTime().date),
                    ServicesListScreenItem.ServiceItem(
                        title = "Английский",
                        client = BaseClient(name = "Мария", surname = "Сак"),
                        service = BaseService(price = 40f, address = "онлайн"),
                        isPaid = true,
                        isFinished = true,
                    ),
                    ServicesListScreenItem.ServiceItem(
                        title = "Математика",
                        client = BaseClient(name = "Олег", surname = "Тарасов"),
                        service = BaseService(price = 60f, address = "Немига 12"),
                    ),
                ),
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun HomeScreenEmptyPreview() {
    OrganicTheme {
        HomeScreenContent(state = ServicesListScreenState(isLoading = false), onAction = {})
    }
}
