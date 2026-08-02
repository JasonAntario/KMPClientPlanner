# План миграции на дизайн-систему Organic (desktop-first, KMP/CMP)

Источник истины по дизайну: `design-handoff/README.md` + `_ds/organic-*/styles.css` + `_ds/organic-*/readme.md`.
HTML-референс (`Куфар Блокнот - Desktop MVP.dc.html`) — только для сверки, код из него не переносим.

---

## 0. Исходная точка и объём работ

Что уже есть и **остаётся без изменений**:

| Слой | Состояние |
|---|---|
| `sharedLogic` (domain, data, Room, DataStore, DI) | Готов, переиспользуется полностью |
| ViewModel'и + `*Action` / `*State` / `*Event` (MVI, bi-directional) | Готовы, переиспользуются с точечными доработками (§7) |
| Навигация: Navigation3 (`NavDisplay`, `Screen: NavKey`, backstack в `MainScreen`) | Каркас остаётся, меняется состав destination'ов (часть уходит в модалки) |
| Koin + `koinViewModel()` | Без изменений |

Что **переписывается целиком** — весь view-слой `sharedUI/ui/**`:
- тема (`ui/theme/Theme.kt`, `Color.kt`) — сейчас это сгенерированный M3-scheme в зелёной палитре, к Organic отношения не имеет;
- 10 экранов + 12 модальных окон;
- все 8 компонентов из `ui/components` (`CardView`, `SelectorView`, `DropDownMenuView`, `ToolbarView`, `HeaderView`, `ShortNameBoxView`, `DateTimeViewWithPicker`, `ServiceDateTimeSelectorView`).

Ключевой вывод: **это миграция только UI-слоя.** Данные, use-case'ы и логика VM почти полностью готовы; основная работа — построить DS-слой и переложить на него экраны.

### Почему не «просто перекрасить MaterialTheme»

Organic не выражается через `M3 ColorScheme`:
- две смысловые рампы по 9 шагов (`accent-*`, `accent-2-*`) + нейтральная — M3 роли (`primary/secondary/tertiary`) их не покрывают;
- семантика статусов — тройка fill/text/border из разных шагов рампы;
- обязательные pill-радиусы (999) на кнопках/инпутах/тегах и 32 на карточках/диалогах;
- «муть» текста задана как `text @55% / 70%`, разделители — `text @16% / 8%`, hover — `text @4% / 7%`, pressed — `@14%`;
- CSS-тени (`0 3px 10px @16%`) не равны M3-elevation.

Поэтому строим **свой слой токенов** поверх Compose Foundation, а `material3` оставляем в зависимостях только как «двигатель» для нескольких сервисных штук (DatePicker/TimePicker, `DropdownMenu`/`Popup`, `SnackbarHostState`) — но их визуал переопределяем токенами.

---

## 1. Этап 1 — токены и тема (`ui/design`)

Новый пакет `sharedUI/src/commonMain/kotlin/com/dsankovsky/kmpclientplanner/ui/design/`:

```
design/
  OrganicTheme.kt        // CompositionLocal'ы + @Composable OrganicTheme(content)
  OrganicColors.kt       // роли + рампы + семантика статусов + состояния
  OrganicSpacing.kt      // 4.4 / 8.8 / 13.2 / 17.6 / 26.4 / 35.2 dp
  OrganicShapes.kt       // sm 8, md 16, lg 28, card 32, pill 999
  OrganicTypography.kt   // Caprasimo / Figtree + шкала
  OrganicElevation.kt    // shadow sm/md/lg через Modifier.dropShadow
```

`1 CSS px = 1 dp` (макет свёрстан под окно 1360 px, десктоп рисует в dp 1:1). Дробные значения плотности 1.10× кладём как есть — `13.2.dp` валиден.

