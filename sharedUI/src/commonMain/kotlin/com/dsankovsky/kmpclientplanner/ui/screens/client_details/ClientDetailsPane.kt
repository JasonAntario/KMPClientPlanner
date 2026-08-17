package com.dsankovsky.kmpclientplanner.ui.screens.client_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.ui.design.OrganicTheme
import com.dsankovsky.kmpclientplanner.ui.design.components.Avatar
import com.dsankovsky.kmpclientplanner.ui.design.components.ConfirmModal
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicButtonDefaults
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicCard
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicIconButton
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalHost
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicModalPanel
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicText
import com.dsankovsky.kmpclientplanner.ui.design.components.Tag
import com.dsankovsky.kmpclientplanner.ui.design.components.TagDefaults
import com.dsankovsky.kmpclientplanner.ui.design.elevationSm
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.extensions.collectWithLifecycle
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIDayAndMonth
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIMoney
import com.dsankovsky.kmpclientplanner.ui.extensions.toUIShortName
import com.dsankovsky.kmpclientplanner.ui.extensions.toUITime
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.ClientScreenDialog
import com.dsankovsky.kmpclientplanner.ui.screens.loading.LoadingScreen
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.autofill_prolong_description
import kmpclientplanner.sharedui.generated.resources.client_comment
import kmpclientplanner.sharedui.generated.resources.client_currency
import kmpclientplanner.sharedui.generated.resources.client_details_all_services
import kmpclientplanner.sharedui.generated.resources.client_details_autofill_title
import kmpclientplanner.sharedui.generated.resources.client_details_close
import kmpclientplanner.sharedui.generated.resources.client_details_contacts
import kmpclientplanner.sharedui.generated.resources.client_details_crossing_title
import kmpclientplanner.sharedui.generated.resources.client_details_delete
import kmpclientplanner.sharedui.generated.resources.client_details_delete_confirm
import kmpclientplanner.sharedui.generated.resources.client_details_delete_text
import kmpclientplanner.sharedui.generated.resources.client_details_delete_title
import kmpclientplanner.sharedui.generated.resources.client_details_edit
import kmpclientplanner.sharedui.generated.resources.client_details_empty_selection
import kmpclientplanner.sharedui.generated.resources.client_details_fill_lessons
import kmpclientplanner.sharedui.generated.resources.client_details_fill_trainings
import kmpclientplanner.sharedui.generated.resources.client_details_format
import kmpclientplanner.sharedui.generated.resources.client_details_level_value
import kmpclientplanner.sharedui.generated.resources.client_details_prepaid
import kmpclientplanner.sharedui.generated.resources.client_details_price
import kmpclientplanner.sharedui.generated.resources.client_details_since
import kmpclientplanner.sharedui.generated.resources.client_details_unpaid
import kmpclientplanner.sharedui.generated.resources.client_details_weight_value
import kmpclientplanner.sharedui.generated.resources.client_offline
import kmpclientplanner.sharedui.generated.resources.client_online
import kmpclientplanner.sharedui.generated.resources.client_phone
import kmpclientplanner.sharedui.generated.resources.client_shoud_continue_autofill
import kmpclientplanner.sharedui.generated.resources.confirm
import kmpclientplanner.sharedui.generated.resources.service_crossing
import kmpclientplanner.sharedui.generated.resources.statistics_empty_value
import kmpclientplanner.sharedui.generated.resources.statistics_prepay
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Правая панель экрана 08 — карточка клиента.
 *
 * Раньше это был отдельный destination со своим тулбаром; теперь панель master-detail.
 * Крестик [onClose] снимает выделение: на широком окне справа остаётся заглушка,
 * на узком возвращается список.
 */
