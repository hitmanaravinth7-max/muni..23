package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodGroup
import com.example.data.model.BloodStockEntity
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.StockStatusBadge
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.BloodRedOnContainer
import com.example.ui.theme.BloodRedPrimary
import com.example.ui.theme.EmergencyAccent
import com.example.ui.theme.EmergencyOrange
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AvailabilityScreen(
    viewModel: BloodViewModel,
    stocks: List<BloodStockEntity>,
    modifier: Modifier = Modifier
) {
    val totalUnits = stocks.sumOf { it.unitsAvailable }
    val criticalCount = stocks.count { it.status == "CRITICAL" }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("availability_grid"),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- 1. HEADER & OVERVIEW BANNER ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Blood Inventory & Availability",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Live blood bank units across all 8 blood groups. Tap any blood card to inspect transfusion compatibility or update count.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (criticalCount > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BloodRedContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = EmergencyAccent)
                            Text(
                                text = "CRITICAL DEFICIT: $criticalCount blood type(s) below safety threshold. Donors urgently required!",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = BloodRedOnContainer
                            )
                        }
                    }
                }
            }
        }

        // --- 2. 8 BLOOD GROUP TILES ---
        items(stocks, key = { it.bloodGroup }) { stock ->
            val progress = (stock.unitsAvailable / 50f).coerceIn(0f, 1f)
            val progressColor = when (stock.status.uppercase()) {
                "AVAILABLE" -> StatusAvailableGreen
                "LOW" -> EmergencyOrange
                else -> EmergencyAccent
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openCompatibilityModal(stock.bloodGroup) }
                    .testTag("stock_card_${stock.bloodGroup}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                        BloodGroupBadge(
                            bloodGroup = stock.bloodGroup,
                            size = 46,
                            fontSize = 17
                        )

                        IconButton(
                            onClick = { viewModel.openStockEditor(stock) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit stock units",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${stock.unitsAvailable}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Units",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    StockStatusBadge(status = stock.status)

                    // Compatible donors list
                    val donorsList = BloodGroup.getCompatibleDonors(stock.bloodGroup).joinToString(", ")
                    Text(
                        text = "Accepts: $donorsList",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                viewModel.selectBloodGroupFilter(stock.bloodGroup)
                                viewModel.navigateTo(AppScreen.FIND_DONOR)
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Find Donors", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = { viewModel.openCompatibilityModal(stock.bloodGroup) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Compatibility Info",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- 3. BLOOD COMPATIBILITY MATRIX INFO ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compatibility_info_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Quick Blood Compatibility Rules",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BloodGroupBadge(bloodGroup = "O-", size = 32, fontSize = 12)
                        Column {
                            Text(
                                text = "Universal Red Blood Cell Donor",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Can be safely given to any patient in severe trauma when time is limited.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BloodGroupBadge(bloodGroup = "AB+", size = 32, fontSize = 12)
                        Column {
                            Text(
                                text = "Universal Recipient",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Can receive red blood cells from any blood group safely.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