```kotlin
@Immutable
data class OrganicColors(
    val bg: Color = Color(0xFFF5EAD8),
    val surface: Color = Color(0xFFEBDDC5),
    val text: Color = Color(0xFF201E1D),
    val accent: Color = Color(0xFFC67139),
    val accent2: Color = Color(0xFF7A8A5E),
    val accentRamp: ColorRamp = ColorRamp(/* 100..900 из styles.css */),
    val accent2Ramp: ColorRamp = ColorRamp(/* ... */),
    val neutralRamp: ColorRamp = ColorRamp(/* ... */),
) {
    val divider get() = text.copy(alpha = 0.16f)
    val rowLine get() = text.copy(alpha = 0.08f)
    val muted   get() = text.copy(alpha = 0.55f)   // .text-muted, figcaption
    val label   get() = text.copy(alpha = 0.70f)   // подписи полей
    val hoverSubtle get() = text.copy(alpha = 0.04f)  // hover строки таблицы
    val hover   get() = text.copy(alpha = 0.07f)   // hover secondary
    val pressed get() = text.copy(alpha = 0.14f)
    val scrim   get() = neutralRamp.s900.copy(alpha = 0.50f)
}

@Immutable
data class StatusColors(val fill: Color, val content: Color, val border: Color)

val OrganicColors.positive get() = StatusColors(accent2Ramp.s100, accent2Ramp.s800, accent2Ramp.s400)
val OrganicColors.negative get() = StatusColors(accentRamp.s100,  accentRamp.s800,  accentRamp.s400)
val OrganicColors.destructive get() = StatusColors(accentRamp.s700, bg, accentRamp.s700)
```

`OrganicTheme` делает три вещи:
1. кладёт `LocalOrganicColors / Spacing / Shapes / Typography / Elevation`;
2. ставит `LocalContentColor = colors.text`, `LocalTextStyle = typography.body`, `LocalIndication` — своя ripple-less indication (Organic не использует M3-ripple, состояния — тонирование);
3. **временно** оборачивает контент в `MaterialTheme` с `colorScheme`, смапленным из токенов (`primary = accent`, `background = bg`, `surface = surface`, `onSurface = text`, `outline = divider`, `error = accentRamp.s700`, …) — чтобы ещё не переписанные экраны и M3-пикеры не выглядели сломанными во время миграции. После этапа 6 маппинг остаётся только для пикеров.

Тень (CSS `box-shadow` → Compose): в CMP 1.11 доступен `Modifier.dropShadow(shape, Shadow(radius, spread, offset, color))` из `androidx.compose.ui.draw` (проверено в артефакте `ui-desktop-1.11.0.jar`). Это то, что нужно: тень рисуется **вне** границ без «эффекта карточки» M3.

```kotlin
fun Modifier.elevationMd(shape: Shape) = dropShadow(
    shape = shape,
    shadow = Shadow(radius = 10.dp, offset = DpOffset(0.dp, 3.dp), color = Color(0xFF2E2B25).copy(alpha = 0.16f))
)
```
Радиус блюра подбирается визуально по референсу (CSS blur ≠ 1:1 к Compose radius). Fallback, если API окажется experimental и это неудобно — `Modifier.shadow(elevation, shape, ambientColor, spotColor)`.

**Фокус** (`2px accent, offset 2px`) — общий модификатор `Modifier.organicFocusRing(shape)` на `onFocusEvent` + `drawBehind`; вешается на все интерактивные атомы. **Disabled** — `Modifier.alpha(0.45f)` + `enabled = false`.

---

## 2. Этап 2 — шрифты и иконки

**Шрифты — сделано, но не так, как в хендоффе.** В Caprasimo и Figtree нет кириллицы
(проверено: `canDisplayUpTo("Привет") == 0` у обеих), а интерфейс русский — фирменными
остались бы только латиница и цифры. Поэтому одна гарнитура на всё — **Nunito**
(Google Fonts, OFL-1.1, кириллица родная, пластика та же скруглённая):
```
sharedUI/src/commonMain/composeResources/font/nunito_variable.ttf   // 277 КБ, ось wght 200..1000
sharedUI/licenses/OFL-Nunito.txt
```
Дисплейная роль отыгрывается весом `Black`, текстовая — 400/600/700; все веса берутся из
одного вариативного файла (`Font(...)` в Compose Resources 1.11 сам передаёт ось `wght`
из `FontWeight`):
```kotlin
@Composable fun organicFontFamily() = FontFamily(
    Font(Res.font.nunito_variable, FontWeight.Normal),
    Font(Res.font.nunito_variable, FontWeight.SemiBold),
    Font(Res.font.nunito_variable, FontWeight.Bold),
    Font(Res.font.nunito_variable, FontWeight.Black),
)
```
Шкала (`OrganicTypography`), lineHeight/letterSpacing из `styles.css`:

