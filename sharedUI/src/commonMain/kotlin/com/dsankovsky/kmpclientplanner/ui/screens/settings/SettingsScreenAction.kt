package com.dsankovsky.kmpclientplanner.ui.screens.settings

sealed interface SettingsScreenAction {

    data object LoadData : SettingsScreenAction
    data class OnServiceTypeSelected(val serviceTypeIndex: Int) : SettingsScreenAction
    /** Нажали «Удалить все данные» — показать М10, а не стирать сразу. */
    data object OnResetClicked : SettingsScreenAction
    data object DeleteAllData : SettingsScreenAction
}

sealed interface SettingsScreenEvent {
    data object ResetRequested : SettingsScreenEvent
    data object AllDataCleared : SettingsScreenEvent
}