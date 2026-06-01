package com.dsankovsky.kmpclientplanner.data.db.dao.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.SportServiceSpecificFieldsDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SportServiceFieldsDao {
    @Query("SELECT * FROM sport_service_specific_fields WHERE service_id = :serviceId LIMIT 1")
    fun getSportServiceSpecificFieldByServiceId(serviceId: Long): Flow<SportServiceSpecificFieldsDbModel>

    @Update
    suspend fun updateSportServiceSpecificField(field: SportServiceSpecificFieldsDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSportServiceSpecificField(field: SportServiceSpecificFieldsDbModel): Long
}