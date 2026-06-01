package com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.dsankovsky.kmpclientplanner.data.db.models.base.BaseClientDbModel

@Entity(
    tableName = "sport_client_specific_fields",
    foreignKeys = [
        ForeignKey(
            entity = BaseClientDbModel::class,
            parentColumns = arrayOf(BaseClientDbModel.Companion.NAME_ID),
            childColumns = arrayOf(SportClientSpecificFieldsDbModel.NAME_CLIENT_ID),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SportClientSpecificFieldsDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = NAME_ID)
    val id: Long,
    @ColumnInfo(name = NAME_CLIENT_ID)
    val clientId: Long,
    val weight: Float?,
    val isOnline: Boolean,
    val lessonDateTimeList: ServiceDateTimeListDbModel
) {
    companion object {
        const val NAME_ID = "id"
        const val NAME_CLIENT_ID = "client_id"
    }
}