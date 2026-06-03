package com.dsankovsky.kmpclientplanner.ui.screens.service_details

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.ui.extensions.getCurrentDateTime
import kotlinx.datetime.LocalDate

@Immutable
data class ServiceDetailsScreenState(
    val isLoading: Boolean = true,
    val title: String = "",
    val date: LocalDate = getCurrentDateTime().date,
    val time: String = "",
    val clientName: String = "",
    val address: String? = null,
    val price: String? = null,
    val isPaid: Boolean = false,
    val isFinished: Boolean = false,
    val comment: String? = null,
    val service: BaseService = BaseService(),
    val serviceSpecificFields: ServiceSpecificFields? = null,
    val initialServiceSpecificFields: ServiceSpecificFields? = null,
    val exercisesList: List<ServiceSpecificFields.SportServiceSpecificFields.Exercise> = emptyList()
)
