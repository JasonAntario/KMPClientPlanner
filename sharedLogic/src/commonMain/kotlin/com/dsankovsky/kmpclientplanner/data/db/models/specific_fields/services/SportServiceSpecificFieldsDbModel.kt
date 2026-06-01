package com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.dsankovsky.kmpclientplanner.data.db.models.base.BaseServiceDbModel
import kotlinx.serialization.Serializable


@Entity(
    tableName = "sport_service_specific_fields",
    foreignKeys = [
        ForeignKey(
            entity = BaseServiceDbModel::class,
            parentColumns = arrayOf(BaseServiceDbModel.Companion.NAME_ID),
            childColumns = arrayOf(SportServiceSpecificFieldsDbModel.NAME_SERVICE_ID),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SportServiceSpecificFieldsDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(NAME_ID)
    val id: Long,
    @ColumnInfo(NAME_SERVICE_ID)
    val serviceId: Long,
    val isOnline: Boolean,
    val exercises: ExercisesListDbModel
) {
    @Serializable
    data class ExercisesListDbModel(
        val exercises: List<ExerciseDbModel>
    )

    @Serializable
    data class ExerciseDbModel(
        val title: String,
        val sets: ExerciseSetsListDbModel
    )

    @Serializable
    data class ExerciseSetsListDbModel(
        val sets: List<ExerciseSetDbModel>
    )

    @Serializable
    data class ExerciseSetDbModel(
        val repeat: Int,
        val weight: Float
    )

    companion object {
        const val NAME_ID = "id"
        const val NAME_SERVICE_ID = "service_id"
    }
}
