package com.dsankovsky.kmpclientplanner.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.usecases.ClearDatabaseUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appSettings: AppSettings,
    private val clearDatabaseUseCase: ClearDatabaseUseCase
) : ViewModel() {

    private val _serviceType = MutableStateFlow<ServiceType?>(null)
    val serviceType: StateFlow<ServiceType?> = _serviceType.asStateFlow()

    private val _navigateToWelcome = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToWelcome: SharedFlow<Unit> = _navigateToWelcome.asSharedFlow()

    init {
        viewModelScope.launch {
            _serviceType.value = appSettings.getServiceType()
        }
    }

    fun updateServiceType(type: ServiceType) {
        viewModelScope.launch {
            appSettings.saveServiceType(type)
            _serviceType.value = type
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            clearDatabaseUseCase.clearDatabase()
            appSettings.clear()
            _navigateToWelcome.emit(Unit)
        }
    }
}
