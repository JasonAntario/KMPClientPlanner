package com.dsankovsky.kmpclientplanner.data.db.dao.service

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dsankovsky.kmpclientplanner.data.db.models.base.BaseServiceDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicesDao {
    @Query("SELECT * FROM base_service ORDER BY startTimestamp ASC")
    fun getAllServices(): Flow<List<BaseServiceDbModel>>

    @Query("SELECT * FROM base_service WHERE (isFinished = 0 OR isPaid = 0) ORDER BY startTimestamp ASC")
    fun getAllServicesExceptPaidAndFinished(): Flow<List<BaseServiceDbModel>>

    @Query("SELECT * FROM base_service WHERE (isFinished = 1 AND isPaid = 0) AND client_id = :clientId  ORDER BY startTimestamp ASC")
    fun getAllFinishedUnpaidServicesByClientId(clientId: Long): Flow<List<BaseServiceDbModel>>

    @Update
    suspend fun updateService(serviceDbModel: BaseServiceDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addService(serviceDbModel: BaseServiceDbModel): Long

    @Delete
    suspend fun deleteService(serviceDbModel: BaseServiceDbModel)

    @Query("DELETE FROM base_service WHERE id = :serviceId")
    suspend fun deleteService(serviceId: Long)

    @Query("SELECT * FROM base_service WHERE client_id = :clientId AND isPaid = 0 ORDER BY startTimestamp ASC")
    fun getAllUnpaidServices(clientId: Long): Flow<List<BaseServiceDbModel>>

    @Query("SELECT * FROM base_service WHERE startTimestamp >= :start AND startTimestamp < :end OR endTimestamp > :start AND endTimestamp <= :end")
    suspend fun getServicesInInterval(start: Long, end: Long): List<BaseServiceDbModel>

    @Query("SELECT * FROM base_service WHERE id = :serviceId LIMIT 1")
    fun getServiceById(serviceId: Long): Flow<BaseServiceDbModel>

    @Query("SELECT * FROM base_service WHERE client_id = :clientId ORDER BY startTimestamp DESC")
    suspend fun getServiceByClientId(clientId: Long): List<BaseServiceDbModel>

    @Query("SELECT * FROM base_service WHERE client_id = :clientId ORDER BY startTimestamp DESC")
    fun getServiceByClientIdFlow(clientId: Long): Flow<List<BaseServiceDbModel>>

    @Query("SELECT * FROM base_service WHERE client_id = :clientId ORDER BY startTimestamp DESC LIMIT 1")
    suspend fun getLastServiceByClientId(clientId: Long): BaseServiceDbModel
}