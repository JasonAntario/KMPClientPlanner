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

## 3. Этап 3 — атомы DS на базовых компонентах Compose — сделано

Пакет `ui/design/components/`. Всё на `foundation`, без M3-обёрток.

| Файл | Что внутри |
|---|---|
| `OrganicText.kt` | `OrganicText` (BasicText + шкала + `LocalOrganicContentColor`), `OrganicDivider` |
| `OrganicButton.kt` | `OrganicButtonColors` + фабрики `primary/secondary/ghost/destructive/status/dangerOutlined`, `OrganicButton`, `OrganicIconButton` 36×36, общая `OrganicClickableSurface` |
| `OrganicTextField.kt` | `OrganicField` (подпись 12/отступ 5), `OrganicTextField` (**BasicTextField**, pill, 36, 6/14), `OrganicTextArea` (radius 16, 12/14, min 90) |
| `OrganicSelect.kt` | поле в оформлении `.input` + `Popup` + `LazyColumn` |
| `SegmentedControl.kt` | pill-контейнер с бордером и разделителями, опции 13/паддинг 7/12 |
| `StatusButton.kt` | `PaymentStatusButton`, `SessionStatusButton` |
| `Tag.kt` | `TagColors` + `accent/accent2/neutral/outline` |
| `OrganicCard.kt` | surface, radius 32, паддинг 13.2, gap 8.8, слоты kicker/title |
| `Avatar.kt` | кружок с инициалами, цвет детерминирован хешем имени |
| `OrganicTable.kt` | `OrganicTableHeader/Row/HeaderCell/Cell` — примитивы, колонки задаёт вызывающий |
| `ProgressBar.kt`, `Stepper.kt` | полоса 10px и «− значение +» |
| `Photo.kt` | `WashedColorFilter` (`.washed` как `ColorMatrix`), `PhotoTile` 3:4/20, `PhotoCarousel`, `PhotoCarouselDots`, `PhotoThumbnail` 56 |
| `EmptyState.kt` | кружок 132 + h2 + текст 16 muted + primary |

Решения по ходу:
- **Варианты кнопок — не enum, а фабрики цветов.** Кроме трёх из `styles.css` системе нужны деструктивная (accent-700), статусные (тройка fill/content/border) и «опасная зона» на экране настроек (бордер accent-600, текст accent-800).
- **Состояния — на одной `OrganicClickableSurface`**: заливка по hover/pressed, бордер, фокус-ринг, `alpha 0.45` для disabled. На ней же потом соберутся строки списков.
- У `.input` в CSS `outline-offset: 0`, поэтому кольцо фокуса поля рисуется вплотную к бордеру, а не с отступом 2, как у остальных контролов.
- `BasicTextField` не репортит hover, а бордер поля по нему меняется — добавлен явный `Modifier.hoverable`.
- Подпись кнопки и тега — `maxLines = 1, softWrap = false`: pill в макете всегда однострочный (без этого «Запланировано» переносилось по слогам).
- `LocalOrganicContentColor` — свой, чтобы атомы не зависели от `material3.LocalContentColor`.

У каждого компонента есть `@Preview` в том же файле (подложка — `PreviewSurface`: тема, кремовый фон, воздух):
поля, селект, сегмент-контрол и степпер в превью с настоящим состоянием, так что их можно щёлкать в
интерактивном режиме. Отдельное превью — сетка всех 23 иконок с именами.

Проверка — `OrganicComponentsSheetRenderTest` рендерит каталог в `sharedUI/build/design/organic-components.png`.

---

## 4. Этап 4 — адаптивный каркас (Adaptive от Google) — сделано

Зависимости (`gradle/libs.versions.toml`, `composeAdaptive = "1.2.0"` — та же линия, что уже
тянулась транзитивно) в `commonMain` `sharedUI`, то есть сразу на все таргеты:
`adaptive`, `adaptive-layout`, `adaptive-navigation`. `compose-navsuitscaffold` удалён —
он нигде не использовался, а рейл всё равно свой (см. 4.2).

