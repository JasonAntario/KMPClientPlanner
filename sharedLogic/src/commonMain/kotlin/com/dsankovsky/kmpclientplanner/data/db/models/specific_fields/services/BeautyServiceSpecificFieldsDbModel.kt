package com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.dsankovsky.kmpclientplanner.data.db.models.base.BaseServiceDbModel


@Entity(
    tableName = "beauty_service_specific_fields",
    foreignKeys = [
        ForeignKey(
            entity = BaseServiceDbModel::class,
            parentColumns = arrayOf(BaseServiceDbModel.Companion.NAME_ID),
            childColumns = arrayOf(BeautyServiceSpecificFieldsDbModel.NAME_SERVICE_ID),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BeautyServiceSpecificFieldsDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(NAME_ID)
    val id: Long,
    @ColumnInfo(NAME_SERVICE_ID)
    val serviceId: Long,
    val images: ImagesListDbModel?
) {

    companion object {
        const val NAME_ID = "id"
        const val NAME_SERVICE_ID = "service_id"
    }
}