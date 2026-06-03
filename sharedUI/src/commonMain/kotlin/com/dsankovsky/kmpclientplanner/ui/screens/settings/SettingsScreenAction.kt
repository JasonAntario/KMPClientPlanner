package com.dsankovsky.kmpclientplanner.ui.screens.settings

sealed interface SettingsScreenAction {

    data object LoadData : SettingsScreenAction
    data class OnServiceTypeSelected(val serviceTypeIndex: Int) : SettingsScreenAction
    data object DeleteAllData : SettingsScreenAction
}

sealed interface SettingsScreenEvent {
    data object AllDataCleared : SettingsScreenEvent
}