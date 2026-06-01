package com.dsankovsky.kmpclientplanner.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createDataStore(): DataStore<Preferences> {
    return getDataStore {
        DATA_STORE_FILE_NAME
    }
}