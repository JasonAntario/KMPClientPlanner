package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationRail
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationRailItem
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientAmount
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsPaneContent
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientListItem
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsListPane
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsListPaneWidth
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsListScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.main.empty.NoClientsScreen
import com.dsankovsky.kmpclientplanner.ui.screens.service_type_selection.ServiceTypeSelectionScreen
import com.dsankovsky.kmpclientplanner.ui.screens.services.HomeScreenContent
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenItem
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsScreenContent
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.StatisticsScreenContent
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.StatisticsScreenState
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.model.StatisticsClientItem
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Экраны в десктопном кадре 1360×860. По PNG видно то, чего не показывает компиляция:
 * переносы подписей, ширины колонок, воздух между блоками.
 *
 * Экраны, которым нужен Koin (формы, детали), сюда не попадают — офскрин-сцена
 * без графа зависимостей их не поднимет.
 */
class OrganicScreensRenderTest {

    @Test
    fun `renders screen 01 service type selection`() {
        val image = renderScreen("organic-screen-01.png") {
            ServiceTypeSelectionScreen(
                onServiceTypeClicked = {},
                initialSelection = ServiceType.EDUCATION,
            )
        }
        assertHasCards(image)
    }

    /**
     * То же самое в широком окне: карточки категорий пропадали именно при растягивании
     * (`FlowRow` + `height(IntrinsicSize.Max)` давал нулевую высоту), а в кадре 1360
     * это выглядело как «просто пустой экран».
     */
    @Test
    fun `renders screen 01 in a wide window`() {
        val image = renderScreen("organic-screen-01-wide.png", frameWidth = 1920) {
            ServiceTypeSelectionScreen(
                onServiceTypeClicked = {},
                initialSelection = ServiceType.EDUCATION,
            )
        }
        assertHasCards(image)
    }

    /**
     * Карточки — единственное на экране 01, что залито `surface` и `accent-100`
     * (выбранная), так что их площадь и есть признак, что сетка не схлопнулась.
     */
    private fun assertHasCards(image: Image) {
        val bitmap = requireNotNull(Bitmap.makeFromImage(image))
        val colors = OrganicColors()
        val cardColors = setOf(colors.surface.toArgb(), colors.accentRamp.s100.toArgb())
        var cardPixels = 0
        // Шаг 8 px: считаем площадь, точность до пикселя тут не нужна.
        for (x in 0 until bitmap.width step 8) {
            for (y in 0 until bitmap.height step 8) {
                if (bitmap.getColor(x, y) in cardColors) cardPixels++
            }
        }
        assertTrue(cardPixels > 500, "карточки категорий не отрисовались: $cardPixels")
    }

    @Test
    fun `renders screen 02 no clients`() {
        renderScreen("organic-screen-02.png", withRail = true) {
            NoClientsScreen(onAddClientCLicked = {}, onChangeServiceTypeCLicked = {})
        }
    }

    @Test
    fun `renders screen 03 no services`() {
        renderScreen("organic-screen-03.png", withRail = true) {
            HomeScreenContent(state = ServicesListScreenState(isLoading = false), onAction = {})
        }
    }

    @Test
    fun `renders screen 09 statistics`() {
        renderScreen("organic-screen-09.png", withRail = true) {
            StatisticsScreenContent(state = statisticsState(), onAction = {})
        }
    }

    @Test
    fun `renders screen 10 settings`() {
        renderScreen("organic-screen-10.png", withRail = true) {
            SettingsScreenContent(screenState = SettingsScreenState(), onAction = {})
        }
    }

    @Test
    fun `renders screen 04 services feed`() {
        renderScreen("organic-screen-04.png", withRail = true) {
            HomeScreenContent(state = feedState(), onAction = {})
        }
    }

