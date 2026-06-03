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
import kotlinx.datetime.LocalDateTime

@Immutable
data class AddEditServiceScreenState(
    val isLoading: Boolean = true,
    val isEdit: Boolean = false,
    val id: Long = BaseConstants.UNDEFINED_ID,
    val title: String = "",
    val client: BaseClient? = null,
    val startDateTime: LocalDateTime = getStartDateTime(),
    val endDateTime: LocalDateTime = getStartDateTime().addHours(1),
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
    val showDialog: ServiceScreenDialog? = null
) {

    sealed interface ServiceScreenDialog {
        data class ServicesCrossing(val services: List<BaseService>) : ServiceScreenDialog
        data object ConfirmServiceDeleting : ServiceScreenDialog
    }

    fun isFinishButtonEnabled(): Boolean {
        return title.isNotBlank() && client != null
    }
}