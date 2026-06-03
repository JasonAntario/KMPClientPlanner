package com.dsankovsky.kmpclientplanner.ui.screens.services_history

import androidx.compose.runtime.Immutable

@Immutable
data class ServicesHistoryScreenState(
    val isLoading: Boolean = true,
    val items: List<ServicesHistoryScreenItem> = emptyList()
)