    /**
     * Экран 08 собран из панелей вручную: `ListDetailPaneScaffold` в офскрин-сцене
     * не поднять (обеим панелям нужен Koin), а проверять надо именно раскладку 404 + детали.
     */
    @Test
    fun `renders screen 08 clients`() {
        renderScreen("organic-screen-08.png", withRail = true) {
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.width(ClientsListPaneWidth).fillMaxHeight()) {
                    ClientsListPane(state = clientsListState(), onAction = {})
                }
                Box(Modifier.weight(1f).fillMaxHeight()) {
                    ClientDetailsPaneContent(state = clientDetailsState(), onAction = {})
                }
            }
        }
    }

    /**
     * Те же экраны в узком окне. Заголовки и строки ломались именно при сужении: рядом
     * с текстом стоят несжимаемые контролы, а текстовая колонка идёт через `weight(1f)`,
     * поэтому она сжималась в ноль и «Занятия» переносилось по буквам в вертикальную полоску.
     * Кадр 720 — окно с рейлом, при котором контенту остаётся меньше 500.
     */
    @Test
    fun `renders narrow window layouts`() {
        renderScreen("organic-narrow-04.png", withRail = true, frameWidth = 720) {
            HomeScreenContent(state = feedState(), onAction = {})
        }
        renderScreen("organic-narrow-09.png", withRail = true, frameWidth = 720) {
            StatisticsScreenContent(state = statisticsState(), onAction = {})
        }
        renderScreen("organic-narrow-10.png", withRail = true, frameWidth = 720) {
            SettingsScreenContent(screenState = SettingsScreenState(), onAction = {})
        }
    }

    private fun renderScreen(
        fileName: String,
        withRail: Boolean = false,
        frameWidth: Int = FrameWidth,
        content: @Composable () -> Unit,
    ): Image {
        val scene = ImageComposeScene(
            width = frameWidth * 2,
            height = FrameHeight * 2,
            density = Density(2f),
        ) {
            OrganicTheme {
                Row(Modifier.fillMaxSize().background(OrganicTheme.colors.bg)) {
                    if (withRail) DemoRail()
                    Box(Modifier.fillMaxSize()) { content() }
                }
            }
        }
        var image = scene.render()
        repeat(20) {
            Thread.sleep(50)
            image = scene.render()
        }
        scene.close()

        val file = File("build/design/$fileName")
        file.parentFile.mkdirs()
        file.writeBytes(
            checkNotNull(image.encodeToData(EncodedImageFormat.PNG)) { "не удалось закодировать PNG" }.bytes,
        )
        assertTrue(file.length() > 0)
        return image
    }
}

private const val FrameWidth = 1360
private const val FrameHeight = 860

@Composable
private fun DemoRail() {
    Column(Modifier.fillMaxHeight()) {
        OrganicNavigationRail(brand = "Куфар Блокнот", footer = "Репетитор · v1.0.0") {
            OrganicNavigationRailItem(OrganicIcons.Home, "Главная", selected = true, onClick = {})
            OrganicNavigationRailItem(OrganicIcons.Users, "Клиенты", selected = false, onClick = {})
            OrganicNavigationRailItem(
                OrganicIcons.BarChart,
                "Статистика",
                selected = false,
                onClick = {})
            OrganicNavigationRailItem(
                OrganicIcons.Settings,
                "Настройки",
                selected = false,
                onClick = {})
        }
    }
}

private fun feedState(): ServicesListScreenState {
    val today = getCurrentDateTime()
    fun item(
        hour: Int,
        name: String,
        surname: String,
        title: String,
        price: Float,
        address: String,
        currency: CurrencyItem = CurrencyItem.BYN,
        paid: Boolean = false,
        done: Boolean = false,
        dayShift: Int = 0,
    ) = ServicesListScreenItem.ServiceItem(
        title = title,
        client = BaseClient(name = name, surname = surname),
        startDate = today.date.plusDaysAt(dayShift, hour, 0),
        endDate = today.date.plusDaysAt(dayShift, hour + 1, 0),
        isPaid = paid,
        isFinished = done,
        service = BaseService(price = price, address = address, currency = currency),
    )

    return ServicesListScreenState(
        isLoading = false,
        items = listOf(
            ServicesListScreenItem.DateDivider(today.date),
            item(9, "Мария", "Сак", "Английский", 40f, "онлайн", paid = true, done = true),
            item(11, "Олег", "Тарасов", "Математика", 60f, "Немига 12", paid = true, done = true),
            item(15, "Анна", "Ковалёва", "Английский", 40f, "онлайн"),
            item(17, "Ирина", "Мороз", "Немецкий", 25f, "онлайн", currency = CurrencyItem.USD),
            ServicesListScreenItem.DateDivider(today.date.plusDays(1)),
            item(10, "Полина", "Юркевич", "Английский", 40f, "онлайн", dayShift = 1),
        ),
    )
}

