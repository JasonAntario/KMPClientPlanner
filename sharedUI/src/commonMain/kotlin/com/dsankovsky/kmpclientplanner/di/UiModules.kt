package com.dsankovsky.kmpclientplanner.di

import com.dsankovsky.kmpclientplanner.navigation.AppNavigationViewModel
import com.dsankovsky.kmpclientplanner.screens.settings.SettingsViewModel
import com.dsankovsky.kmpclientplanner.screens.welcome.WelcomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { AppNavigationViewModel(get()) }
    viewModel { WelcomeViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
}
