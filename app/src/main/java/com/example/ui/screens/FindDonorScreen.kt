package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodGroup
import com.example.data.model.DonorEntity
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.dialPhoneNumber
import com.example.ui.components.sendSmsMessage
import com.example.ui.components.shareText
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.BloodRedOnContainer
import com.example.ui.theme.BloodRedPrimary
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusAvailableGreenContainer
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FindDonorScreen(
    viewModel: BloodViewModel,
    donors: List<DonorEntity>,
    searchQuery: String,
    selectedBloodGroup: String?,
    onlyAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("find_donors_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. SEARCH HEADER ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Find Emergency Blood Donors",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Search our registered directory of verified life-savers by blood type and city.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by city, donor name, or blood group…") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = BloodRedPrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("donor_search_input")
                )
            }
        }

        // --- 2. FILTERS (BLOOD GROUP & AVAILABILITY) ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Blood Group Filter:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (selectedBloodGroup != null || searchQuery.isNotEmpty() || onlyAvailable) {
                        TextButton(
                            onClick = { viewModel.clearAllFilters() },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Reset All", fontSize = 12.sp)
                        }
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedBloodGroup == null,
                            onClick = { viewModel.selectBloodGroupFilter(null) },
                            label = { Text("All Groups") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BloodRedPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_chip_all")
                        )
                    }
                    items(BloodGroup.allLabels) { group ->
                        FilterChip(
                            selected = selectedBloodGroup == group,
                            onClick = { viewModel.selectBloodGroupFilter(group) },
                            label = { Text(group, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BloodRedPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_chip_$group")
                        )
                    }
                }

                // Available Only Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Show Only Currently Available Donors",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = onlyAvailable,
                        onCheckedChange = { viewModel.toggleOnlyAvailableFilter() },
                        colors = SwitchDefaults.colors(checkedThumbColor = BloodRedPrimary),
                        modifier = Modifier.testTag("available_only_switch")
                    )
                }

                Text(
                    text = "Showing ${donors.size} matching donor(s)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- 3. EMPTY STATE ---
        if (donors.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .testTag("empty_donors_card"),
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No Donors Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "No donors match your current filter parameters. Try clearing the search or blood group filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { viewModel.clearAllFilters() },
                            colors = ButtonDefaults.buttonColors(containerColor = BloodRedPrimary)
                        ) {
                            Text("Reset Filters")
                        }
                    }
                }
            }
        }

        // --- 4. DONOR CARDS ---
        items(donors, key = { it.id }) { donor ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("donor_card_${donor.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Top Info Row
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
                                bloodGroup = donor.bloodGroup,
                                size = 48,
                                fontSize = 17
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = donor.fullName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified Donor",
                                        tint = StatusAvailableGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = BloodRedPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = donor.city,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Availability Status Tag
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (donor.isAvailable) StatusAvailableGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (donor.isAvailable) "Available" else "Busy / Ineligible",
                                color = if (donor.isAvailable) StatusAvailableGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Contact & Meta details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Phone: ${donor.phoneNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (donor.lastDonationDate.isNotBlank()) {
                            Text(
                                text = "Last donated: ${donor.lastDonationDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { dialPhoneNumber(context, donor.phoneNumber) },
                            colors = ButtonDefaults.buttonColors(containerColor = BloodRedPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("call_donor_btn_${donor.id}")
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Call Donor", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                sendSmsMessage(
                                    context,
                                    donor.phoneNumber,
                                    "Hello ${donor.fullName}, there is an urgent blood inquiry for blood group ${donor.bloodGroup} in ${donor.city}. Are you available to donate?"
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sms_donor_btn_${donor.id}")
                        ) {
                            Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("SMS", fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = {
                                shareText(
                                    context,
                                    "Verified Blood Donor Contact",
                                    "Verified ${donor.bloodGroup} Blood Donor: ${donor.fullName} in ${donor.city}. Contact: ${donor.phoneNumber}."
                                )
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
