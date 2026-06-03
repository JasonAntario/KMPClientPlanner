package com.dsankovsky.kmpclientplanner.di

import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.main.MainScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServicesScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.services.HomeScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.services_history.ServicesHistoryScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.StatisticsScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { MainScreenViewModel(get(), get()) }
    viewModel { AddEditClientViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { AddEditServiceViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ClientDetailsViewModel(get(), get(), get(), get(), get()) }
    viewModel { ClientsScreenViewModel(get(), get()) }
    viewModel { HomeScreenViewModel(get(), get(), get()) }
    viewModel { PayServicesScreenViewModel(get(), get(), get()) }
    viewModel { ServiceDetailsScreenViewModel(get(), get(), get(), get(), get()) }
    viewModel { ServicesHistoryScreenViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { StatisticsScreenViewModel(get(), get()) }
}
