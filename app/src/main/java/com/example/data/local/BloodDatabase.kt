package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BloodGroup
import com.example.data.model.BloodRequestEntity
import com.example.data.model.BloodStockEntity
import com.example.data.model.DonorEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [DonorEntity::class, BloodRequestEntity::class, BloodStockEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BloodDatabase : RoomDatabase() {

    abstract fun bloodDao(): BloodDao

    companion object {
        @Volatile
        private var INSTANCE: BloodDatabase? = null

        fun getInstance(context: Context): BloodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BloodDatabase::class.java,
                    "blood_donation_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default sample data when database is created
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).populateInitialData()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun populateInitialData() {
        val dao = bloodDao()

        // 1. Initial Blood Stocks
        val initialStocks = listOf(
            BloodStockEntity(BloodGroup.O_POS.label, 48, "AVAILABLE"),
            BloodStockEntity(BloodGroup.O_NEG.label, 6, "CRITICAL"),
            BloodStockEntity(BloodGroup.A_POS.label, 36, "AVAILABLE"),
            BloodStockEntity(BloodGroup.A_NEG.label, 14, "LOW"),
            BloodStockEntity(BloodGroup.B_POS.label, 29, "AVAILABLE"),
            BloodStockEntity(BloodGroup.B_NEG.label, 9, "LOW"),
            BloodStockEntity(BloodGroup.AB_POS.label, 22, "AVAILABLE"),
            BloodStockEntity(BloodGroup.AB_NEG.label, 4, "CRITICAL")
        )
        dao.insertStocks(initialStocks)

        // 2. Initial Verified Donors
        val initialDonors = listOf(
            DonorEntity(
                fullName = "Dr. Michael Chen",
                bloodGroup = "O-",
                phoneNumber = "+1 (555) 234-8901",
                email = "michael.chen@healthmail.com",
                city = "New York, NY",
                isAvailable = true,
                lastDonationDate = "2024-01-15"
            ),
            DonorEntity(
                fullName = "Sarah Jenkins",
                bloodGroup = "O+",
                phoneNumber = "+1 (555) 876-5432",
                email = "s.jenkins@outlook.com",
                city = "Chicago, IL",
                isAvailable = true,
                lastDonationDate = "2024-02-20"
            ),
            DonorEntity(
                fullName = "David Rodriguez",
                bloodGroup = "A+",
                phoneNumber = "+1 (555) 345-6789",
                email = "david.rodz@gmail.com",
                city = "Houston, TX",
                isAvailable = true,
                lastDonationDate = "2023-11-10"
            ),
            DonorEntity(
                fullName = "Emily Watson",
                bloodGroup = "B+",
                phoneNumber = "+1 (555) 432-1098",
                email = "emily.watson@medcare.org",
                city = "Los Angeles, CA",
                isAvailable = false,
                lastDonationDate = "2024-03-01"
            ),
            DonorEntity(
                fullName = "Marcus Johnson",
                bloodGroup = "AB+",
                phoneNumber = "+1 (555) 987-6543",
                email = "marcus.j@fastmail.com",
                city = "Seattle, WA",
                isAvailable = true,
                lastDonationDate = "2023-12-05"
            ),
            DonorEntity(
                fullName = "Priya Sharma",
                bloodGroup = "A-",
                phoneNumber = "+1 (555) 654-3210",
                email = "priya.sharma@healthnet.com",
                city = "Boston, MA",
                isAvailable = true,
                lastDonationDate = "2024-01-28"
            ),
            DonorEntity(
                fullName = "Liam O'Connor",
                bloodGroup = "B-",
                phoneNumber = "+1 (555) 789-0123",
                email = "liam.oconnor@citymail.com",
                city = "Atlanta, GA",
                isAvailable = true,
                lastDonationDate = "2023-10-18"
            ),
            DonorEntity(
                fullName = "Amina Al-Mansoor",
                bloodGroup = "AB-",
                phoneNumber = "+1 (555) 321-7654",
                email = "amina.m@globalhealth.org",
                city = "Denver, CO",
                isAvailable = true,
                lastDonationDate = "2024-02-14"
            )
        )
        dao.insertDonors(initialDonors)

        // 3. Initial Emergency Blood Requests
        val initialRequests = listOf(
            BloodRequestEntity(
                patientName = "Robert Taylor (Emergency Trauma)",
                bloodGroup = "O-",
                hospitalName = "St. Jude Metropolitan Trauma Center",
                city = "New York, NY",
                contactNumber = "+1 (555) 911-0044",
                emergencyLevel = "CRITICAL",
                unitsNeeded = 3,
                notes = "Urgent road accident victim in ICU room 402. Immediate transfusion required.",
                isFulfilled = false
            ),
            BloodRequestEntity(
                patientName = "Clara Gomez (Surgical Care)",
                bloodGroup = "B-",
                hospitalName = "Mercy General Hospital",
                city = "Chicago, IL",
                contactNumber = "+1 (555) 911-2288",
                emergencyLevel = "URGENT",
                unitsNeeded = 2,
                notes = "Scheduled cardiac bypass surgery tomorrow morning, donor backup required.",
                isFulfilled = false
            ),
            BloodRequestEntity(
                patientName = "Ethan Miller (Pediatric Ward)",
                bloodGroup = "A+",
                hospitalName = "Children's Memorial Hospital",
                city = "Houston, TX",
                contactNumber = "+1 (555) 911-6633",
                emergencyLevel = "STANDARD",
                unitsNeeded = 1,
                notes = "Thalassemia routine monthly transfusion support.",
                isFulfilled = false
            )
        )
        dao.insertRequests(initialRequests)
    }
}
