package com.dsankovsky.kmpclientplanner.di

import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import com.dsankovsky.kmpclientplanner.domain.usecases.ClearDatabaseUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.AddEditClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.AddEditDeleteClientUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditServiceSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AutofillServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.CheckServiceCrossingUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServiceSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

/**
 * Koin bootstrap for the iOS target.
 *
 * Mirrors the Android/Desktop startup (platformModule + useCasesModule + repositoryModule)
 * but omits the Compose-only `uiModule`, since iOS supplies its own SwiftUI ViewModels.
 *
 * Must be called once on app launch (see `iOSApp.swift`).
 */
fun initKoinIos() {
    startKoin {
        modules(platformModule, useCasesModule, repositoryModule)
    }
}

/**
 * Typed accessor that lets Swift obtain dependencies without referencing Koin symbols
 * directly — `koin-core` is an `implementation` dependency and therefore is not part of
 * the exported framework header. Swift uses it as `IosDi.shared.appSettings`, etc.
 */
object IosDi : KoinComponent {
    val appSettings: AppSettings by inject()
    val clearDatabaseUseCase: ClearDatabaseUseCase by inject()

    val getClientsUseCase: GetClientsUseCase by inject()
    val addEditDeleteClientUseCase: AddEditDeleteClientUseCase by inject()
    val getClientSpecificFieldsUseCase: GetClientSpecificFieldsUseCase by inject()
    val addEditClientSpecificFieldsUseCase: AddEditClientSpecificFieldsUseCase by inject()

    val getServicesUseCase: GetServicesUseCase by inject()
    val addEditDeleteServiceUseCase: AddEditDeleteServiceUseCase by inject()
    val getServiceSpecificFieldsUseCase: GetServiceSpecificFieldsUseCase by inject()
    val addEditServiceSpecificFieldsUseCase: AddEditServiceSpecificFieldsUseCase by inject()
    val autofillServiceUseCase: AutofillServiceUseCase by inject()
    val checkServiceCrossingUseCase: CheckServiceCrossingUseCase by inject()
}
