package com.dsankovsky.kmpclientplanner.ui.screens.client_details

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.ClientScreenDialog

@Immutable
data class ClientDetailsScreenState(
    val isLoading: Boolean = true,
    val clientName: String = "",
    val clientShortName: String = "",
    val phone: String? = null,
    val address: String? = null,
    val price: String? = null,
    val comment: String? = null,
    val client: BaseClient = BaseClient(),
    val showDialog: ClientScreenDialog? = null,
    val clientSpecificFields: ClientSpecificFields? = null,
    val initialClientSpecificFields: ClientSpecificFields? = null,
    val showServicesHistory: Boolean = false
)