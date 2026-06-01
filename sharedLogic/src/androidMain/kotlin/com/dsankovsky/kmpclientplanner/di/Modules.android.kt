package com.dsankovsky.kmpclientplanner.di

import com.dsankovsky.kmpclientplanner.data.datastore.createDataStore
import com.dsankovsky.kmpclientplanner.data.db.getAppDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single { getAppDatabase(get()) }
    single { createDataStore(get()) }
}