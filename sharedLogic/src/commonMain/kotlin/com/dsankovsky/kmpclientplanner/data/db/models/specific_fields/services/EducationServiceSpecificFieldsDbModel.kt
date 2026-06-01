package com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.dsankovsky.kmpclientplanner.data.db.models.base.BaseServiceDbModel

@Entity(
    tableName = "education_service_specific_fields",
    foreignKeys = [
        ForeignKey(
            entity = BaseServiceDbModel::class,
            parentColumns = arrayOf(BaseServiceDbModel.Companion.NAME_ID),
            childColumns = arrayOf(EducationServiceSpecificFieldsDbModel.NAME_SERVICE_ID),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EducationServiceSpecificFieldsDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(NAME_ID)
    val id: Long,
    @ColumnInfo(NAME_SERVICE_ID)
    val serviceId: Long,
    val isOnline: Boolean,
    val homework: String?
) {

    companion object {
        const val NAME_ID = "id"
        const val NAME_SERVICE_ID = "service_id"
    }
}