@Composable
fun ClientDetailsPane(
    clientId: Long,
    onEditClient: () -> Unit,
    onPrepayClient: () -> Unit,
    onOpenServicesHistory: () -> Unit,
    onAutofillCompleted: () -> Unit,
    onClientDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
) {
    val viewModel: ClientDetailsViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.collectWithLifecycle { event ->
        when (event) {
            ClientDetailsEvents.OpenEditClientScreen -> onEditClient()
            ClientDetailsEvents.OpenPrepay -> onPrepayClient()
            ClientDetailsEvents.OpenServicesHistory -> onOpenServicesHistory()
            ClientDetailsEvents.AutofillCompleted -> onAutofillCompleted()
            ClientDetailsEvents.ClientDeleted -> onClientDeleted()
            ClientDetailsEvents.OnCloseScreen -> onClose?.invoke()
        }
    }

    LaunchedEffect(clientId) {
        viewModel.handleActions(ClientDetailsActions.LoadData(clientId))
    }

    Box(modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingScreen()
            else -> ClientDetailsPaneContent(
                state = state,
                onAction = viewModel::handleActions,
                onClose = onClose,
            )
        }
        ClientDetailsDialogs(state = state, onAction = viewModel::handleActions)
    }
}

@Composable
fun ClientDetailsPaneContent(
    state: ClientDetailsScreenState,
    onAction: (ClientDetailsActions) -> Unit,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrganicTheme.colors.bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 36.dp, vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        ClientHeader(state = state, onAction = onAction, onClose = onClose)
        MetricsRow(state)
        ContactsBlock(state = state, onAction = onAction)
    }
}

@Composable
private fun ClientHeader(
    state: ClientDetailsScreenState,
    onAction: (ClientDetailsActions) -> Unit,
    onClose: (() -> Unit)?,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Avatar(
            initials = state.clientShortName,
            size = 76.dp,
            fontSize = 28.sp,
            seed = state.clientName,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space1),
        ) {
            OrganicText(
                text = state.clientName,
                style = OrganicTheme.typography.h2.copy(fontSize = 30.sp, lineHeight = 34.sp),
            )
            clientSubtitle(state)?.let { subtitle ->
                OrganicText(
                    text = subtitle,
                    style = OrganicTheme.typography.bodySm,
                    color = OrganicTheme.colors.muted,
                )
            }
            ClientTags(state)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2)) {
            OrganicIconButton(
                icon = OrganicIcons.Pencil,
                onClick = { onAction(ClientDetailsActions.OnEditClientClicked) },
                contentDescription = stringResource(Res.string.client_details_edit),
            )
            OrganicIconButton(
                icon = OrganicIcons.Trash,
                onClick = { onAction(ClientDetailsActions.OnDeleteClientClicked) },
                contentDescription = stringResource(Res.string.client_details_delete),
            )
            if (onClose != null) {
                OrganicIconButton(
                    icon = OrganicIcons.X,
                    onClick = onClose,
                    contentDescription = stringResource(Res.string.client_details_close),
                )
            }
        }
    }
}

/** Теги под именем: предоплаченные занятия и расписание. */
@Composable
private fun ClientTags(state: ClientDetailsScreenState) {
    val schedule = state.clientSpecificFields.schedule
    if (state.prepaidCount == 0 && schedule.isEmpty()) return

    Row(
        modifier = Modifier.padding(top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2),
    ) {
        if (state.prepaidCount > 0) {
            Tag(
                text = pluralStringResource(
                    Res.plurals.client_details_prepaid,
                    state.prepaidCount,
                    state.prepaidCount,
                ),
                colors = TagDefaults.accent2(),
            )
        }
        scheduleLabel(schedule)?.let { Tag(text = it, colors = TagDefaults.neutral()) }
    }
}

/**
 * Две карточки-метрики шириной не больше 520: долг и цена занятия.
 *
 * На узкой панели (medium-окно, где рейл ещё виден, а панель уже одна) они встают
 * колонкой — иначе сумма в 24 не влезает и рвётся по цифрам.
 */
