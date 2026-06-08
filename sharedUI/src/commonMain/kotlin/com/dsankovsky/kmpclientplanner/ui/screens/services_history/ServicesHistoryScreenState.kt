package com.dsankovsky.kmpclientplanner.ui.screens.services_history

import androidx.compose.runtime.Immutable
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesListScreenItem

@Immutable
data class ServicesHistoryScreenState(
    val isLoading: Boolean = true,
    val items: List<ServicesListScreenItem> = emptyList()
)
