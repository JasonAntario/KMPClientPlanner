package com.dsankovsky.kmpclientplanner.data

import com.dsankovsky.kmpclientplanner.data.db.AppDatabase
import com.dsankovsky.kmpclientplanner.domain.AppRepository

class AppRepositoryImpl(
    private val appDatabase: AppDatabase
) : AppRepository {

    override fun clearDatabase() {
//        appDatabase.clearAllTables()
    }
}