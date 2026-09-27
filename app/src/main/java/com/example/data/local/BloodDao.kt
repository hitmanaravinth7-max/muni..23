package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BloodRequestEntity
import com.example.data.model.BloodStockEntity
import com.example.data.model.DonorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodDao {
    // --- DONORS ---
    @Query("SELECT * FROM donors ORDER BY id DESC")
    fun getAllDonors(): Flow<List<DonorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: DonorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonors(donors: List<DonorEntity>)

    @Update
    suspend fun updateDonor(donor: DonorEntity)

    @Query("UPDATE donors SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun updateDonorAvailability(id: Long, isAvailable: Boolean)

    @Query("DELETE FROM donors WHERE id = :id")
    suspend fun deleteDonor(id: Long)

    @Query("SELECT COUNT(*) FROM donors")
    suspend fun countDonors(): Int

    // --- BLOOD REQUESTS ---
    @Query("SELECT * FROM blood_requests ORDER BY CASE emergencyLevel WHEN 'CRITICAL' THEN 1 WHEN 'URGENT' THEN 2 ELSE 3 END, createdAt DESC")
    fun getAllRequests(): Flow<List<BloodRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: BloodRequestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<BloodRequestEntity>)

    @Update
    suspend fun updateRequest(request: BloodRequestEntity)

    @Query("UPDATE blood_requests SET isFulfilled = :isFulfilled WHERE id = :id")
    suspend fun setRequestFulfilled(id: Long, isFulfilled: Boolean)

    @Query("DELETE FROM blood_requests WHERE id = :id")
    suspend fun deleteRequest(id: Long)

    @Query("SELECT COUNT(*) FROM blood_requests")
    suspend fun countRequests(): Int

    // --- BLOOD STOCKS ---
    @Query("SELECT * FROM blood_stocks")
    fun getAllStocks(): Flow<List<BloodStockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStock(stock: BloodStockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(stocks: List<BloodStockEntity>)

    @Query("UPDATE blood_stocks SET unitsAvailable = :units, status = :status, lastUpdated = :timestamp WHERE bloodGroup = :bloodGroup")
    suspend fun updateStock(bloodGroup: String, units: Int, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM blood_stocks")
    suspend fun countStocks(): Int
}
