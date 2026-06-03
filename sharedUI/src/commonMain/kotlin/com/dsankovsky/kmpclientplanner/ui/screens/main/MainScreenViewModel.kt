package com.dsankovsky.kmpclientplanner.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val appSettings: AppSettings,
    private val clientsUseCase: GetClientsUseCase
) : ViewModel() {

    val event = MutableSharedFlow<MainScreenEvent>()

    fun handleActions(actions: MainScreenActions) {
        when (actions) {
            MainScreenActions.GetStartDestination -> getStartDestination()
            is MainScreenActions.OnServiceTypeSelected -> updateServiceType(actions.serviceType)
        }
    }


    private fun getStartDestination() {
        viewModelScope.launch {
            val serviceType = appSettings.getServiceType()
            val clients = clientsUseCase.getAllClients().firstOrNull() ?: emptyList()
            val startDestination = when {
                serviceType == null -> Screen.WelcomeScreen
                clients.isEmpty() -> Screen.NoClientsScreen
                else -> Screen.HomeScreen
            }

            event.tryEmit(MainScreenEvent.Navigate(startDestination))
        }
    }

    private fun updateServiceType(serviceType: ServiceType) {
        viewModelScope.launch {
            appSettings.saveServiceType(serviceType)
            getStartDestination()
        }
    }
}