@Composable
private fun MetricsRow(state: ClientDetailsScreenState) {
    val unpaid: @Composable (Modifier) -> Unit = { cardModifier ->
        MetricCard(
            kicker = stringResource(Res.string.client_details_unpaid),
            amounts = state.unpaidTotals.ifEmpty {
                listOf(ClientAmount(0f, state.client.currency))
            },
            accent = state.unpaidTotals.isNotEmpty(),
            modifier = cardModifier,
        )
    }
    val price: @Composable (Modifier) -> Unit = { cardModifier ->
        MetricCard(
            kicker = stringResource(Res.string.client_details_price),
            amounts = state.client.price?.let { listOf(ClientAmount(it, state.client.currency)) }
                ?: emptyList(),
            accent = false,
            modifier = cardModifier,
        )
    }

    BoxWithConstraints(Modifier.widthIn(max = ContentMaxWidth)) {
        if (maxWidth >= MetricsRowMinWidth) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                unpaid(Modifier.weight(1f).fillMaxHeight())
                price(Modifier.weight(1f).fillMaxHeight())
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space3)) {
                unpaid(Modifier.fillMaxWidth())
                price(Modifier.fillMaxWidth())
            }
        }
    }
}

/** Ниже этой ширины две суммы в 24 рядом не читаются. */
private val MetricsRowMinWidth = 420.dp

@Composable
private fun MetricCard(
    kicker: String,
    amounts: List<ClientAmount>,
    accent: Boolean,
    modifier: Modifier = Modifier,
) {
    OrganicCard(
        modifier = modifier.elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
        kicker = kicker,
        verticalGap = OrganicTheme.spacing.space1,
    ) {
        if (amounts.isEmpty()) {
            OrganicText(
                text = stringResource(Res.string.statistics_empty_value),
                style = MetricStyle(),
                color = OrganicTheme.colors.muted,
            )
        } else {
            amounts.forEachIndexed { index, amount ->
                OrganicText(
                    text = amount.money.toUIMoney(amount.currency),
                    style = if (index == 0) MetricStyle() else OrganicTheme.typography.bodySm,
                    color = when {
                        index > 0 -> OrganicTheme.colors.muted
                        accent -> OrganicTheme.colors.accentText
                        else -> OrganicTheme.colors.text
                    },
                )
            }
        }
    }
}

@Composable
private fun MetricStyle() =
    OrganicTheme.typography.numeric.copy(fontSize = 24.sp, lineHeight = 28.sp)

