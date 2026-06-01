package com.dsankovsky.kmpclientplanner.data.db.dao.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.BeautyServiceSpecificFieldsDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface BeautyServiceFieldsDao {
    @Query("SELECT * FROM beauty_service_specific_fields WHERE service_id = :serviceId LIMIT 1")
    fun getBeautyServiceSpecificFieldByServiceId(serviceId: Long): Flow<BeautyServiceSpecificFieldsDbModel>

    @Update
    suspend fun updateBeautyServiceSpecificField(field: BeautyServiceSpecificFieldsDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBeautyServiceSpecificField(field: BeautyServiceSpecificFieldsDbModel): Long
}