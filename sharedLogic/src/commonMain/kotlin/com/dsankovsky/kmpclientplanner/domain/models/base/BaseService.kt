package com.dsankovsky.kmpclientplanner.domain.models.base

import com.dsankovsky.kmpclientplanner.data.BaseConstants.UNDEFINED_ID
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.extensions.getCurrentDateTime
import com.dsankovsky.kmpclientplanner.extensions.toTime
import com.dsankovsky.kmpclientplanner.extensions.toUIDate
import kotlinx.datetime.LocalDateTime

data class BaseService(
    val id: Long = UNDEFINED_ID,
    val title: String = "",
    val clientId: Long = UNDEFINED_ID,
    val startDate: LocalDateTime = getCurrentDateTime(),
    val endDate: LocalDateTime = getCurrentDateTime(),
    val address: String? = null,
    val isFinished: Boolean = false,
    val isPaid: Boolean = false,
    val price: Float? = null,
    val currency: CurrencyItem = CurrencyItem.BYN,
    val comment: String? = null,
    val serviceType: ServiceType = ServiceType.BASE,
    val serviceSubtype: String? = null
) {

    fun getServiceTime(): String {
        return "${startDate.toTime()} - ${endDate.toTime()}"
    }

    fun getServiceDate(): String {
        return startDate.date.toUIDate()
    }
}