| Токен | Размер / lineHeight / letterSpacing | Семейство |
|---|---|---|
| `h1` | 42 (48 на экране 01) / 1.12 / −0.015em | heading |
| `h2` | 32 (34 в шапках экранов, 30 в деталях) / 1.12 / −0.015em | heading |
| `h3` | 25 / 1.12 / −0.015em | heading |
| `cardTitle` | 17 (19 в шапке рейла) / 1.2 | heading |
| `numeric` | 26 (время в `ServiceRow`), 38 (метрики статистики) | heading |
| `body` | 15 / 1.55 | body 400 |
| `bodySm` | 14 / 1.55 | body 400 |
| `label` | 12 / 1.4, цвет `label` | body 400 |
| `meta` | 11–13, цвет `muted` | body 400 |
| `kicker` | 10–11, uppercase, letterSpacing 0.08–0.1em | body 600 |

Денежные суммы — `tabular-nums`: `TextStyle(fontFeatureSettings = "tnum")`.

**Иконки — сделано.** Набор взят не из библиотеки Lucide, а из самого макета: в
`Куфар Блокнот - Desktop MVP.dc.html` иконки нарисованы инлайн-SVG (94 использования,
**23 уникальных глифа**) в стиле Lucide на сетке 24×24 со stroke-width 2.75. Контуры
перенесены дословно в `ui/design/icons/OrganicIcons.kt` как `ImageVector` (строки путей
парсятся один раз на глиф, зато сверяются с макетом построчно), плюс `OrganicIcon`
и размеры 16/18/24. Глифов, которых в макете нет (поиск, телефон, Telegram), там и не
рисуется — если экрану понадобится, добавляем в той же манере.

После миграции `compose.materialIconsExtended` из `sharedUI/build.gradle.kts` удаляем.

---

## 3. Этап 3 — атомы DS на базовых компонентах Compose

Пакет `ui/design/components/`. Всё — на `foundation`, **без** M3-обёрток (по требованию: `BasicTextField` вместо `OutlinedTextField` и т.д.).

| Компонент | Базис | Заметки |
|---|---|---|
| `OrganicButton` (primary / secondary / ghost / icon 36×36) | `Box` + `Modifier.clickable` + `indication` | pill 999; hover `accent-600`, pressed `accent-700`; текст — **heading**-семейство 14 (как в `.btn`) |
| `OrganicTextField` | **`BasicTextField`** | pill 999, `minHeight 36`, padding 6/14, `background = surface`, border `divider` → hover `text@45%` → focus `accent`; `cursorBrush = SolidColor(accent)` |
| `OrganicTextArea` | **`BasicTextField`** (multiline) | radius **16**, padding 12/14, `minHeight 90` (120 для «Домашнего задания»), фон `bg` внутри карточки |
| `FieldLabel` + `Field` | `Column` + `BasicText` | подпись 12 / `label`, отступ 5 |
| `OrganicSelect` | `OrganicTextField(readOnly)` + `Popup` + `LazyColumn` | своя реализация вместо `ExposedDropdownMenuBox`; используется для клиента/услуги/валюты/адреса |
| `SegmentedControl` | `Row` в pill-контейнере с `divider`-бордером | опции 13, padding 7/12; выбранная — `accent` + `bg`-текст |
| `StatusButton` | `OrganicButton` + `StatusColors` | 2 вида: `PaymentStatus` (иконка banknote) → Оплачено/Не оплачено; `SessionStatus` (calendar) → Проведено/Запланировано. **Вид не зависит от выделения строки** |
| `Tag` (accent / accent-2 / neutral / outline) | `Box` + `BasicText` | 11, padding 3/10, radius 12 |
| `OrganicCard` | `Column` + `dropShadow` | `surface`, radius 32, padding 13.2, gap 8.8; слоты `kicker` / `title` / `content` |
| `Avatar` | `Box(CircleShape)` + инициалы | 38–40 в списке, 48 в настройках, 76 в деталях; фон по хешу: `accent-200` / `accent-2-200` / `neutral-300`; берёт `BaseClient.getShortName()` |
| `OrganicTable` | `Column` + `Row` (не `LazyColumn` — таблицы короткие) | header 11 uppercase `muted` + линия `divider`; строки — линия `rowLine`, hover `hoverSubtle` |
| `ProgressBar` | `Canvas` / `Box` | высота 10, фон `neutral-300`, заполнение `accent`, radius 999 (экран 09) |
| `Stepper` | `Row` из двух `icon`-кнопок + `BasicText` | М5 |
| `PhotoTile` / `PhotoCarousel` / `PhotoViewer` | `Image` (Coil) + `Modifier.graphicsLayer` для зума | 3:4 плитки radius 20; заглушки `neutral-300/400`; `.washed` → `ColorFilter.colorMatrix(saturation 0.6)` + `alpha 0.94` |
| `EmptyState` | `Column` | кружок 132 (`accent-200` / `accent-2-200`) + `h2` + текст 16 `muted` + primary-кнопка |
| `OrganicModal` | см. §5 | |

