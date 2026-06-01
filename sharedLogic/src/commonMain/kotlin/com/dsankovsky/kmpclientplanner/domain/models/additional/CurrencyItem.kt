package com.dsankovsky.kmpclientplanner.domain.models.additional

import kotlinx.serialization.Serializable

@Serializable
sealed class CurrencyItem(
    val code: String
) {

    data object BYN : CurrencyItem("BYN")
    data object RUB : CurrencyItem("RUB")
    data object USD : CurrencyItem("USD")
    data object EUR : CurrencyItem("EUR")


    companion object {
        fun getCurrenciesList() = listOf(BYN, RUB, USD, EUR)
    }
}