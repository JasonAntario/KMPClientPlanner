package com.dsankovsky.kmpclientplanner.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dsankovsky.kmpclientplanner.data.datastore.createDataStore
import com.dsankovsky.kmpclientplanner.data.db.AppDatabase
import com.dsankovsky.kmpclientplanner.data.db.getAppDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> { getAppDatabase() }
    single<DataStore<Preferences>> { createDataStore() }
}
