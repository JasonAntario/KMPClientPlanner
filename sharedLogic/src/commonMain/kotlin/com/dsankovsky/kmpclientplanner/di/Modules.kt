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
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val useCasesModule = module {
    factoryOf(::AddEditClientSpecificFieldsUseCase)
    factoryOf(::AddEditDeleteClientUseCase)
    factoryOf(::GetClientSpecificFieldsUseCase)
    factoryOf(::GetClientsUseCase)
    factoryOf(::AddEditDeleteServiceUseCase)
    factoryOf(::AddEditServiceSpecificFieldsUseCase)
    factoryOf(::AutofillServiceUseCase)
    factoryOf(::CheckServiceCrossingUseCase)
    factoryOf(::GetServiceSpecificFieldsUseCase)
    factoryOf(::GetServicesUseCase)
    factoryOf(::ClearDatabaseUseCase)
}

val repositoryModule = module {
    singleOf(::AppRepositoryImpl).bind<AppRepository>()
    singleOf(::ClientsListRepositoryImpl).bind<ClientsListRepository>()
    singleOf(::ServicesListRepositoryImpl).bind<ServicesListRepository>()
    single { AppSettings(get()) }
}