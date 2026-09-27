package com.example

import com.example.data.model.BloodGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testUniversalDonorCompatibility() {
        // O- can donate to all 8 blood groups
        BloodGroup.allLabels.forEach { recipient ->
            assertTrue("O- should donate to $recipient", BloodGroup.canDonateTo("O-", recipient))
        }
    }

    @Test
    fun testUniversalRecipientCompatibility() {
        // AB+ can receive from all 8 blood groups
        BloodGroup.allLabels.forEach { donor ->
            assertTrue("AB+ should receive from $donor", BloodGroup.canDonateTo(donor, "AB+"))
        }
    }

    @Test
    fun testIncompatibleDonations() {
        // A+ cannot donate to B+
        assertFalse(BloodGroup.canDonateTo("A+", "B+"))
        // B+ cannot donate to A-
        assertFalse(BloodGroup.canDonateTo("B+", "A-"))
        // AB+ cannot donate to O-
        assertFalse(BloodGroup.canDonateTo("AB+", "O-"))
    }
}
