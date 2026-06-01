package com.dsankovsky.kmpclientplanner.data.db.dao.client

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.TattooClientSpecificFieldsDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TattooClientFieldsDao {

    @Query("SELECT * FROM tattoo_client_specific_fields WHERE client_id = :clientId LIMIT 1")
    fun getTattooClientSpecificFieldByClientId(clientId: Long): Flow<TattooClientSpecificFieldsDbModel>

    @Update
    suspend fun updateTattooClientSpecificField(field: TattooClientSpecificFieldsDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTattooClientSpecificField(field: TattooClientSpecificFieldsDbModel): Long
}