package com.dsankovsky.kmpclientplanner.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppNavigationViewModel(
    private val appSettings: AppSettings
) : ViewModel() {

    private val _startRoute = MutableStateFlow<AppRoutes?>(null)
    val startRoute: StateFlow<AppRoutes?> = _startRoute.asStateFlow()

    init {
        viewModelScope.launch {
            _startRoute.value =
                if (appSettings.getServiceType() != null) AppRoutes.MainRoute else AppRoutes.WelcomeRoute
        }
    }
}
