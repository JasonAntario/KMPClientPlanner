package com.dsankovsky.kmpclientplanner.data.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

fun getAppDatabase(): AppDatabase {
    val dbFile = NSHomeDirectory() + "/${AppDatabase.DB_NAME}"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile
    )
        .setDriver(BundledSQLiteDriver())
        .build()
}