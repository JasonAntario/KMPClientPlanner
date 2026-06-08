package com.dsankovsky.kmpclientplanner.data.db.dao.client

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import androidx.room.Update
import com.dsankovsky.kmpclientplanner.data.db.models.base.BaseClientDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientsDao {

    @Query("SELECT * FROM base_clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<BaseClientDbModel>>

    @Update
    suspend fun updateClient(clientDbModel: BaseClientDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addClient(clientDbModel: BaseClientDbModel): Long

    @Delete
    suspend fun deleteClient(clientDbModel: BaseClientDbModel)

    @Query("DELETE FROM base_clients WHERE id = :clientId")
    suspend fun deleteClient(clientId: Long)

    @Query("SELECT * FROM base_clients WHERE id = :clientId LIMIT 1")
    fun getClientById(clientId: Long?): Flow<BaseClientDbModel>

    @Transaction
    @Query("DELETE FROM base_clients")
    suspend fun clearAllTables()

    @RawQuery
    suspend fun query(query: RoomRawQuery): Int

    suspend fun query(sql: String) = query(RoomRawQuery(sql))
}