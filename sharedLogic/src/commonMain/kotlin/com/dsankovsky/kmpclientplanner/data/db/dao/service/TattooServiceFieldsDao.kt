package com.dsankovsky.kmpclientplanner.data.db.dao.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.TattooServiceSpecificFieldsDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TattooServiceFieldsDao {

    @Query("SELECT * FROM tattoo_service_specific_fields WHERE service_id = :serviceId LIMIT 1")
    fun getTattooServiceSpecificFieldByServiceId(serviceId: Long): Flow<TattooServiceSpecificFieldsDbModel>

    @Update
    suspend fun updateTattooServiceSpecificField(field: TattooServiceSpecificFieldsDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTattooServiceSpecificField(field: TattooServiceSpecificFieldsDbModel): Long
}