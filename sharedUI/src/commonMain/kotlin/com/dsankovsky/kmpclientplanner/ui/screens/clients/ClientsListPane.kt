package com.dsankovsky.kmpclientplanner.ui.screens.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.Avatar
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonColors
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicClickableSurface
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicTextField
import com.dsankovsky.kmpclientplanner.ui.design.components.PaddingValuesOf
import com.dsankovsky.kmpclientplanner.ui.design.components.PreviewSurface
import com.dsankovsky.kmpclientplanner.ui.design.elevationSm
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.client_add_client_short
import kmpclientplanner.sharedui.generated.resources.clients_search_empty
import kmpclientplanner.sharedui.generated.resources.clients_search_placeholder
import kmpclientplanner.sharedui.generated.resources.clients_title_count
import org.jetbrains.compose.resources.stringResource

/** Ширина списка клиентов из макета (экран 08). */
val ClientsListPaneWidth = 404.dp

/**
 * Левая панель экрана 08: заголовок со счётчиком, кнопка «Добавить», поиск по имени
 * и список клиентов секциями по первой букве.
 *
 * Фон панели — `surface` в 45%: так в макете список отделён от кремовой «земли» деталей,
 * но не выглядит второй карточкой.
 */
@Composable
fun ClientsListPane(
    state: ClientsListScreenState,
    onAction: (ClientsListScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OrganicTheme.colors
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface.copy(alpha = PaneBackgroundAlpha))
            .padding(horizontal = 22.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrganicText(
                text = stringResource(Res.string.clients_title_count, state.clientsCount),
                style = OrganicTheme.typography.h3,
                // Рядом несжимаемая кнопка: без ограничения на узкой панели заголовок
                // переносился бы по буквам.
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            OrganicButton(
                text = stringResource(Res.string.client_add_client_short),
                onClick = { onAction(ClientsListScreenAction.AddClientClicked) },
                icon = OrganicIcons.Plus,
                textStyle = OrganicTheme.typography.button.copy(fontSize = 13.sp),
            )
        }

        OrganicTextField(
            value = state.searchQuery,
            onValueChange = { onAction(ClientsListScreenAction.OnSearchQueryChanged(it)) },
            placeholder = stringResource(Res.string.clients_search_placeholder),
        )

        if (state.clients.isEmpty()) {
            OrganicText(
                text = stringResource(Res.string.clients_search_empty),
                style = OrganicTheme.typography.bodySm,
                color = colors.muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = OrganicTheme.spacing.space6),
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2),
                contentPadding = PaddingValues(bottom = OrganicTheme.spacing.space4),
            ) {
                items(state.clients) { item ->
                    when (item) {
                        is ClientListItem.LetterDivider -> OrganicText(
                            text = item.letter,
                            style = OrganicTheme.typography.tableHeader,
                            color = colors.muted,
                            modifier = Modifier.padding(start = 14.dp, top = OrganicTheme.spacing.space1),
                        )

                        is ClientListItem.Client -> ClientRow(
                            client = item.client,
                            selected = item.client.id == state.selectedClientId,
                            onClick = {
                                onAction(ClientsListScreenAction.OnClientItemClicked(item.client))
                            },
                        )
                    }
                }
            }
        }
    }
}

/**
 * `ClientRow` — аватар 38 с инициалами и имя 15, без второй строки.
 * Выбранная строка поднимается на `surface` с `shadow-sm`, остальные лежат плоско.
 */
@Composable
private fun ClientRow(
    client: BaseClient,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = OrganicTheme.colors
    val shape = OrganicTheme.shapes.rowCompact
    OrganicClickableSurface(
        onClick = onClick,
        colors = OrganicButtonColors(
            container = if (selected) colors.surface else Color.Transparent,
            content = colors.text,
            hoverContainer = if (selected) colors.surface else colors.hoverSubtle,
            pressedContainer = if (selected) colors.surface else colors.hover,
        ),
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (selected) Modifier.elevationSm(shape, OrganicTheme.elevation) else Modifier),
        contentPadding = PaddingValuesOf(horizontal = 14.dp, vertical = 11.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(
                initials = client.getShortName(),
                size = 38.dp,
                seed = client.getFullName(),
            )
            OrganicText(
                text = client.getFullName(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private const val PaneBackgroundAlpha = 0.45f

@Preview
@Composable
private fun ClientsListPanePreview() {
    PreviewSurface(width = ClientsListPaneWidth) {
        Box(Modifier.fillMaxWidth()) {
            ClientsListPane(
                state = ClientsListScreenState(
                    isLoading = false,
                    clientsCount = 4,
                    selectedClientId = 1L,
                    clients = listOf(
                        ClientListItem.LetterDivider("А"),
                        ClientListItem.Client(BaseClient(id = 1, name = "Анна", surname = "Ковалёва")),
                        ClientListItem.LetterDivider("Д"),
                        ClientListItem.Client(BaseClient(id = 2, name = "Дмитрий", surname = "Лис")),
                        ClientListItem.LetterDivider("И"),
                        ClientListItem.Client(BaseClient(id = 3, name = "Ирина", surname = "Мороз")),
                        ClientListItem.LetterDivider("М"),
                        ClientListItem.Client(BaseClient(id = 4, name = "Мария", surname = "Сак")),
                    ),
                ),
                onAction = {},
            )
        }
    }
}
