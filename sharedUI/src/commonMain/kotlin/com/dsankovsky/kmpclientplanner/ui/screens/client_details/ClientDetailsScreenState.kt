package com.dsankovsky.kmpclientplanner.ui.screens.client_details

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.ClientScreenDialog
import kotlinx.datetime.LocalDate

/**
 * Состояние карточки клиента (правая панель экрана 08).
 *
 * Метрики ([unpaidTotals], [prepaidCount], [servicesCount], [firstServiceDate]) считаются
 * по занятиям клиента и обновляются вслед за ними: статус можно переключить в ленте,
 * а карточка при этом уже открыта.
 */
@Immutable
data class ClientDetailsScreenState(
    val isLoading: Boolean = true,
    val clientName: String = "",
    val clientShortName: String = "",
    val phone: String? = null,
    val address: String? = null,
    val comment: String? = null,
    val client: BaseClient = BaseClient(),
    val showDialog: ClientScreenDialog? = null,
    val clientSpecificFields: ClientSpecificFields? = null,
    val initialClientSpecificFields: ClientSpecificFields? = null,
    val showServicesHistory: Boolean = false,
    /** «Не оплачено» — суммы неоплаченных занятий по валютам. */
    val unpaidTotals: List<ClientAmount> = emptyList(),
    /** Тег «N предоплаченных занятий»: оплачено, но ещё не проведено. */
    val prepaidCount: Int = 0,
    /** Всего занятий — число в подтверждении удаления (М8). */
    val servicesCount: Int = 0,
    /** «клиент с 12 марта» — дата самого раннего занятия. */
    val firstServiceDate: LocalDate? = null,
)

/** Сумма в конкретной валюте: у клиента занятия могут быть в двух валютах сразу. */
@Immutable
data class ClientAmount(val money: Float, val currency: CurrencyItem)
