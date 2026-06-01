package com.dsankovsky.kmpclientplanner.data.db.models.base

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "base_service", foreignKeys = [
        ForeignKey(
            entity = BaseClientDbModel::class,
            parentColumns = [BaseClientDbModel.NAME_ID],
            childColumns = [BaseServiceDbModel.NAME_CLIENT_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BaseServiceDbModel(
    @ColumnInfo(NAME_ID)
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    @ColumnInfo(NAME_CLIENT_ID)
    val clientId: Long,
    val address: String?,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val isFinished: Boolean,
    val isPaid: Boolean,
    val price: Float?,
    val currency: String?,
    val comment: String?,
    val serviceType: Int
) {
    companion object {
        const val NAME_ID = "id"
        const val NAME_CLIENT_ID = "client_id"
    }
}