package com.dsankovsky.kmpclientplanner.ui.screens.settings

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType

@Immutable
data class SettingsScreenState(
    val serviceType: ServiceType = ServiceType.BASE,
    val serviceTypeList: List<ServiceType> = ServiceType.entries,
    val version: String = "0.1.0"
)
