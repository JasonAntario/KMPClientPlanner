package com.dsankovsky.kmpclientplanner.data.db.dao.client

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.SportClientSpecificFieldsDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SportClientFieldsDao {

    @Query("SELECT * FROM sport_client_specific_fields WHERE client_id = :clientId LIMIT 1")
    fun getSportClientSpecificFieldByClientId(clientId: Long): Flow<SportClientSpecificFieldsDbModel>

    @Update
    suspend fun updateSportClientSpecificField(field: SportClientSpecificFieldsDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSportClientSpecificField(field: SportClientSpecificFieldsDbModel): Long
}