/** Карточка «Контакты» с комментарием и кнопки под ней. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContactsBlock(
    state: ClientDetailsScreenState,
    onAction: (ClientDetailsActions) -> Unit,
) {
    Column(
        modifier = Modifier.widthIn(max = ContentMaxWidth),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        OrganicCard(
            modifier = Modifier
                .fillMaxWidth()
                .elevationSm(OrganicTheme.shapes.card, OrganicTheme.elevation),
            kicker = stringResource(Res.string.client_details_contacts),
            verticalGap = 10.dp,
        ) {
            state.phone?.takeIf { it.isNotBlank() }?.let {
                ContactRow(stringResource(Res.string.client_phone), it)
            }
            clientFormat(state)?.let {
                ContactRow(stringResource(Res.string.client_details_format), it)
            }
            ContactRow(
                label = stringResource(Res.string.client_currency),
                value = state.client.currency.code,
            )

            state.comment?.takeIf { it.isNotBlank() }?.let { comment ->
                OrganicText(
                    text = stringResource(Res.string.client_comment).uppercase(),
                    style = OrganicTheme.typography.kicker,
                    color = OrganicTheme.colors.accent,
                    modifier = Modifier.padding(top = OrganicTheme.spacing.space2),
                )
                OrganicText(text = comment, style = OrganicTheme.typography.bodySm)
            }
        }

        // Кнопок бывает три, и в 520 dp они рядом не встают — лишние переносим на строку ниже.
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2),
            verticalArrangement = Arrangement.spacedBy(OrganicTheme.spacing.space2),
        ) {
            // Та же М5, что и со статистики, только клиент уже выбран и не меняется.
            if (state.showPrepay) {
                OrganicButton(
                    text = stringResource(Res.string.statistics_prepay),
                    onClick = { onAction(ClientDetailsActions.OnPrepayClicked) },
                    icon = OrganicIcons.Wallet,
                )
            }
            if (state.showServicesHistory) {
                OrganicButton(
                    text = stringResource(Res.string.client_details_all_services),
                    onClick = { onAction(ClientDetailsActions.ShowServicesHistory) },
                    colors = OrganicButtonDefaults.secondary(),
                )
            }
            // Автозаполнения в макете 08 нет, но у расписания это единственная точка входа:
            // по макету занятия на 4 недели предлагаются в М7 сразу после сохранения клиента.
            autofillLabel(state)?.let { label ->
                OrganicButton(
                    text = label,
                    onClick = { onAction(ClientDetailsActions.FillServicesClicked) },
                    colors = OrganicButtonDefaults.secondary(),
                )
            }
        }
    }
}

@Composable
private fun ContactRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OrganicText(
            text = label,
            style = OrganicTheme.typography.bodySm,
            color = OrganicTheme.colors.muted,
            modifier = Modifier.weight(1f),
        )
        OrganicText(text = value, style = OrganicTheme.typography.bodySm)
    }
}

/** Диалоги карточки: подтверждение удаления (М8), продление занятий и пересечение (М6/М7). */
@Composable
private fun ClientDetailsDialogs(
    state: ClientDetailsScreenState,
    onAction: (ClientDetailsActions) -> Unit,
) {
    val dialog = state.showDialog ?: return
    val onDismiss = {
        when (dialog) {
            ClientScreenDialog.ConfirmAutofillServices ->
                onAction(ClientDetailsActions.OnAutofillDismissClicked)

            else -> onAction(ClientDetailsActions.CloseClientDialog)
        }
    }

    OrganicModalHost(onDismissRequest = onDismiss) {
        when (dialog) {
            ClientScreenDialog.ConfirmClientDeleting -> ConfirmModal(
                title = stringResource(Res.string.client_details_delete_title),
                text = stringResource(
                    Res.string.client_details_delete_text,
                    state.clientName,
                    state.servicesCount,
                ),
                confirmText = stringResource(Res.string.client_details_delete_confirm),
                onConfirm = { onAction(ClientDetailsActions.OnDeleteClientConfirmed) },
                onDismiss = onDismiss,
                destructive = true,
            )

            ClientScreenDialog.ConfirmAutofillServices -> ConfirmModal(
                title = stringResource(Res.string.client_details_autofill_title),
                text = stringResource(Res.string.autofill_prolong_description),
                confirmText = stringResource(Res.string.confirm),
                onConfirm = { onAction(ClientDetailsActions.OnAutofillConfirmClicked) },
                onDismiss = onDismiss,
            )

            is ClientScreenDialog.ServicesCrossing -> ConfirmModal(
                title = stringResource(Res.string.client_details_crossing_title),
                text = stringResource(Res.string.service_crossing),
                confirmText = stringResource(Res.string.client_shoud_continue_autofill),
                onConfirm = {
                    onAction(ClientDetailsActions.OnAutofillWithCrossingConfirmClicked)
                },
                onDismiss = onDismiss,
                extraContent = {
                    OrganicModalPanel {
                        dialog.services.forEach { service ->
                            ContactRow(service.title, service.getServiceTime())
                        }
                    }
                },
            )

            // М9 принадлежит форме: в карточке нечего терять несохранённым.
            ClientScreenDialog.ConfirmDiscard -> Unit
        }
    }
}

/** «Английский, уровень B1 · клиент с 12 марта». */
@Composable
private fun clientSubtitle(state: ClientDetailsScreenState): String? {
    val head = buildList {
        state.client.serviceSubtype?.takeIf { it.isNotBlank() }?.let(::add)
        when (val fields = state.clientSpecificFields) {
            is ClientSpecificFields.EducationClientSpecificFields ->
                fields.level?.takeIf { it.isNotBlank() }?.let {
                    add(stringResource(Res.string.client_details_level_value, it))
                }

            is ClientSpecificFields.SportClientSpecificFields ->
                fields.weight?.takeIf { it.isNotBlank() }?.let {
                    add(stringResource(Res.string.client_details_weight_value, it))
                }

            else -> {}
        }
    }.joinToString(", ")

    val since = state.firstServiceDate?.let {
        stringResource(Res.string.client_details_since, it.toUIDayAndMonth())
    }

    return listOfNotNull(head.takeIf { it.isNotBlank() }, since)
        .joinToString(" · ")
        .takeIf { it.isNotBlank() }
}

