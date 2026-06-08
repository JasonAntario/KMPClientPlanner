package com.dsankovsky.kmpclientplanner.data

import com.dsankovsky.kmpclientplanner.data.db.AppDatabase
import com.dsankovsky.kmpclientplanner.data.db.dao.client.ClientsDao
import com.dsankovsky.kmpclientplanner.domain.AppRepository

class AppRepositoryImpl(
    appDatabase: AppDatabase
) : AppRepository {

    private val clientsDao: ClientsDao = appDatabase.clientsDao()

    override suspend fun clearDatabase() {
        clientsDao.clearAllTables()
    }
}