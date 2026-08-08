package com.dsankovsky.kmpclientplanner.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dsankovsky.kmpclientplanner.data.AppDirectory

fun createDataStore(): DataStore<Preferences> {
    return getDataStore {
        AppDirectory.file(DATA_STORE_FILE_NAME).absolutePath
    }
}
