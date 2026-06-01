package com.dsankovsky.kmpclientplanner.data.db.models.base

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "base_clients")
data class BaseClientDbModel(
    @ColumnInfo(NAME_ID)
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val surname: String?,
    val address: String?,
    val phone: String?,
    val price: Float?,
    val currency: String,
    val comment: String?,
    val serviceType: Int
) {
    companion object {
        const val NAME_ID = "id"
    }
}