Про `BasicTextField` и MVI: у нас VM — единственный источник истины (`state.name`, `onAction(OnNameChanged(it))`), поэтому используем перегрузку `BasicTextField(value: String, onValueChange: (String) -> Unit, decorationBox = { … })`. Перегрузка с `TextFieldState` даёт лучшее поведение IME/undo, но требует локального состояния + `snapshotFlow` в VM — берём её только если на Android всплывут проблемы с курсором.

---

## 4. Этап 4 — адаптивный каркас (Adaptive от Google)

Зависимости в `gradle/libs.versions.toml` (версия `1.2.0` — та же линия, что уже тянется транзитивно):

```toml
[versions]
composeAdaptive = "1.2.0"

[libraries]
compose-adaptive = { module = "org.jetbrains.compose.material3.adaptive:adaptive", version.ref = "composeAdaptive" }
compose-adaptive-layout = { module = "org.jetbrains.compose.material3.adaptive:adaptive-layout", version.ref = "composeAdaptive" }
compose-adaptive-navigation = { module = "org.jetbrains.compose.material3.adaptive:adaptive-navigation", version.ref = "composeAdaptive" }
```
→ в `commonMain` `sharedUI` (все таргеты сразу, как требует CLAUDE.md).

**4.1 Window size class вместо `BoxWithConstraints`.** Сейчас в `MainScreen.kt` порог зашит как `maxWidth >= 600.dp`. Заменяем на гайдлайновый:
```kotlin
val windowSize = currentWindowAdaptiveInfo().windowSizeClass
val expanded = windowSize.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) // 900dp
```

**4.2 Навигация.** Дизайн требует рейл 248 dp с pill-заливкой, брендом Caprasimo 19 сверху и подписью «<категория> · v1.0.0» снизу. Ни `NavigationRail`, ни `WideNavigationRail`, ни `NavigationSuiteScaffold` такое не параметризуют (у `NavigationSuiteItemColors` нет ни ширины, ни footer-слота). Поэтому:
- свой `OrganicNavigationRail` (248 dp, `surface`, padding 24/16, item = иконка 18 + текст 14, padding 11/14, radius 999, активный — `accent` + `bg`);
- свой `OrganicNavigationBar` для compact (мобильный таргет, 4 вкладки снизу);
- выбор — по window size class в `AppScaffold`. `compose-navsuitscaffold` после этого можно убрать из зависимостей.

**4.3 Master-detail.** Экраны 05–08 — `ListDetailPaneScaffold` из `adaptive-layout` + `rememberListDetailPaneScaffoldNavigator<Long>()` (ключ — id выбранной сущности) из `adaptive-navigation`. Это даёт бесплатно то, что README описывает как platform mapping: на expanded — две панели, на compact — одна с переходом «список → детали» и обработкой back.

```kotlin
val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
ListDetailPaneScaffold(
    directive = navigator.scaffoldDirective.copy(horizontalPartitionSpacerSize = 18.dp),
    value = navigator.scaffoldValue,
    listPane = { AnimatedPane(Modifier.preferredWidth(404.dp)) { ClientsListPane(...) } },
    detailPane = { AnimatedPane { ClientDetailPane(...) } },
)
```
Ширины панелей из макета: занятия — 400, клиенты — 404, рейл — 248. Рейл живёт **вне** scaffold'а (`Row { rail; scaffold }`).

