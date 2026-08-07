package com.dsankovsky.kmpclientplanner.ui.screens.pay_services

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService

/**
 * Состояние М5 (предоплата).
 *
 * Отмечаются оплаченными самые ранние неоплаченные занятия клиента, поэтому в состоянии
 * лежит весь их список, а степпер выбирает, сколько из них взять. Всё остальное —
 * производные величины: их считает не UI и не VM, а само состояние, чтобы правило
 * «сколько платим» жило в одном месте.
 *
 * @param clients клиенты со счётчиком долга — в селекте нужен «Имя — N неоплаченных занятий»
 * @param unpaidServices неоплаченные занятия выбранного клиента, от самого раннего
 */
@Immutable
data class PayServiceScreenState(
    val isLoading: Boolean = true,
    val clients: List<PrepayClient> = emptyList(),
    val selectedClientId: Long? = null,
    val amount: Int = DefaultAmount,
    val unpaidServices: List<BaseService> = emptyList(),
) {

    val maxAmount: Int get() = unpaidServices.size

    /** Занятия, которые уйдут в оплату: степпер отсекает хвост списка. */
    val servicesToPay: List<BaseService> get() = unpaidServices.take(amount)

    /** Итог по валютам: у занятий клиента она обычно одна, но модель это не гарантирует. */
    val totals: List<PrepayTotal>
        get() = servicesToPay
            .groupBy { it.currency }
            .map { (currency, services) ->
                PrepayTotal(services.sumOf { (it.price ?: 0f).toDouble() }.toFloat(), currency)
            }

    val isPaymentReady: Boolean get() = selectedClientId != null && amount in 1..maxAmount

    /** Платить нечего: ни у одного клиента нет долга. */
    val isEmpty: Boolean get() = !isLoading && clients.none { it.unpaidCount > 0 }

    companion object {
        /**
         * Начинаем с одного занятия, а не со всего долга: «Оплатить» в один клик не должно
         * закрывать все неоплаченные занятия клиента.
         */
        const val DefaultAmount = 1
    }
}

/** Клиент в селекте М5 вместе со своим долгом. */
@Immutable
data class PrepayClient(val client: BaseClient, val unpaidCount: Int)

@Immutable
data class PrepayTotal(val money: Float, val currency: CurrencyItem)
