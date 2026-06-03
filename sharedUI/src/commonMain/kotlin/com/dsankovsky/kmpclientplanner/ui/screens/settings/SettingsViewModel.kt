package com.dsankovsky.kmpclientplanner.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.usecases.ClearDatabaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appSettings: AppSettings,
    private val getDatabaseUseCase: ClearDatabaseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<SettingsScreenEvent>()

    fun handleActions(action: SettingsScreenAction) {
        when (action) {
            SettingsScreenAction.LoadData -> loadData()
            is SettingsScreenAction.OnServiceTypeSelected -> changeServiceType(action.serviceTypeIndex)
            SettingsScreenAction.DeleteAllData -> deleteAllData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val currentServiceType = appSettings.getServiceType()
            currentServiceType?.let { type ->
                _state.update {
                    it.copy(serviceType = type)
                }
            }
        }
    }

    private fun changeServiceType(serviceTypeIndex: Int) {
        val serviceType = ServiceType.entries[serviceTypeIndex]
        viewModelScope.launch {
            appSettings.saveServiceType(serviceType)
            _state.update {
                it.copy(serviceType = serviceType)
            }
        }
    }

    private fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            appSettings.clear()
            getDatabaseUseCase.clearDatabase()
            event.emit(SettingsScreenEvent.AllDataCleared)
        }
    }

}