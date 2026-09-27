package com.example.data.repository

import com.example.data.local.BloodDao
import com.example.data.local.BloodDatabase
import com.example.data.model.BloodRequestEntity
import com.example.data.model.BloodStockEntity
import com.example.data.model.DonorEntity
import kotlinx.coroutines.flow.Flow

class BloodRepository(
    private val dao: BloodDao,
    private val database: BloodDatabase
) {
    val allDonors: Flow<List<DonorEntity>> = dao.getAllDonors()
    val allRequests: Flow<List<BloodRequestEntity>> = dao.getAllRequests()
    val allStocks: Flow<List<BloodStockEntity>> = dao.getAllStocks()

    suspend fun checkAndInitializeData() {
        if (dao.countStocks() == 0) {
            database.populateInitialData()
        }
    }

    suspend fun registerDonor(donor: DonorEntity): Long {
        return dao.insertDonor(donor)
    }

    suspend fun updateDonor(donor: DonorEntity) {
        dao.updateDonor(donor)
    }

    suspend fun setDonorAvailability(donorId: Long, isAvailable: Boolean) {
        dao.updateDonorAvailability(donorId, isAvailable)
    }

    suspend fun deleteDonor(donorId: Long) {
        dao.deleteDonor(donorId)
    }

    suspend fun createBloodRequest(request: BloodRequestEntity): Long {
        return dao.insertRequest(request)
    }

    suspend fun setRequestFulfilled(requestId: Long, isFulfilled: Boolean) {
        dao.setRequestFulfilled(requestId, isFulfilled)
    }

    suspend fun deleteRequest(requestId: Long) {
        dao.deleteRequest(requestId)
    }

    suspend fun updateStockUnits(bloodGroup: String, units: Int) {
        val calculatedStatus = when {
            units <= 5 -> "CRITICAL"
            units <= 15 -> "LOW"
            else -> "AVAILABLE"
        }
        dao.updateStock(bloodGroup, units.coerceAtLeast(0), calculatedStatus)
    }
}