**4.1 Window size class вместо `BoxWithConstraints`.** Порог `maxWidth >= 600.dp` заменён на
гайдлайновый — `isWideWindow()` в `ui/screens/main/AppScaffold.kt`:
```kotlin
currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
```
Порог — **medium (600 dp)**, а не expanded (840): по Material шире medium уже показывают
боковую навигацию, и численно это совпадает с прежним поведением. Expanded понадобится
отдельным флагом на этапе 6 — там он решает, показывать ли две панели.

**4.2 Навигация.** Дизайн требует рейл 248 dp с pill-заливкой, брендом 19 сверху и подписью
«<категория> · v1.0.0» снизу. Ни `NavigationRail`, ни `WideNavigationRail`, ни
`NavigationSuiteScaffold` такое не параметризуют (у `NavigationSuiteItemColors` нет ни ширины,
ни footer-слота), поэтому в DS появились свои:

| Компонент | Что делает |
|---|---|
| `OrganicNavigationRail` | 248 dp, `surface`, padding 24/16, gap 8, бренд сверху, футер прижат к низу |
| `OrganicNavigationRailItem` | pill во всю ширину, иконка 18 + текст 14, padding 11/14; активный — `accent` + контент `bg` |
| `OrganicNavigationBar` / `…BarItem` | compact-раскладка: `surface`, hairline сверху, те же pill-пункты иконкой над подписью |

- `AppScaffold` (`ui/screens/main/AppScaffold.kt`) выбирает рейл или нижнюю панель по
  `isWideWindow()`, держит `SnackbarHost` и оконные вставки. `MainScreen` теперь только
  backstack + `entryProvider`.
- `NavigationItem` переехал с `Icons.Default.*` на `OrganicIcons`; заодно у «Настроек»
  исправлена подпись — она указывала на `nav_bar_statistycs`.
- Футер рейла берёт категорию из нового `MainScreenState.serviceType` (`MainScreenViewModel`
  и раньше читал её из `AppSettings`, теперь просто отдаёт наружу) и версию из `AppInfo.VERSION`.
  Общего для таргетов источника версии нет — у Android `versionName`, у десктопа
  `packageVersion`, — поэтому строка в `AppInfo` синхронизируется руками.
- Кнопка «Добавить» временно живёт в рейле (`onAddClick`): в макете действие в шапке экрана,
  но шапки переедут только на этапе 6, а M3-шный FAB из рейла уже убран.

**4.3 Master-detail — на этапе 6.** Зависимости `adaptive-layout` / `adaptive-navigation`
подключены заранее, но `ListDetailPaneScaffold` ставится вместе с экранами 05–08:
```kotlin
val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
ListDetailPaneScaffold(
    directive = navigator.scaffoldDirective.copy(horizontalPartitionSpacerSize = 18.dp),
    value = navigator.scaffoldValue,
    listPane = { AnimatedPane(Modifier.preferredWidth(404.dp)) { ClientsListPane(...) } },
    detailPane = { AnimatedPane { ClientDetailPane(...) } },
)
```
Ширины панелей из макета: занятия — 400, клиенты — 404, рейл — 248. Рейл живёт **вне**
scaffold'а (`Row { rail; scaffold }`) — он уже так и стоит в `AppScaffold`.

**4.4 Окно десктопа.** `rememberWindowState(width = 1360.dp, height = 900.dp)` и
`window.minimumSize = 940×640` (ниже рейл 248 + контент перестают помещаться). Титульную
полоску из макета не реализуем — это имитация окна ОС.

Проверка — `OrganicNavigationRenderTest` рендерит обе раскладки (десктопный кадр 1360×660
как на экране 02 и compact с нижней панелью) в `sharedUI/build/design/organic-navigation.png`
и проверяет, что полоса рейла залита `--color-surface`, а контент лежит на `--color-bg`.

---

## 5. Этап 5 — модальный слой — сделано

Формы больше не destination'ы: `Screen.AddEditClientScreen`, `Screen.AddEditServiceScreen`
и `Screen.PayServicesScreen` удалены из `Screen`, вместо них — `ModalState`
(`ui/screens/main/ModalState.kt`), который держит `MainScreen` и раскрывает `AppModal`.

