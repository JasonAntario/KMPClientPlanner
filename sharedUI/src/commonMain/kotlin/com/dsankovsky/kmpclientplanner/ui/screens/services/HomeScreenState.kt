package com.dsankovsky.kmpclientplanner.ui.screens.services

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.additional.getHomeScreenFilters

@Immutable
data class HomeScreenState(
    val isLoading: Boolean = true,
    val currentFilter: ServicesFilter = ServicesFilter.TODAY,
    val filtersList: List<ServicesFilter> = getHomeScreenFilters(),
    val items: List<HomeScreenItem> = emptyList(),
    val scrollToIndex: Int = 0
)
