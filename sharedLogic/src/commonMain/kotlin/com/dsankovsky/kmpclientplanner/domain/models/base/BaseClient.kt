package com.dsankovsky.kmpclientplanner.domain.models.base

import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.data.BaseConstants.UNDEFINED_ID
import kotlinx.serialization.Serializable

@Serializable
data class BaseClient(
    val id: Long = UNDEFINED_ID,
    val name: String = "",
    val surname: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val price: Float? = null,
    val currency: CurrencyItem = CurrencyItem.BYN,
    val comment: String? = null,
    val serviceType: ServiceType = ServiceType.BASE,
    val serviceSubtype: String? = null,
) {

    fun getFullName() = surname?.let { "$name $it" } ?: name

    fun getShortName() = surname?.let { (name.take(1) + it.take(1)).uppercase() } ?: name.take(2)

    fun getServiceName() = "Service with ${getFullName()}"

    fun getFormattedPrice() = "$price ${currency.code}"
}