**5.1 Слой в DS** (`ui/design/components/OrganicModal.kt`) — не
`androidx.compose.ui.window.Dialog`: нужен точный скрим (`neutral-900 @50%`) и одинаковое
поведение на всех таргетах, а платформенное окно на десктопе приносит свою рамку.

| Компонент | Что делает |
|---|---|
| `OrganicModalHost` | скрим на весь экран, панель по центру, закрытие по Esc и клику мимо |
| `OrganicModal` | `.dialog`: surface, radius 32, `shadow-lg`, паддинг 17.6, gap 13.2; клики не пропускает, содержимое клипается по скруглению |
| `OrganicModalHeader` | заголовок 20 + пояснение 12 + крестик |
| `OrganicModalActions` | кнопки справа; деструктивное действие уходит влево (`justify-content: space-between` в макете) |
| `OrganicModalPanel` | кремовая плашка внутри модалки (списки в М5–М7), radius 22 |
| `ConfirmModal` | «вопрос — два действия» для М8/М9/М10 |

Ширины — `OrganicModalWidth`: `Form` 600 (М1, М3), `Medium` 520 (М5–М7, М11),
`Small` 460 (М8–М10). На compact окно `fullScreen = true` растягивает панель на весь экран.

**5.2 Что уже ходит через слой.** М1, М3, М5 и М10. Формы М1/М3/М5 показывают **ещё не
переписанные экраны**: они приходят со своим `Scaffold` и тулбаром, поэтому кладутся
в панель без паддинга и без шапки DS (`LegacyFormModal`). На этапе 6 содержимое меняется
на форму по макету, а слой остаётся как есть. VM переиспользуются целиком, включая
логику сохранения, удаления и снекбаров.

**5.3 М10 — сброс приложения.** Появилась защита, которой не было: раньше «Удалить все
данные» стирало базу по одному нажатию, теперь `ResetAppModal` разблокирует кнопку только
после ввода слова «СБРОС». Модалка берёт собственный `SettingsViewModel` — она живёт вне
`NavDisplay` и до экземпляра с экрана настроек не дотягивается; из VM ей нужна только
очистка базы.

**5.4 Что осталось на этап 6.** М6 (пересечение по времени), М7 (автозаполнение) и М8
(удаление) сейчас — состояния форм (`ClientScreenDialog`, `ServiceScreenDialog`),
нарисованные `AlertDialog` из M3; они переедут на `ConfirmModal`/`OrganicModalPanel` вместе
с формами. М9 (несохранённые изменения) требует признака «форма изменена», которого у
текущих VM нет. М11 (упражнение) и М12 (просмотр фото) принадлежат экранам деталей.

Проверка — `OrganicModalRenderTest` рендерит М5 и М10 в
`sharedUI/build/design/organic-modals.png` и проверяет, что скрим действительно перекрывает
кадр.

---

## 6. Этап 6 — экраны — в работе

Порядок выбран так, чтобы приложение оставалось запускаемым после каждого шага.

### Сделано

