package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.data.BaseConstants
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields

/**
 * Состояние формы клиента (М3).
 *
 * Значения полей живут здесь, а не в `TextFieldState` внутри экрана: форма — модалка,
 * её содержимое подменяется на подтверждения (М6–М9), и после возврата поля должны быть
 * теми же. Заодно это даёт «форма изменена» для М9 — сравнением со [initialSnapshot].
 */
@Immutable
data class AddEditClientScreenState(
    val isLoading: Boolean = true,
    val isEdit: Boolean = false,
    val id: Long = BaseConstants.UNDEFINED_ID,
    val name: String = "",
    val surname: String = "",
    val address: String = "",
    val addressList: List<String> = emptyList(),
    val phone: String = "",
    val price: String = "",
    val currency: CurrencyItem = CurrencyItem.BYN,
    val currenciesList: List<CurrencyItem> = CurrencyItem.getCurrenciesList(),
    val comment: String = "",
    val serviceType: ServiceType = ServiceType.BASE,
    val clientSpecificFields: ClientSpecificFields? = null,
    val showDialog: ClientScreenDialog? = null,
    /** Каким клиент был при открытии формы; у нового клиента — пустая форма. */
    val initialSnapshot: ClientFormSnapshot? = null,
) {

    fun getShortName(): String {
        return if (surname.isNotEmpty()) {
            (name.take(1) + surname.take(1)).uppercase()
        } else {
            name.take(2)
        }
    }

    /** Поля категории на момент открытия — по ним решается, спрашивать ли про автозаполнение. */
    val initialServiceFields: ClientSpecificFields? get() = initialSnapshot?.specificFields

    val snapshot: ClientFormSnapshot
        get() = ClientFormSnapshot(
            name = name,
            surname = surname,
            address = address,
            phone = phone,
            price = price,
            currency = currency,
            comment = comment,
            specificFields = clientSpecificFields,
        )

    val isDirty: Boolean get() = initialSnapshot != null && initialSnapshot != snapshot

    /** Без имени клиента сохранять нечего. */
    val canSave: Boolean get() = name.isNotBlank()
}

/** Слепок формы для сравнения «менялось / не менялось» (М9). */
@Immutable
data class ClientFormSnapshot(
    val name: String,
    val surname: String,
    val address: String,
    val phone: String,
    val price: String,
    val currency: CurrencyItem,
    val comment: String,
    val specificFields: ClientSpecificFields?,
)

sealed interface ClientScreenDialog {
    /** М7 — предложение сразу создать занятия по расписанию. */
    data object ConfirmAutofillServices : ClientScreenDialog

    /** М6 — автозаполнение наткнулось на занятое время. */
    data class ServicesCrossing(val services: List<BaseService>) : ClientScreenDialog

    /** М8 — удаление клиента. */
    data object ConfirmClientDeleting : ClientScreenDialog

    /** М9 — закрытие формы с несохранёнными правками. */
    data object ConfirmDiscard : ClientScreenDialog
}
