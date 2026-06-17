import Foundation

/// Mirror of the strings used by the referenced Compose screens
/// (`sharedUI/.../composeResources/values/strings.xml`).
/// Kept here because Compose resources are not part of the iOS framework.
enum AppStrings {
    // Welcome
    static let welcomeMessage = "Добро пожаловать в Блокнот!"
    static let welcomeDescription = "Приложение позволяет вести учет клиентов, услуг и оплат за оказываемые услуги"
    static let start = "Начать"

    // Service type selection
    static let serviceTypeSelectionTitle = "Выберите тип услуг"
    static let serviceTypeSelectionDescription = "Это позволит нам правильно настроить приложение и упростить Вам работу с ним"

    // Service types
    static let serviceTypeBase = "Другое"
    static let serviceTypeEducation = "Образование"
    static let serviceTypeBeauty = "Красота и уход"
    static let serviceTypeTattoo = "Тату"
    static let serviceTypeSport = "Спорт"

    // Settings
    static let settingsTitle = "Настройки"
    static let settingsServiceType = "Тип услуг"
    static let settingsClearData = "Удалить все данные"
    static let settingsVersion = "Версия приложения"
    static let settingsFeedback = "Поделитесь мнением"
    static let feedbackUrl = "https://forms.gle/sh8ateS5wgdT4x4W6"

    // Home / nav
    static let mainTitle = "Записи"
    static let navBarMain = "Главная"
    static let navBarClients = "Клиенты"
    static let navBarStatistics = "Статистика"
    static let navBarSettings = "Настройки"
    static let serviceTypeChange = "Сменить тип услуг"

    // Common
    static let confirm = "Подтвердить"
    static let ok = "ОК"
    static let cancel = "Отмена"
    static let requiredField = "Обязательное поле"
    static let close = "Закрыть"

    // Clients list / empty
    static let clientsTitle = "Клиенты"
    static let clientsListNoClients = "Список клиентов пуст"
    static let clientsListNoClientsDescription = "Здесь пока никого нет.\nЧтобы начать, добавьте своего первого клиента"

    // Add/Edit client
    static let clientNewClient = "Новый клиент"
    static let clientEditClient = "Изменение клиента"
    static let clientName = "Имя"
    static let clientSurname = "Фамилия"
    static let clientPrice = "Цена"
    static let clientCurrency = "Валюта"
    static let clientPriceWillFillAutomatically = "Заполните как можно больше параметров, чтобы они автоматически подставлялись при создании услуги"
    static let clientComment = "Комментарий"
    static let clientLevel = "Уровень знаний"
    static let clientWeight = "Вес, кг"
    static let clientChooseFormatEducation = "Формат занятий"
    static let clientChooseFormatSport = "Формат тренировок"
    static let clientOnline = "Онлайн"
    static let clientOffline = "Оффлайн"
    static let clientAddLesson = "Добавить занятие"
    static let clientAddTraining = "Добавить тренировку"
    static let clientAddClient = "Добавить клиента"
    static let clientCurrentProject = "Текущий проект"
    static let clientFinishedProjects = "Завершенные проекты"
    static let clientFinishProject = "Завершить проект"
    static let clientAddress = "Адрес проведения"
    static let clientAddressPlaceholder = "Улица, Дом"
    static let clientPhone = "Телефон"
    static let clientConfirmDeleting = "Вы уверены, что хотите удалить клиента и все данные, связанные с ним?"
    static let clientShouldContinueAutofill = "Все равно заполнить услуги?"
    static let dayOfWeek = "День недели"
    static let datetimeDuration = "Длительность, ч"

    // Client details
    static let clientDetailsLessons = "Занятия"
    static let clientDetailsFillLessons = "Заполнить занятия"
    static let clientDetailsTraining = "Тренировки"
    static let clientDetailsFillTrainings = "Заполнить тренировки"
    static let clientDetailsServices = "Услуги клиента"

    // Services list
    static let servicesListNoServices = "Список услуг пуст"
    static let servicesListNoServicesDescription = "Здесь пока ничего нет\nДобавьте свою первую услугу или измените фильтр"
    static let servicesListHistory = "История услуг"