private fun kotlinx.datetime.LocalDate.plusDays(days: Int): kotlinx.datetime.LocalDate =
    kotlinx.datetime.LocalDate.fromEpochDays(toEpochDays() + days)

private fun kotlinx.datetime.LocalDate.plusDaysAt(
    days: Int,
    hour: Int,
    minute: Int,
): kotlinx.datetime.LocalDateTime =
    kotlinx.datetime.LocalDateTime(plusDays(days), kotlinx.datetime.LocalTime(hour, minute))

private fun statisticsState() = StatisticsScreenState(
    isLoading = false,
    paidPercentage = 0.78f,
    servicesTotal = 34,
    servicesPaid = 26,
    servicesUnpaid = 8,
    clientsWithDebt = 4,
    receivedTotalByCurrency = listOf(
        StatisticsClientItem.StatisticsPaymentItem(1240f, CurrencyItem.BYN),
        StatisticsClientItem.StatisticsPaymentItem(275f, CurrencyItem.USD),
    ),
    expectedTotalByCurrency = listOf(
        StatisticsClientItem.StatisticsPaymentItem(320f, CurrencyItem.BYN),
    ),
    itemsByClients = listOf(
        client("Ирина", "Мороз", 10, 0f, 250f),
        client("Олег", "Тарасов", 8, 480f, 0f),
        client("Дмитрий", "Лис", 6, 360f, 0f),
        client("Анна", "Ковалёва", 7, 280f, 0f),
        client("Мария", "Сак", 3, 120f, 25f),
    ),
)

private fun clientsListState(): ClientsListScreenState {
    val clients = listOf(
        BaseClient(id = 1, name = "Анна", surname = "Ковалёва"),
        BaseClient(id = 2, name = "Дмитрий", surname = "Лис"),
        BaseClient(id = 3, name = "Ирина", surname = "Мороз"),
        BaseClient(id = 4, name = "Мария", surname = "Сак"),
        BaseClient(id = 5, name = "Олег", surname = "Тарасов"),
        BaseClient(id = 6, name = "Полина", surname = "Юркевич"),
    )
    return ClientsListScreenState(
        isLoading = false,
        clientsCount = clients.size,
        selectedClientId = 1L,
        clients = clients.flatMap {
            listOf(
                ClientListItem.LetterDivider(it.name.take(1).uppercase()),
                ClientListItem.Client(it),
            )
        },
    )
}

private fun clientDetailsState() = ClientDetailsScreenState(
    isLoading = false,
    clientName = "Анна Ковалёва",
    clientShortName = "АК",
    phone = "+375 29 123-45-67",
    comment = "Готовится к экзамену в декабре. Домашние задания просит присылать в Telegram.",
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
        lessonDateTimeList = listOf(
            ServiceDateTime(dayOfWeek = kotlinx.datetime.DayOfWeek.MONDAY),
            ServiceDateTime(dayOfWeek = kotlinx.datetime.DayOfWeek.WEDNESDAY),
        ),
    ),
    unpaidTotals = listOf(ClientAmount(40f, CurrencyItem.BYN)),
    prepaidCount = 2,
    servicesCount = 12,
    firstServiceDate = kotlinx.datetime.LocalDate(2026, 3, 12),
    showServicesHistory = true,
)

private fun client(name: String, surname: String, paid: Int, byn: Float, usd: Float) =
    StatisticsClientItem(
        client = BaseClient(name = name, surname = surname),
        income = listOf(
            StatisticsClientItem.StatisticsPaymentItem(byn, CurrencyItem.BYN),
            StatisticsClientItem.StatisticsPaymentItem(usd, CurrencyItem.USD),
        ),
        mustBePaid = emptyList(),
        paidServicesCount = paid,
    )
