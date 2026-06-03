# Kufar Блокнот — UI Mockups
Material 3 · MVP · Отдельный файл на каждый экран

## Структура

```
mockups/
├── shared_styles.css          — общие стили Material 3 (Android + Desktop)
├── android/
│   ├── 01_welcome.html              — Приветственный экран (выбор типа услуг)
│   ├── 02_home_feed.html            — Главная: лента занятий с фильтрами
│   ├── 03_session_detail.html       — Детали занятия (статусы, ДЗ, комментарий)
│   ├── 04_add_session.html          — Добавление занятия
│   ├── 05_clients_list.html         — Список клиентов (алфавитная группировка)
│   ├── 06_client_detail.html        — Карточка клиента с ближайшими занятиями
│   ├── 07_add_client_basic.html     — Новый клиент: базовый / бьюти-мастер
│   ├── 08_add_client_tutor.html     — Новый клиент: Репетитор (расписание, уровень)
│   ├── 09_add_client_trainer.html   — Новый клиент: Тренер (вес, расписание)
│   ├── 10_edit_client.html          — Редактирование клиента (с кнопкой Удалить)
│   ├── 11_statistics.html           — Статистика доходов + предоплата
│   └── 12_settings.html             — Настройки
└── desktop/
    ├── D01_welcome.html                    — Приветственный экран
    ├── D02_home_feed.html                  — Главная: Rail + две панели
    ├── D03_add_session.html                — Добавление занятия (двухколоночная форма)
    ├── D04_clients.html                    — Клиенты: список + карточка
    ├── D05_statistics.html                 — Статистика + рейтинг клиентов
    ├── D06_settings.html                   — Настройки
    ├── D07_add_client_basic.html           — Новый клиент: базовый / бьюти-мастер
    ├── D08_add_client_tutor_trainer.html   — Новый клиент: Репетитор / Тренер
    └── D09_edit_client.html                — Редактирование клиента
```

## Компоненты Material 3

**Android:** Navigation Bar · FAB · Cards (Filled, Outlined) ·
Filter Chips · Switches · Text Fields (Filled, Outlined) ·
Badges · List Items · Avatars · Buttons (Filled, Outlined, Text, Danger)

**Desktop:** Navigation Rail · FAB · Two-pane layout ·
Top App Bar · Filter Chips · Cards · Switches · Form fields ·
Badges · List Items · Avatars · Buttons

## Цветовая схема (Material 3 Purple)

| Token              | Hex       |
|--------------------|-----------|
| Primary            | #6750A4   |
| Primary Container  | #E8DEF8   |
| On Primary         | #FFFFFF   |
| Surface            | #FFFBFE   |
| Surface Variant    | #E6E0F8   |
| Outline            | #79747E   |
| Error              | #B3261E   |
| Success fill       | #C2E7C8   |
| Warning fill       | #FADDBE   |