    // Add/Edit service
    static let serviceChooseFormatLesson = "Формат занятия"
    static let serviceChooseFormatTraining = "Формат тренировки"
    static let serviceNewService = "Новая услуга"
    static let serviceEditService = "Изменение услуги"
    static let serviceTitle = "Название"
    static let serviceChooseClient = "Выберете клиента"
    static let serviceStartTime = "Начало"
    static let serviceEndTime = "Конец"
    static let serviceStartDateGreaterEndDate = "Дата и время окончания не может быть раньше даты и времени начала"
    static let servicePrice = "Цена"
    static let serviceCurrency = "Валюта"
    static let serviceStatus = "Статус"
    static let serviceComment = "Комментарий"
    static let serviceHomework = "Домашнее задание"
    static let serviceUpdateData = "Обновить данные"
    static let serviceAddService = "Добавить услугу"
    static let serviceReferenceOrResult = "Референсы/Результат"
    static let serviceRepeats = "Повторы"
    static let serviceWeight = "Вес, кг"
    static let serviceTrainingAddExercise = "Добавить упражнение"
    static let serviceTrainingAddSet = "Добавить круг"
    static let serviceTrainingDeleteExercise = "Удалить упражнение"
    static let servicePaid = "Оплачено"
    static let serviceNotPaid = "Не оплачено"
    static let serviceFinished = "Проведено"
    static let serviceNotFinished = "Не проведено"
    static let serviceDetails = "Детали"
    static let serviceDate = "Дата"
    static let serviceTime = "Время"
    static let serviceCrossing = "В указанное время есть пересечение с другими услугами:"
    static let serviceConfirmDeleting = "Вы уверены, что хотите удалить услугу и все данные, связанные с ней?"

    // Currencies
    static let currencyByn = "Белорусский рубль"
    static let currencyRub = "Российский рубль"
    static let currencyEur = "Евро"
    static let currencyUsd = "Доллар США"

    // Autofill
    static let autofillDescription = "Заполнить услуги на ближайшие 4 недели?"
    static let autofillProlongDescription = "Продлить услуги еще на 4 недели? Заполнение начнется с даты последней созданной услуги клиента"
    static let serviceAutofill = "Заполнить"

    // Statistics
    static let statisticsTitle = "Моя статистика"
    static let statisticsByClient = "Статистика по клиентам"
    static let statisticsClient = "Клиент"
    static let statisticsPaid = "Оплачено"
    static let statisticsExpected = "Ожидается"
    static let statisticsIncomeInPeriod = "Заработано за период"
    static let statisticsExpectedInPeriod = "Ожидается за период"

    // Tabs / filters
    static let tabsToday = "сегодня"
    static let tabsTomorrow = "завтра"
    static let tabsCurrentWeek = "текущая неделя"
    static let tabsNextWeek = "следующая неделя"
    static let tabsCurrentMonth = "текущий месяц"
    static let tabsNextMonth = "следующий месяц"
    static let tabsCustomInterval = "период"

    // Pay services
    static let payServicesTitle = "Предоплата"
    static let paymentTitle = "Выберите клиента и количество услуг, которые хотите оплатить"
    static func paymentAvailableServices(_ count: Int) -> String { "Доступно услуг для оплаты: \(count)" }
    static let servicesPaid = "Услуги оплачены"

    // Snackbar messages
    static let clientDetailsDataUpdated = "Данные успешно обновлены"
    static let clientDetailsAutofillCompleted = "Услуги успешно заполнены"
    static let clientDetailsStatusUpdated = "Статус успешно обновлен"
    static let addEditClientDeleted = "Клиент успешно удален"
    static let addEditClientCreated = "Клиент успешно создан"
    static let addEditClientUpdated = "Данные клиента успешно обновлены"
    static let addEditServiceDeleted = "Услуга успешно удалена"
    static let addEditServiceCreated = "Услуга успешно создана"
    static let addEditServiceUpdated = "Данные услуги успешно обновлены"
}

