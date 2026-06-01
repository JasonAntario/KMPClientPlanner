package com.dsankovsky.kmpclientplanner.data.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

fun getAppDatabase(): AppDatabase {
    val dbFile = File(System.getProperty("java.io.tmpdir"), AppDatabase.DB_NAME)
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .build()
}