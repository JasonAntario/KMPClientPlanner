package com.dsankovsky.kmpclientplanner.ui.screens.services

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import kotlinx.datetime.LocalDateTime

sealed interface HomeScreenItem {

    @Immutable
    data class DateDivider(
        val day: Int,
        @field:StringRes val dayOfWeek: Int,
        @field:StringRes val month: Int
    ) : HomeScreenItem

    @Immutable
    data class ServiceItem(
        val id: Long = -1,
        val title: String = "",
        val client: BaseClient = BaseClient(),
        val comment: String? = null,
        val timeInterval: String = "",
        val startDate: LocalDateTime = getCurrentDateTime(),
        val endDate: LocalDateTime = getCurrentDateTime(),
        val serviceType: ServiceType = ServiceType.BASE,
        val isPaid: Boolean = false,
        val isFinished: Boolean = false,
        val specificFieldsId: Long? = null,
        val isOptionsRevealed: Boolean = false,
        val service: BaseService = BaseService()
    ) : HomeScreenItem
}