**4.4 Окно десктопа.** `desktopApp/src/main/kotlin/.../main.kt`: `rememberWindowState(width = 1360.dp, height = 900.dp)` + `window.minimumSize`. Титульную полоску из макета не реализуем (это имитация окна ОС).

---

## 5. Этап 5 — модальный слой (М1–М12)

Сейчас формы — это destination'ы в backstack (`Screen.AddEditClientScreen`, `Screen.AddEditServiceScreen`, `Screen.PayServicesScreen`). По новому дизайну на десктопе это **модальные окна**, на мобильном — по-прежнему полноэкранные формы.

Решение: `OrganicModalHost` — корневой слой над контентом (`Box` в `AppScaffold`), а не `androidx.compose.ui.window.Dialog`. Причины: нужен точный скрим (`neutral-900 @50%`), radius 32, `shadow-lg`, деструктивное действие слева, и одинаковое поведение на всех таргетах без платформенных окон.

```kotlin
sealed interface ModalState {                       // ← в MainScreenState (см. dialogState в README §State)
    data class ServiceForm(val serviceId: Long?) : ModalState        // М1, 600dp
    data class ClientForm(val clientId: Long?) : ModalState          // М3, 600dp
    data object Prepay : ModalState                                  // М5, 520dp
    data class TimeConflict(val conflict: BaseService, val slots: List<LocalTime>) : ModalState  // М6
    data class Autofill(val services: List<BaseService>) : ModalState // М7, 520dp
    data class ConfirmDelete(val target: DeleteTarget, val linked: Int) : ModalState // М8, 460dp
    data object UnsavedChanges : ModalState                          // М9, 460dp
    data object ResetApp : ModalState                                // М10, 460dp
    data class NewExercise(val serviceId: Long) : ModalState         // М11, 520dp
    data class PhotoViewer(val photos: List<String>, val index: Int) : ModalState // М12, fullscreen
}
```
- `Esc` / клик по скриму → закрытие (для форм с изменениями → М9);
- ширина панели — параметр `OrganicModal(width = 600.dp)`;
- на compact `OrganicModal` рендерится как полноэкранный лист (одна ветка `if (expanded)`).

`Screen.AddEditClientScreen` / `AddEditServiceScreen` / `PayServicesScreen` уходят из `Screen`; их VM (`AddEditClientViewModel`, `AddEditServiceViewModel`, `PayServicesScreenViewModel`) переиспользуются **как есть** — просто получают `koinViewModel()` внутри модалки. `М6`/`М7` уже поддержаны логикой (`CheckServiceCrossingUseCase`, `AutofillServiceUseCase`), `М10` — `ClearDatabaseUseCase`.

---

## 6. Этап 6 — экраны

Порядок выбран так, чтобы приложение оставалось запускаемым после каждого шага. Ветка одна, коммит на экран.

