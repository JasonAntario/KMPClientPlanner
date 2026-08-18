package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.data.BaseConstants
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.extensions.addHours
import com.dsankovsky.kmpclientplanner.ui.extensions.getStartDateTime
import com.dsankovsky.kmpclientplanner.ui.extensions.minutesUntil
import kotlinx.datetime.LocalDateTime

/**
 * Состояние формы услуги (М1).
 *
 * Дата и время лежат только разобранными: их выбирают в пикерах, набрать вручную негде.
 * Текстом держится одна длительность ([durationText]) — это обычное числовое поле, и пока
 * в нём стирают цифры, в модели должно оставаться прежнее значение.
 */
@Immutable
data class AddEditServiceScreenState(
    val isLoading: Boolean = true,
    val isEdit: Boolean = false,
    val id: Long = BaseConstants.UNDEFINED_ID,
    val title: String = "",
    val client: BaseClient? = null,
    val startDateTime: LocalDateTime = getStartDateTime(),
    val endDateTime: LocalDateTime = getStartDateTime().addHours(1),
    val durationText: String = "",
    val address: String = "",
    val addressList: List<String> = emptyList(),
    val comment: String = "",
    val price: String = "",
    val currency: CurrencyItem = CurrencyItem.BYN,
    val currenciesList: List<CurrencyItem> = CurrencyItem.getCurrenciesList(),
    val isPaid: Boolean = false,
    val isFinished: Boolean = false,
    val clientsList: List<BaseClient> = emptyList(),
    val serviceType: ServiceType = ServiceType.BASE,
    val serviceSpecificFields: ServiceSpecificFields? = null,
    val showDialog: ServiceScreenDialog? = null,
    /** Какой услуга была при открытии формы; у новой — пустая форма. */
    val initialSnapshot: ServiceFormSnapshot? = null,
) {

    sealed interface ServiceScreenDialog {
        /** М6 — время занято другим занятием. */
        data class ServicesCrossing(val services: List<BaseService>) : ServiceScreenDialog

        /** М8 — удаление занятия. */
        data object ConfirmServiceDeleting : ServiceScreenDialog

        /** М9 — закрытие формы с несохранёнными правками. */
        data object ConfirmDiscard : ServiceScreenDialog

        /**
         * Занятие переехало в другой слот недели — переносить ли остальные занятия клиента.
         *
         * @param services занятия, которые переедут при положительном ответе
         * @param updatesClientSchedule в карточке клиента есть строка этого же слота, и она
         *   переедет вместе с занятиями: об этом в вопросе стоит сказать отдельно
         */
        data class ConfirmShiftFutureServices(
            val services: List<BaseService>,
            val updatesClientSchedule: Boolean,
        ) : ServiceScreenDialog
    }

    /** Длительность в минутах — она же значение поля, когда его ещё не трогали. */
    val durationMinutes: Int get() = startDateTime.minutesUntil(endDateTime)

    val snapshot: ServiceFormSnapshot
        get() = ServiceFormSnapshot(
            title = title,
            clientId = client?.id,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            address = address,
            comment = comment,
            price = price,
            currency = currency,
            isPaid = isPaid,
            isFinished = isFinished,
            specificFields = serviceSpecificFields,
        )

    val isDirty: Boolean get() = initialSnapshot != null && initialSnapshot != snapshot

    /** Начало занятия до правки: по нему опознаются остальные занятия того же слота недели. */
    val initialStartDateTime: LocalDateTime? get() = initialSnapshot?.startDateTime

    /** Занятие отдали другому клиенту: прошлая серия и его расписание уже не про это занятие. */
    val clientChanged: Boolean
        get() = initialSnapshot != null && initialSnapshot.clientId != client?.id

    /**
     * Занятие переехало в другой слот недели — другой день недели или другое время начала.
     *
     * Переезд на другую дату того же дня недели (занятие сдвинули на неделю вперёд) слот
     * не меняет: остальным занятиям серии от этого ничего не грозит.
     */
    val movedToAnotherWeekSlot: Boolean
        get() {
            val initial = initialStartDateTime ?: return false
            return initial.time != startDateTime.time ||
                initial.dayOfWeek != startDateTime.dayOfWeek
        }

    fun isFinishButtonEnabled(): Boolean {
        return title.isNotBlank() && client != null
    }
}

/** Слепок формы для сравнения «менялось / не менялось» (М9). */
@Immutable
data class ServiceFormSnapshot(
    val title: String,
    val clientId: Long?,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val address: String,
    val comment: String,
    val price: String,
    val currency: CurrencyItem,
    val isPaid: Boolean,
    val isFinished: Boolean,
    val specificFields: ServiceSpecificFields?,
)
