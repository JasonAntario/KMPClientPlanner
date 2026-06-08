package com.dsankovsky.kmpclientplanner.di

import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.AddEditClientViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service.AddEditServiceViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.client_details.ClientDetailsViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.clients.ClientsScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.main.MainScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.pay_services.PayServicesScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.service_details.ServiceDetailsScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.services.ServicesScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.services_history.ServicesHistoryScreenViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.settings.SettingsViewModel
import com.dsankovsky.kmpclientplanner.ui.screens.statistics.StatisticsScreenViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val uiModule = module {
    viewModel<MainScreenViewModel>()
    viewModel<AddEditClientViewModel>()
    viewModel<AddEditServiceViewModel>()
    viewModel<ClientDetailsViewModel>()
    viewModel<ClientsScreenViewModel>()
    viewModel<ServicesScreenViewModel>()
    viewModel<PayServicesScreenViewModel>()
    viewModel<ServiceDetailsScreenViewModel>()
    viewModel<ServicesHistoryScreenViewModel>()
    viewModel<SettingsViewModel>()
    viewModel<StatisticsScreenViewModel>()
}