| # | Экран | Что получилось |
|---|---|---|
| 01 | Выбор категории | Приветственный экран **удалён** (`WelcomeScreen.kt`, `Screen.WelcomeScreen`): в макете это один экран. Логотип 64, h1 48, пять карточек в `FlowRow` (равная высота через `IntrinsicSize.Max`, выбранная — `accent-100` + обводка 2 accent), «Продолжить». Категория теперь применяется кнопкой, а не кликом по строке. Названия категорий переписаны под макет: EDUCATION → «Репетитор», SPORT → «Тренер», TATTOO → «Тату-мастер», BEAUTY → «Бьюти-мастер»; у каждой появилась подпись-подсказка |
| 02 | Нет клиентов | `EmptyState` с кружком `accent-2-200`. У `EmptyState` добавлено второстепенное действие — «Сменить тип услуг» больше некуда деть |
| 03 | Занятий нет | Ветка пустого состояния в `HomeScreenContent`, кружок `accent-200` |
| 04 | Лента занятий | Шапка «Занятия» + подзаголовок «Среда, 29 июля · 5 занятий, 3 не оплачены» (счётчики считаются в UI, плюрализация через `plurals`), `SegmentedControl` периода, primary «Добавить услугу» вместо FAB. `ServiceItemView` — строка 124 / 1fr / 176 / статусы, radius 26; **контекстное меню по правому клику убрано**, статусы стали кнопками. Группы дней — киккером, следующие дни `alpha 0.75` |
| 09 | Статистика | Три карточки-метрики (получено / процент оплаты с `ProgressBar` / ожидает оплаты) и `OrganicTable` «Клиенты по сумме выплат» с колонкой на каждую валюту. `KufarPieChart` и `StatisticsCurrencyCardView` **удалены**. В VM добавлены счётчики (`servicesTotal/Paid/Unpaid`, `clientsWithDebt`, `paidServicesCount` у клиента) и сортировка клиентов по убыванию выплат — `paidPercentage` до этого вообще никогда не заполнялся |
| 10 | Настройки | Колонка 820: «Профиль» (кнопка «Войти» выключена — входа ещё нет), «Приложение» (категория селектом, версия, обратная связь), «Опасная зона» на `accent-100` с кнопкой в М10 |
| — | Загрузка | Свой индикатор на `Canvas` вместо `CircularProgressIndicator` |

Попутно: `LocalDateTime.toTime()` печатает часы с ведущим нулём (в колонке времени «9:00» прыгало), появились `DayOfWeek.toUIName()` (раньше в шапке дня печаталось `WEDNESDAY`), `LocalDate.toUIWeekdayAndDate()` и `Float?.toUIMoney()`; подписи периодов сокращены до макетных.

Проверка — `OrganicScreensRenderTest` рендерит 01, 02, 03, 04, 09, 10 в кадре 1360×860
(`sharedUI/build/design/organic-screen-*.png`). Экраны, которым нужен Koin, офскрин-сцена
не поднимает — они проверяются только компиляцией.

### Осталось

| # | Экран | Что нужно |
|---|---|---|
| 05–07 | Детали услуги (репетитор / тренер / тату-бьюти) | 3 колонки: рейл 248 · список 400 (`surface@45%`, выбранная строка — `surface` + `shadow-md` + обводка accent) · детали. Специфика: репетитор — textarea «Домашнее задание»; тренер — таблица упражнений с колонкой «Прошлый раз» и дельта-тегом (+ М11); тату/бьюти — карусель референсов и сетка результата (+ М12) |
| 08 | Клиенты | `ListDetailPaneScaffold`: список 404 (поиск, секции по первой букве — `ClientListItem.LetterDivider` уже есть) · детали (аватар 76, две карточки-метрики, «Контакты», «Все занятия клиента») |
| М1/М3 | Формы услуги и клиента | Сейчас в модалке лежит старый экран со своим `Scaffold` (`LegacyFormModal`). Нужна форма по макету: сетка 1fr/1fr, статусные кнопки, разделитель и блок «Поля категории», деструктивное действие слева. Вместе с ней переезжают М6/М7/М8 (сейчас `AlertDialog` внутри форм) и появляется М9 |
| М5 | Предоплата | Та же история: содержимое модалки — старый `PayServicesScreen`. По макету это селект клиента, степпер, плашка со списком занятий и итогом |
| — | История занятий клиента | В макете экрана нет, кнопка «Все занятия клиента» на 08 есть; **всё ещё нужно решить** — модалка, detail-панель или экран |

Удаляются после миграции: `ui/components/CardView.kt`, `SelectorView.kt`, `DropDownMenuView.kt`, `ToolbarView.kt`, `HeaderView.kt`, `ShortNameBoxView.kt` (заменяются атомами из §3); `DateTimeViewWithPicker.kt` и `ServiceDateTimeSelectorView.kt` — переписываются на `OrganicSelect` + M3-пикеры, стилизованные токенами. Пока живы: их держат ещё не переписанные формы и детали.

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
