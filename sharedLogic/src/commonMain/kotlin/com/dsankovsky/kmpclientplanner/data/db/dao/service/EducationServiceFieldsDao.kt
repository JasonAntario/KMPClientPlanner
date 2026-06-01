package com.dsankovsky.kmpclientplanner.data.db.dao.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.EducationServiceSpecificFieldsDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface EducationServiceFieldsDao {
    @Query("SELECT * FROM education_service_specific_fields WHERE service_id = :serviceId LIMIT 1")
    fun getEducationServiceSpecificFieldByServiceId(serviceId: Long): Flow<EducationServiceSpecificFieldsDbModel>

    @Update
    suspend fun updateEducationServiceSpecificField(field: EducationServiceSpecificFieldsDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEducationServiceSpecificField(field: EducationServiceSpecificFieldsDbModel): Long
}