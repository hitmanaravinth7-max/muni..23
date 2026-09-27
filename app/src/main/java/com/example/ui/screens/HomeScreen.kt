package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodGroup
import com.example.data.model.BloodRequestEntity
import com.example.data.model.BloodStockEntity
import com.example.data.model.DonorEntity
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.EmergencyBadge
import com.example.ui.components.StockStatusBadge
import com.example.ui.components.dialPhoneNumber
import com.example.ui.components.shareText
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.BloodRedOnContainer
import com.example.ui.theme.BloodRedPrimary
import com.example.ui.theme.EmergencyAccent
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: BloodViewModel,
    stocks: List<BloodStockEntity>,
    donors: List<DonorEntity>,
    requests: List<BloodRequestEntity>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var quickSearchGroup by remember { mutableStateOf("O-") }
    var quickSearchCity by remember { mutableStateOf("") }

    val totalUnits = stocks.sumOf { it.unitsAvailable }
    val activeDonorsCount = donors.count { it.isAvailable }
    val criticalRequests = requests.filter { !it.isFulfilled && it.emergencyLevel == "CRITICAL" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // --- 1. HERO EMERGENCY BANNER ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BloodRedPrimary
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BloodRedPrimary, Color(0xFF8E0000))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Emergency Blood Network",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "Every Drop Counts.\nFind Donors Instantly.",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 28.sp
                        )

                        Text(
                            text = "Connecting emergency patients with verified blood donors and live blood bank inventory.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openQuickEmergencyDialog() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = BloodRedPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hero_emergency_sos_btn")
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Emergency SOS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(AppScreen.REGISTER_DONOR) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.linearGradient(listOf(Color.White, Color.White))
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hero_register_donor_btn")
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Be a Donor", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- 2. STATS OVERVIEW ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Stock Units
                ElevatedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(AppScreen.AVAILABILITY) }
                        .testTag("stat_card_units"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$totalUnits",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = BloodRedPrimary
                        )
                        Text(
                            text = "Units Stocked",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Active Donors
                ElevatedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(AppScreen.FIND_DONOR) }
                        .testTag("stat_card_donors"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$activeDonorsCount",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = StatusAvailableGreen
                        )
                        Text(
                            text = "Active Donors",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Critical Requests
                ElevatedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(AppScreen.EMERGENCY_REQUEST) }
                        .testTag("stat_card_critical"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${criticalRequests.size}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = EmergencyAccent
                        )
                        Text(
                            text = "Critical SOS",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // --- 3. QUICK EMERGENCY BLOOD SEARCH WIDGET ---
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_quick_search_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = BloodRedPrimary
                            )
                            Text(
                                text = "Emergency Donor Search",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "1. Select Blood Group Needed:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BloodGroup.allLabels.forEach { group ->
                            val isSelected = quickSearchGroup == group
                            FilterChip(
                                selected = isSelected,
                                onClick = { quickSearchGroup = group },
                                label = { Text(group, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BloodRedPrimary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("quick_search_chip_$group")
                            )
                        }
                    }

                    OutlinedTextField(
                        value = quickSearchCity,
                        onValueChange = { quickSearchCity = it },
                        label = { Text("2. City / Location (e.g. New York, Chicago)") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BloodRedPrimary)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_search_location_input")
                    )

                    Button(
                        onClick = {
                            viewModel.performQuickEmergencySearch(quickSearchGroup, quickSearchCity.trim())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BloodRedPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_search_find_btn")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Find Matching Donors for $quickSearchGroup", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 4. BLOOD AVAILABILITY SNAPSHOT (ALL 8 GROUPS) ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Blood Stock (8 Groups)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { viewModel.navigateTo(AppScreen.AVAILABILITY) }) {
                        Text("View Details")
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(stocks, key = { it.bloodGroup }) { stock ->
                        Card(
                            modifier = Modifier
                                .width(135.dp)
                                .clickable { viewModel.openCompatibilityModal(stock.bloodGroup) }
                                .testTag("home_stock_chip_${stock.bloodGroup}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BloodGroupBadge(bloodGroup = stock.bloodGroup, size = 40, fontSize = 14)
                                Text(
                                    text = "${stock.unitsAvailable} Units",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                StockStatusBadge(status = stock.status)
                            }
                        }
                    }
                }
            }
        }

        // --- 5. CRITICAL ACTIVE SOS ALERTS ---
        if (criticalRequests.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = EmergencyAccent)
                            Text(
                                text = "Immediate SOS Requests",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmergencyAccent
                            )
                        }
                        TextButton(onClick = { viewModel.navigateTo(AppScreen.EMERGENCY_REQUEST) }) {
                            Text("See All (${requests.size})")
                        }
                    }

                    criticalRequests.take(2).forEach { req ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_sos_request_${req.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(EmergencyAccent, EmergencyAccent.copy(alpha = 0.3f)))
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        BloodGroupBadge(bloodGroup = req.bloodGroup, size = 36, fontSize = 13)
                                        Column {
                                            Text(
                                                text = req.patientName,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                            Text(
                                                text = "${req.hospitalName} • ${req.city}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    EmergencyBadge(level = req.emergencyLevel)
                                }

                                if (req.notes.isNotBlank()) {
                                    Text(
                                        text = req.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { dialPhoneNumber(context, req.contactNumber) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyAccent),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Call Hospital", fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            shareText(
                                                context,
                                                "EMERGENCY BLOOD REQUIRED: ${req.bloodGroup}",
                                                "CRITICAL SOS: Patient ${req.patientName} requires ${req.unitsNeeded} units of ${req.bloodGroup} blood at ${req.hospitalName} (${req.city}). Urgent contact: ${req.contactNumber}."
                                            )
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 6. BECOME A DONOR CALLOUT ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_volunteer_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BloodRedContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BloodRedPrimary,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Register as a Blood Donor",
                            fontWeight = FontWeight.Bold,
                            color = BloodRedOnContainer,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Join our donor database to get notified when urgent blood requests arise in your city.",
                            style = MaterialTheme.typography.bodySmall,
                            color = BloodRedOnContainer.copy(alpha = 0.8f)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.REGISTER_DONOR) },
                        modifier = Modifier.testTag("home_volunteer_arrow_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go to registration",
                            tint = BloodRedPrimary
                        )
                    }
                }
            }
        }
    }
}
