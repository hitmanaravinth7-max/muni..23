package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodGroup
import com.example.data.model.BloodRequestEntity
import com.example.data.model.EmergencyLevel
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.EmergencyBadge
import com.example.ui.components.dialPhoneNumber
import com.example.ui.components.shareText
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.BloodRedOnContainer
import com.example.ui.theme.BloodRedPrimary
import com.example.ui.theme.EmergencyAccent
import com.example.ui.theme.EmergencyOrange
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusAvailableGreenContainer
import com.example.ui.viewmodel.BloodRequestFormState
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmergencyRequestScreen(
    viewModel: BloodViewModel,
    requests: List<BloodRequestEntity>,
    formState: BloodRequestFormState,
    selectedEmergencyLevel: String?,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showRequestDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("emergency_requests_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. HEADER & SOS BUTTON ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Emergency Requests",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Urgent transfusions broadcasted by local hospitals & families.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Prominent Emergency Broadcast Button
                Button(
                    onClick = { showRequestDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyAccent),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("post_emergency_request_btn")
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "BROADCAST EMERGENCY BLOOD SOS",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // --- 2. URGENCY FILTERS ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Filter Urgency:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedEmergencyLevel == null,
                            onClick = { viewModel.selectEmergencyFilterLevel(null) },
                            label = { Text("All Requests (${requests.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BloodRedPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_urgency_all")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedEmergencyLevel == "CRITICAL",
                            onClick = { viewModel.selectEmergencyFilterLevel("CRITICAL") },
                            label = { Text("Critical SOS Only") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmergencyAccent,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_urgency_critical")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedEmergencyLevel == "URGENT",
                            onClick = { viewModel.selectEmergencyFilterLevel("URGENT") },
                            label = { Text("Urgent") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmergencyOrange,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_urgency_urgent")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedEmergencyLevel == "STANDARD",
                            onClick = { viewModel.selectEmergencyFilterLevel("STANDARD") },
                            label = { Text("Standard") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_urgency_standard")
                        )
                    }
                }
            }
        }

        // --- 3. EMPTY STATE ---
        if (requests.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusAvailableGreen,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "No Active Requests",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "There are no pending emergency blood requests matching this filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // --- 4. REQUEST CARDS ---
        items(requests, key = { it.id }) { req ->
            val isCritical = req.emergencyLevel == "CRITICAL"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("request_item_${req.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = if (isCritical && !req.isFulfilled) {
                    CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(EmergencyAccent, EmergencyAccent.copy(alpha = 0.2f)))
                    )
                } else null
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top Row: Badges & Patient
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BloodGroupBadge(
                                bloodGroup = req.bloodGroup,
                                size = 48,
                                fontSize = 16
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = req.patientName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalHospital,
                                        contentDescription = null,
                                        tint = BloodRedPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "${req.hospitalName} • ${req.city}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        EmergencyBadge(level = req.emergencyLevel)
                    }

                    // Units and Notes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BloodRedContainer
                        ) {
                            Text(
                                text = "${req.unitsNeeded} Unit(s) Needed",
                                color = BloodRedOnContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (req.isFulfilled) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StatusAvailableGreenContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = StatusAvailableGreen, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "Fulfilled",
                                        color = StatusAvailableGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    if (req.notes.isNotBlank()) {
                        Text(
                            text = req.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Contact & Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { dialPhoneNumber(context, req.contactNumber) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCritical) EmergencyAccent else BloodRedPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("call_request_hospital_${req.id}")
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Call Hospital", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                shareText(
                                    context,
                                    "EMERGENCY BLOOD REQUIRED: ${req.bloodGroup}",
                                    "URGENT BLOOD SOS: Patient ${req.patientName} requires ${req.unitsNeeded} unit(s) of ${req.bloodGroup} at ${req.hospitalName} (${req.city}). Emergency Level: ${req.emergencyLevel}. Contact immediately: ${req.contactNumber}."
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(0.8f)
                                .testTag("share_request_${req.id}")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Share", fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = { viewModel.toggleRequestFulfilled(req) },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                .testTag("fulfill_toggle_${req.id}")
                        ) {
                            Icon(
                                imageVector = if (req.isFulfilled) Icons.Default.Close else Icons.Default.Check,
                                contentDescription = if (req.isFulfilled) "Mark Unfulfilled" else "Mark Fulfilled",
                                tint = if (req.isFulfilled) EmergencyAccent else StatusAvailableGreen
                            )
                        }
                    }
                }
            }
        }
    }

    // --- EMERGENCY REQUEST SUBMISSION MODAL DIALOG ---
    if (showRequestDialog) {
        AlertDialog(
            onDismissRequest = {
                showRequestDialog = false
                viewModel.resetRequestForm()
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = EmergencyAccent)
                    Text("Broadcast Blood SOS", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = formState.patientName,
                            onValueChange = { viewModel.onRequestPatientNameChange(it) },
                            label = { Text("Patient Name *") },
                            placeholder = { Text("e.g. Robert Taylor") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BloodRedPrimary) },
                            isError = formState.errors.containsKey("patientName"),
                            supportingText = { formState.errors["patientName"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("req_form_patient_name")
                        )
                    }

                    item {
                        Text("Blood Group Needed *", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BloodGroup.allLabels.forEach { group ->
                                FilterChip(
                                    selected = formState.bloodGroup == group,
                                    onClick = { viewModel.onRequestBloodGroupChange(group) },
                                    label = { Text(group, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BloodRedPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    item {
                        Text("Emergency Urgency Level *", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("CRITICAL", "URGENT", "STANDARD").forEach { level ->
                                FilterChip(
                                    selected = formState.emergencyLevel == level,
                                    onClick = { viewModel.onRequestEmergencyLevelChange(level) },
                                    label = { Text(level, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = when (level) {
                                            "CRITICAL" -> EmergencyAccent
                                            "URGENT" -> EmergencyOrange
                                            else -> MaterialTheme.colorScheme.secondary
                                        },
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = formState.hospitalName,
                            onValueChange = { viewModel.onRequestHospitalNameChange(it) },
                            label = { Text("Hospital Name *") },
                            placeholder = { Text("e.g. St. Jude Trauma Center") },
                            leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null, tint = BloodRedPrimary) },
                            isError = formState.errors.containsKey("hospitalName"),
                            supportingText = { formState.errors["hospitalName"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("req_form_hospital")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formState.city,
                            onValueChange = { viewModel.onRequestCityChange(it) },
                            label = { Text("City / Location *") },
                            placeholder = { Text("e.g. New York, NY") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = BloodRedPrimary) },
                            isError = formState.errors.containsKey("city"),
                            supportingText = { formState.errors["city"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("req_form_city")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formState.contactNumber,
                            onValueChange = { viewModel.onRequestContactNumberChange(it) },
                            label = { Text("Hospital / Attendant Phone *") },
                            placeholder = { Text("+1 (555) 911-0000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BloodRedPrimary) },
                            isError = formState.errors.containsKey("contactNumber"),
                            supportingText = { formState.errors["contactNumber"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("req_form_contact")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formState.notes,
                            onValueChange = { viewModel.onRequestNotesChange(it) },
                            label = { Text("Clinical Notes / Reason") },
                            placeholder = { Text("e.g. Severe accident ICU room 402, required in next 2 hours") },
                            leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = BloodRedPrimary) },
                            modifier = Modifier.fillMaxWidth().testTag("req_form_notes")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitBloodRequest()
                        showRequestDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyAccent),
                    modifier = Modifier.testTag("submit_emergency_request_dialog_btn")
                ) {
                    Text("Broadcast SOS")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRequestDialog = false
                    viewModel.resetRequestForm()
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
