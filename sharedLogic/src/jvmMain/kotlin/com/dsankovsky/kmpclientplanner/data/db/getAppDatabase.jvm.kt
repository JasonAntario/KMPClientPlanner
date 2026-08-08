package com.dsankovsky.kmpclientplanner.data.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.dsankovsky.kmpclientplanner.data.AppDirectory

fun getAppDatabase(): AppDatabase {
    // Раньше база жила в java.io.tmpdir: писать туда можно, поэтому баг не всплывал,
    // но ОС чистит временный каталог — данные пользователя рано или поздно пропадали.
    val dbFile = AppDirectory.file(AppDatabase.DB_NAME)
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .build()
}
