package com.dsankovsky.kmpclientplanner.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationRail
import com.dsankovsky.kmpclientplanner.ui.design.components.OrganicNavigationRailItem
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
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
import org.jetbrains.skia.EncodedImageFormat
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
        renderScreen("organic-screen-01.png") {
            ServiceTypeSelectionScreen(
                onServiceTypeClicked = {},
                initialSelection = ServiceType.EDUCATION,
            )
        }
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

    private fun renderScreen(
        fileName: String,
        withRail: Boolean = false,
        content: @Composable () -> Unit,
    ) {
        val scene = ImageComposeScene(
            width = FrameWidth * 2,
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
