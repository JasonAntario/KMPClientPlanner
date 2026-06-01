package com.dsankovsky.kmpclientplanner.data.db.dao.client

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.EducationClientSpecificFieldsDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface EducationClientFieldsDao {

    @Query("SELECT * FROM education_client_specific_fields WHERE client_id = :clientId LIMIT 1")
    fun getEducationClientSpecificFieldByClientId(clientId: Long): Flow<EducationClientSpecificFieldsDbModel>

    @Update
    suspend fun updateEducationClientSpecificField(field: EducationClientSpecificFieldsDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEducationClientSpecificField(field: EducationClientSpecificFieldsDbModel): Long
}