| # | Экран макета | Файлы сейчас | VM | Что нового в UI |
|---|---|---|---|---|
| 01 | Приветственный / выбор категории | `welcome/WelcomeScreen.kt`, `service_type_selection/ServiceTypeSelectionScreen.kt` | `MainScreenViewModel` | Объединить в один экран: h1 48 + сетка 5 карточек-категорий (выбранная — `accent-100` + обводка 2 accent) + primary «Продолжить» |
| 02 | Нет клиентов | `main/empty/NoClientsScreen.kt` | `MainScreenViewModel` | `EmptyState` (кружок 132 `accent-2-200`), рейл виден, активна вкладка «Клиенты» |
| 03 | Клиенты есть, занятий нет | новый (ветка пустого состояния в `ServicesListScreen`) | `ServicesScreenViewModel` | `EmptyState` (кружок 132 `accent-200`) |
| 04 | Лента занятий | `services/ServicesListScreen.kt`, `services/ServiceItemView.kt` | `ServicesScreenViewModel` | `ServiceRow` (grid 124/1fr/176/352, radius 26, два `StatusButton` в строке); группы по датам; следующий день — `alpha 0.75`; `SegmentedControl` периода. **Контекстное меню по правому клику из `ServiceItemView` уходит** — статусы теперь кнопки в строке |
| 05–07 | Детали услуги (репетитор / тренер / тату-бьюти) | `service_details/ServiceDetailsScreen.kt` + `specific_fields/*` | `ServiceDetailsScreenViewModel` | 3 колонки: рейл 248 · список 400 (`surface@45%`, компактные строки, выбранная — `surface` + `shadow-md` + обводка accent) · детали. Специфика: репетитор — textarea «Домашнее задание»; тренер — таблица упражнений с колонкой «Прошлый раз» + дельта-тег; тату/бьюти — карусель референсов + сетка результата |
| 08 | Клиенты | `clients/ClientsListScreen.kt`, `client_details/ClientDetailScreen.kt` | `ClientsScreenViewModel`, `ClientDetailsViewModel` | `ListDetailPaneScaffold`: список 404 (поиск, `ClientRow` с секциями по первой букве — под `ClientListItem.LetterDivider` уже есть) · детали (аватар 76, 2 карточки-метрики max-width 520, карточка «Контакты», secondary «Все занятия клиента») |
| 09 | Статистика | `statistics/StatisticsScreen.kt`, `components/KufarPieChart.kt`, `StatisticsCurrencyCardView.kt` | `StatisticsScreenViewModel` | 3 карточки-метрики (38 Caprasimo, `ProgressBar` процента оплаты) + `OrganicTable` «Клиенты по сумме выплат». **`KufarPieChart` в новом дизайне не используется — удалить** |
| 10 | Настройки | `settings/SettingsScreen.kt` | `SettingsViewModel` | Колонка max-width 820: «Профиль» (заглушка входа), «Приложение», «Опасная зона» (`accent-100`, кнопка с бордером `accent-600`) |
| — | История занятий клиента | `services_history/ServicesHistoryScreen.kt` | `ServicesHistoryScreenViewModel` | В макете отдельного экрана нет, но кнопка «Все занятия клиента» на 08 на него ведёт — оформить как `ListDetailPaneScaffold`-detail или модалку; **уточнить** |
| — | Загрузка | `loading/LoadingScreen.kt` | — | Перекрасить: `bg` + свой индикатор (M3 `CircularProgressIndicator` заменить) |

Удаляются после миграции: `ui/components/CardView.kt`, `SelectorView.kt`, `DropDownMenuView.kt`, `ToolbarView.kt`, `HeaderView.kt`, `ShortNameBoxView.kt` (заменяются атомами из §3); `DateTimeViewWithPicker.kt` и `ServiceDateTimeSelectorView.kt` — переписываются на `OrganicSelect` + M3-пикеры, стилизованные токенами.

---

## 7. Этап 7 — доработки domain/VM под новый UI

Новый дизайн просит данные, которых сейчас в модели нет. Всё это — **небольшие** добавки, но их надо сделать до/вместе с соответствующим экраном:

