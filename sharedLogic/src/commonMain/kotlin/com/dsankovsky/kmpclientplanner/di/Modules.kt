package com.dsankovsky.kmpclientplanner.di

import com.dsankovsky.kmpclientplanner.data.AppRepositoryImpl
import com.dsankovsky.kmpclientplanner.data.ClientsListRepositoryImpl
import com.dsankovsky.kmpclientplanner.data.ServicesListRepositoryImpl
import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import com.dsankovsky.kmpclientplanner.domain.AppRepository
import com.dsankovsky.kmpclientplanner.domain.ClientsListRepository
import com.dsankovsky.kmpclientplanner.domain.ServicesListRepository
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
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single

expect val platformModule: Module

val useCasesModule = module {
    factory<AddEditClientSpecificFieldsUseCase>()
    factory<AddEditDeleteClientUseCase>()
    factory<GetClientSpecificFieldsUseCase>()
    factory<GetClientsUseCase>()
    factory<AddEditDeleteServiceUseCase>()
    factory<AddEditServiceSpecificFieldsUseCase>()
    factory<AutofillServiceUseCase>()
    factory<CheckServiceCrossingUseCase>()
    factory<GetServiceSpecificFieldsUseCase>()
    factory<GetServicesUseCase>()
    factory<ClearDatabaseUseCase>()
}

val repositoryModule = module {
    single<AppRepositoryImpl>() bind AppRepository::class
    single<ClientsListRepositoryImpl>() bind ClientsListRepository::class
    single<ServicesListRepositoryImpl>() bind ServicesListRepository::class
    single<AppSettings>()
}