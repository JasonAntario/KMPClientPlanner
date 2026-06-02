package com.dsankovsky.kmpclientplanner.screens.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WelcomeViewModel(
    private val appSettings: AppSettings
) : ViewModel() {

    private val _selectedType = MutableStateFlow<ServiceType?>(null)
    val selectedType: StateFlow<ServiceType?> = _selectedType.asStateFlow()

    fun selectType(type: ServiceType) {
        _selectedType.value = type
    }

    fun saveAndContinue(onDone: () -> Unit) {
        val type = _selectedType.value ?: return
        viewModelScope.launch {
            appSettings.saveServiceType(type)
            onDone()
        }
    }
}
