package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.data.BaseConstants
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields

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
    val initialServiceFields: ClientSpecificFields? = null,
    val clientSpecificFields: ClientSpecificFields? = null,
    val showDialog: ClientScreenDialog? = null,
    val isCurrencyMenuExpanded: Boolean = false
) {

    fun getShortName(): String {
        return if (surname.isNotEmpty()) {
            (name.take(1) + surname.take(1)).uppercase()
        } else {
            name.take(2)
        }
    }
}

sealed interface ClientScreenDialog {
    data object ConfirmAutofillServices : ClientScreenDialog
    data class ServicesCrossing(val services: List<BaseService>) : ClientScreenDialog
    data object ConfirmClientDeleting : ClientScreenDialog
}