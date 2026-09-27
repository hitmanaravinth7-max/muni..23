package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BloodGroup(val label: String) {
    A_POS("A+"),
    A_NEG("A-"),
    B_POS("B+"),
    B_NEG("B-"),
    AB_POS("AB+"),
    AB_NEG("AB-"),
    O_POS("O+"),
    O_NEG("O-");

    companion object {
        fun fromLabel(label: String): BloodGroup? = entries.firstOrNull { it.label.equals(label.trim(), ignoreCase = true) }

        val allLabels: List<String> = entries.map { it.label }

        fun canDonateTo(donorGroup: String, recipientGroup: String): Boolean {
            val donor = fromLabel(donorGroup) ?: return false
            val recipient = fromLabel(recipientGroup) ?: return false

            return when (donor) {
                O_NEG -> true // Universal red cell donor
                O_POS -> recipient in listOf(O_POS, A_POS, B_POS, AB_POS)
                A_NEG -> recipient in listOf(A_NEG, A_POS, AB_NEG, AB_POS)
                A_POS -> recipient in listOf(A_POS, AB_POS)
                B_NEG -> recipient in listOf(B_NEG, B_POS, AB_NEG, AB_POS)
                B_POS -> recipient in listOf(B_POS, AB_POS)
                AB_NEG -> recipient in listOf(AB_NEG, AB_POS)
                AB_POS -> recipient == AB_POS
            }
        }

        fun getCompatibleDonors(recipientGroup: String): List<String> {
            val recipient = fromLabel(recipientGroup) ?: return emptyList()
            return entries.filter { donor -> canDonateTo(donor.label, recipient.label) }.map { it.label }
        }

        fun getCompatibleRecipients(donorGroup: String): List<String> {
            val donor = fromLabel(donorGroup) ?: return emptyList()
            return entries.filter { recipient -> canDonateTo(donor.label, recipient.label) }.map { it.label }
        }
    }
}

enum class EmergencyLevel(val label: String, val levelPriority: Int) {
    CRITICAL("CRITICAL", 3),
    URGENT("URGENT", 2),
    STANDARD("STANDARD", 1);

    companion object {
        fun fromString(value: String): EmergencyLevel = entries.firstOrNull { it.label.equals(value, ignoreCase = true) } ?: STANDARD
    }
}

@Entity(tableName = "donors")
data class DonorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val bloodGroup: String,
    val phoneNumber: String,
    val email: String,
    val city: String,
    val isAvailable: Boolean = true,
    val lastDonationDate: String = "",
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "blood_requests")
data class BloodRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientName: String,
    val bloodGroup: String,
    val hospitalName: String,
    val city: String,
    val contactNumber: String,
    val emergencyLevel: String, // "CRITICAL", "URGENT", "STANDARD"
    val unitsNeeded: Int = 1,
    val notes: String = "",
    val isFulfilled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "blood_stocks")
data class BloodStockEntity(
    @PrimaryKey
    val bloodGroup: String,
    val unitsAvailable: Int,
    val status: String = "AVAILABLE", // "AVAILABLE", "LOW", "CRITICAL"
    val lastUpdated: Long = System.currentTimeMillis()
)
