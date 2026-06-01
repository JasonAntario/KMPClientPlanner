package com.dsankovsky.kmpclientplanner.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import kotlinx.coroutines.flow.first

class AppSettings(private val dataStore: DataStore<Preferences>) {

    private suspend fun save(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    private suspend fun read(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    suspend fun saveServiceType(serviceType: ServiceType) {
        save(KEY_SERVICE_TYPE, serviceType.name)
    }

    suspend fun getServiceType(): ServiceType? {
        val result = read(KEY_SERVICE_TYPE) ?: return null
        return ServiceType.valueOf(result)
    }

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        private const val KEY_SERVICE_TYPE = "service_type"
        const val phonePrefix = "+375"
    }
}