/** «Онлайн» или адрес — то, что в макете стоит в строке «Формат». */
@Composable
private fun clientFormat(state: ClientDetailsScreenState): String? {
    val online = state.clientSpecificFields.isOnline
    return when {
        online == true -> stringResource(Res.string.client_online)
        !state.address.isNullOrBlank() -> state.address
        online == false -> stringResource(Res.string.client_offline)
        else -> null
    }
}

/** «Пн, Ср 15:00» — дни расписания и время первого занятия. */
@Composable
private fun scheduleLabel(schedule: List<ServiceDateTime>): String? {
    if (schedule.isEmpty()) return null
    // `joinToString` не inline, поэтому подписи дней собираем заранее — внутри его лямбды
    // composable-вызов сделать нельзя.
    val days = schedule.map { it.dayOfWeek }.distinct().map { it.toUIShortName() }
    return "${days.joinToString(", ")} ${schedule.first().time.toUITime()}"
}

@Composable
private fun autofillLabel(state: ClientDetailsScreenState): String? =
    when {
        state.clientSpecificFields.schedule.isEmpty() -> null
        state.client.serviceType == ServiceType.SPORT ->
            stringResource(Res.string.client_details_fill_trainings)

        state.client.serviceType == ServiceType.EDUCATION ->
            stringResource(Res.string.client_details_fill_lessons)

        else -> null
    }

private val ClientSpecificFields?.schedule: List<ServiceDateTime>
    get() = when (this) {
        is ClientSpecificFields.EducationClientSpecificFields -> lessonDateTimeList
        is ClientSpecificFields.SportClientSpecificFields -> lessonDateTimeList
        else -> emptyList()
    }

private val ClientSpecificFields?.isOnline: Boolean?
    get() = when (this) {
        is ClientSpecificFields.EducationClientSpecificFields -> isOnline
        is ClientSpecificFields.SportClientSpecificFields -> isOnline
        else -> null
    }

/** Карточки в макете не шире 520 — иначе строки «подпись … значение» разъезжаются. */
private val ContentMaxWidth = 520.dp

@Composable
fun ClientDetailsEmptyPane(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(OrganicTheme.colors.bg),
        contentAlignment = Alignment.Center,
    ) {
        OrganicText(
            text = stringResource(Res.string.client_details_empty_selection),
            style = OrganicTheme.typography.body,
            color = OrganicTheme.colors.muted,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun ClientDetailsPaneContentPreview() {
    OrganicTheme {
        ClientDetailsPaneContent(
            state = ClientDetailsScreenState(
                isLoading = false,
                clientName = "Анна Ковалёва",
                clientShortName = "АК",
                phone = "+375 29 123-45-67",
                comment = "Готовится к экзамену в декабре. Домашние задания просит в Telegram.",
                client = BaseClient(
                    name = "Анна",
                    surname = "Ковалёва",
                    price = 40f,
                    currency = CurrencyItem.BYN,
                    serviceType = ServiceType.EDUCATION,
                    serviceSubtype = "Английский",
                ),
                clientSpecificFields = ClientSpecificFields.EducationClientSpecificFields(
                    level = "B1",
                    isOnline = true,
                    lessonDateTimeList = listOf(ServiceDateTime(), ServiceDateTime()),
                ),
                unpaidTotals = listOf(ClientAmount(40f, CurrencyItem.BYN)),
                prepaidCount = 2,
                servicesCount = 12,
                showServicesHistory = true,
                showPrepay = true,
            ),
            onAction = {},
        )
    }
}
