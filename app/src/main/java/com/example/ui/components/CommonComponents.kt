package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodGroup
import com.example.data.model.BloodStockEntity
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.BloodRedOnContainer
import com.example.ui.theme.BloodRedPrimary
import com.example.ui.theme.EmergencyAccent
import com.example.ui.theme.EmergencyOrange
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusAvailableGreenContainer

// --- INTENT HELPERS ---
fun dialPhoneNumber(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phoneNumber.filter { it.isDigit() || it == '+' }}")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

fun sendSmsMessage(context: Context, phoneNumber: String, messageText: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${phoneNumber.filter { it.isDigit() || it == '+' }}")
            putExtra("sms_body", messageText)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

fun shareText(context: Context, subject: String, content: String) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Emergency Blood Request")
        context.startActivity(shareIntent)
    } catch (_: Exception) {
    }
}

// --- BLOOD GROUP BADGE ---
@Composable
fun BloodGroupBadge(
    bloodGroup: String,
    modifier: Modifier = Modifier,
    size: Int = 44,
    fontSize: Int = 16
) {
    val isUniversal = bloodGroup == "O-" || bloodGroup == "AB+"
    val backgroundColor = if (isUniversal) EmergencyAccent else BloodRedPrimary

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bloodGroup,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center
        )
    }
}

// --- EMERGENCY BADGE ---
@Composable
fun EmergencyBadge(
    level: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (level.uppercase()) {
        "CRITICAL" -> Triple(EmergencyAccent, Color.White, "CRITICAL SOS")
        "URGENT" -> Triple(EmergencyOrange, Color.White, "URGENT")
        else -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "STANDARD")
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (level.uppercase() == "CRITICAL") 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Surface(
        modifier = modifier.scale(pulseScale),
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (level.uppercase() == "CRITICAL") {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// --- STATUS BADGE (AVAILABLE / LOW / CRITICAL) ---
@Composable
fun StockStatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "AVAILABLE" -> Triple(StatusAvailableGreenContainer, StatusAvailableGreen, "Available")
        "LOW" -> Triple(Color(0xFFFFF3E0), EmergencyOrange, "Low Stock")
        else -> Triple(BloodRedContainer, BloodRedOnContainer, "Critical Deficit")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// --- QUICK EMERGENCY SEARCH DIALOG ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickEmergencySearchDialog(
    onDismiss: () -> Unit,
    onSearch: (bloodGroup: String, city: String) -> Unit
) {
    var selectedGroup by remember { mutableStateOf("O-") }
    var cityInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = EmergencyAccent
                )
                Text(
                    text = "Emergency Donor Search",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Select required blood group immediately:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BloodGroup.allLabels.forEach { group ->
                        FilterChip(
                            selected = selectedGroup == group,
                            onClick = { selectedGroup = group },
                            label = { Text(group, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BloodRedPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("emergency_chip_$group")
                        )
                    }
                }

                OutlinedTextField(
                    value = cityInput,
                    onValueChange = { cityInput = it },
                    label = { Text("City or Hospital Area") },
                    placeholder = { Text("e.g. New York, Chicago, Houston") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = BloodRedPrimary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("emergency_city_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSearch(selectedGroup, cityInput.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyAccent),
                modifier = Modifier.testTag("emergency_search_confirm_btn")
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Search Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// --- BLOOD COMPATIBILITY DIALOG ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BloodCompatibilityDialog(
    bloodGroup: String,
    onDismiss: () -> Unit
) {
    val canReceive = BloodGroup.getCompatibleDonors(bloodGroup)
    val canGive = BloodGroup.getCompatibleRecipients(bloodGroup)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BloodGroupBadge(bloodGroup = bloodGroup, size = 38, fontSize = 14)
                Column {
                    Text(
                        text = "Compatibility: $bloodGroup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (bloodGroup) {
                            "O-" -> "Universal Red Cell Donor"
                            "AB+" -> "Universal Plasma & Cell Recipient"
                            "O+" -> "Most In-Demand Blood Type"
                            else -> "Targeted Compatibility"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Can Receive From
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Can Receive Blood From:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = StatusAvailableGreen
                        )
                        Spacer(Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            canReceive.forEach { g ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StatusAvailableGreenContainer,
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        text = g,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Bold,
                                        color = StatusAvailableGreen,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Can Donate To
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Can Safely Donate To:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = BloodRedPrimary
                        )
                        Spacer(Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            canGive.forEach { g ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BloodRedContainer,
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        text = g,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Bold,
                                        color = BloodRedOnContainer,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Got It")
            }
        }
    )
}

// --- STOCK EDIT DIALOG ---
@Composable
fun EditStockDialog(
    stock: BloodStockEntity,
    onDismiss: () -> Unit,
    onSave: (bloodGroup: String, units: Int) -> Unit
) {
    var units by remember { mutableIntStateOf(stock.unitsAvailable) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Update Stock: ${stock.bloodGroup}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current registered inventory for blood group ${stock.bloodGroup}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { if (units > 0) units -= 1 },
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "$units Units",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = BloodRedPrimary
                    )

                    Button(
                        onClick = { units += 1 },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = BloodRedPrimary),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(stock.bloodGroup, units) },
                colors = ButtonDefaults.buttonColors(containerColor = BloodRedPrimary)
            ) {
                Text("Save Units")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
