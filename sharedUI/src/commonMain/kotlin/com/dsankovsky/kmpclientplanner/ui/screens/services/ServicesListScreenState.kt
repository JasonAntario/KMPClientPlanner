package com.dsankovsky.kmpclientplanner.ui.screens.services

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServicesFilter
import com.dsankovsky.kmpclientplanner.domain.models.additional.getHomeScreenFilters

@Immutable
data class ServicesListScreenState(
    val isLoading: Boolean = true,
    val currentFilter: ServicesFilter = ServicesFilter.TODAY,
    val filtersList: List<ServicesFilter> = getHomeScreenFilters(),
    val items: List<ServicesListScreenItem> = emptyList(),
    val scrollToIndex: Int = 0,
    /**
     * Выбранное занятие: пока `null`, экран 04 — лента во всю ширину, иначе справа
     * открываются детали (05–07).
     */
    val selectedServiceId: Long? = null,
)
