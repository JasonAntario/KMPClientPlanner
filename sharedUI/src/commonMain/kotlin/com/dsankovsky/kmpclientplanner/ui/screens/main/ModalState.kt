package com.dsankovsky.kmpclientplanner.ui.screens.main

/**
 * Модальные окна уровня приложения — те, что в макете лежат поверх всего окна вместе
 * с рейлом (М1, М3, М5, М10).
 *
 * Остальные модалки макета живут внутри своих экранов и переедут вместе с ними
 * на этапе 6: М6 (пересечение по времени), М7 (автозаполнение), М8 (удаление) и
 * М9 (несохранённые изменения) сейчас — состояния форм (`ClientScreenDialog`,
 * `ServiceScreenDialog`), а М11 (упражнение) и М12 (просмотр фото) принадлежат
 * экранам деталей.
 */
sealed interface ModalState {

    /** М1 — форма услуги, 600 dp. */
    data class ServiceForm(val serviceId: Long?) : ModalState

    /** М3 — форма клиента, 600 dp. */
    data class ClientForm(val clientId: Long?) : ModalState

    /** М5 — предоплата, 520 dp. */
    data object Prepay : ModalState

    /** М10 — сброс приложения, 460 dp. */
    data object ResetApp : ModalState
}