| Что нужно | Где | Объём |
|---|---|---|
| Выделение в master-detail: `selectedClientId`, `selectedServiceId` | `ClientsListScreenState`, `ServicesListScreenState` (+ actions) | S |
| `addresses` — список ранее введённых адресов для селектора в М1 + «+ новый адрес» | новый use-case поверх `ServicesListRepository` (`SELECT DISTINCT address`) | S |
| Telegram у клиента (карточка «Контакты», М3) | `BaseClient` + `BaseClientDbModel` + миграция Room | M |
| «Прошлый раз» и дельта веса для упражнений (06, М11) | use-case: последнее выполнение упражнения клиента по временной метке | M |
| «Проект» для тату/бьюти (07: тег «Проект: …», группировка фото по активному проекту) | сейчас есть только `images: List<String>`; нужен `project` в `TattooServiceSpecificFields` / `BeautyServiceSpecificFields` + миграция | M |
| Статистика: суммы по двум валютам + процент оплаты («26 из 34») + сортировка клиентов по убыванию | `StatisticsScreenViewModel` / `StatisticsScreenState` | M |
| «N не оплачены» в подзаголовке ленты (04) | `ServicesListScreenState` | S |
| Свободные слоты рядом для М6 | расширить `CheckServiceCrossingUseCase` | M |
| Строки локализации нового UI (kicker'ы, заголовки таблиц, пустые состояния) | `composeResources/values/strings.xml` | S |

`ServicesFilter` уже покрывает сегменты «Сегодня/Завтра/Неделя/Месяц» (04) и «День/Неделя/Месяц/Всё время» (09) — для «Всё время» нужен один новый вариант enum'а.

---

## 8. Порядок работ

```
1. Токены + OrganicTheme (+ маппинг на M3 для не миграированных экранов)   ← ничего не ломает
2. Шрифты в composeResources + Lucide ImageVector'ы
3. Атомы DS + каталог-превью (@Preview экран со всеми атомами = наш «UI-кит»)
4. Adaptive-зависимости, AppScaffold + OrganicNavigationRail/Bar, размер окна десктопа
5. OrganicModalHost + ModalState; перенос форм М1/М3/М5 из backstack в модалки
6. Экраны в порядке: 01–02–03 → 04 → 08 → 05 → 06 → 07 → 09 → 10
7. Доработки domain (§7) — по мере необходимости экрана
8. Уборка: старые components, Color.kt/Theme.kt, materialIconsExtended, navsuitscaffold, KufarPieChart
```

Шаг 3 стоит закрыть экраном-каталогом атомов (аналог UI-кита из HTML) — дальше все экраны собираются из готового и сверяются с референсом быстро.

**Проверка на каждом шаге:** `./gradlew :desktopApp:run` — визуальная сверка с HTML-референсом (открыть рядом), плюс `./gradlew :androidApp:assembleDebug` чтобы compact-ветка не разъезжалась.

---

## 9. Риски и решения

| Риск | Решение |
|---|---|
| Прошлая попытка редизайна делалась параллельным слоем `uinew` и была отревертнута (`3897db2`, `6b50c43`) | Здесь — замена in place, одна ветка, коммит на экран; VM не дублируются, поэтому «второй UI» не появляется |
| CSS-тени и `Modifier.dropShadow` не совпадают 1:1 | Значения радиуса/смещения подбираются один раз в `OrganicElevation` и дальше не трогаются |
| `BasicTextField(value, onValueChange)` + MVI: возможны прыжки курсора на Android при асинхронном обновлении state | Десктоп — основной таргет, проблема не критична; при появлении — перейти на перегрузку с `TextFieldState` + `snapshotFlow` |
| M3-пикеры даты/времени визуально чужие | Стилизовать через маппинг `colorScheme` + `shapes`; если не хватит — свой календарь на `LazyVerticalGrid` (отдельная задача, вне MVP-объёма) |
| Room-миграции (telegram, project) | Делать одной миграцией в конце этапа 7, а не по одной на поле |
| Шрифты Caprasimo/Figtree нужно физически положить в репозиторий | OFL допускает; забрать с Google Fonts, приложить LICENSE рядом |
| iOS-таргет (SwiftUI по CLAUDE.md) | Дизайн-система дублируется в SwiftUI отдельно; сейчас `iosApp` не в git и не в `settings.gradle.kts` — за рамками этой миграции, но токены §1 стоит держать как единственный числовой источник |

---

## 10. Что стоит уточнить перед стартом

1. **История занятий клиента** (`ServicesHistoryScreen`) — в макете экрана нет, а кнопка «Все занятия клиента» на 08 есть. Модалка, detail-панель или отдельный экран?
2. **`BASE` (категория «другое»)** — на экране 01 пять карточек, включая «другое». Для неё блок «Поля категории» в М1/М3 пустой?
3. **Тёмная тема** — `_ds/readme.md` упоминает тёмный вариант («hairline edge + ambient darkness on a dark one»), но макет только светлый, а `ClientPlannerTheme` сейчас поддерживает `isSystemInDarkTheme()`. Оставляем только светлую (тогда `darkScheme` удаляется) или заводим тёмные токены?
4. **Мобильный таргет** — переносим ли compact-ветку в этой же миграции, или desktop-first, а Android догоняем отдельно (тогда на шаге 4 достаточно заглушки).
