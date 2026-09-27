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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodGroup
import com.example.ui.components.BloodGroupBadge
import com.example.ui.theme.BloodRedContainer
import com.example.ui.theme.BloodRedOnContainer
import com.example.ui.theme.BloodRedPrimary
import com.example.ui.theme.StatusAvailableGreen
import com.example.ui.theme.StatusAvailableGreenContainer
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BloodViewModel
import com.example.ui.viewmodel.DonorRegistrationFormState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterDonorScreen(
    viewModel: BloodViewModel,
    formState: DonorRegistrationFormState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("register_donor_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. TITLE & HERO CARD ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Donor Registration",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Register to the emergency network. Your blood donation can save up to three lives.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- 2. SUCCESS CONFIRMATION STATE ---
        if (formState.isSuccess) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("donor_success_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusAvailableGreenContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(StatusAvailableGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = "Registration Complete!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = StatusAvailableGreen
                        )

                        Text(
                            text = "Thank you! You are now enrolled as a verified blood donor in the emergency alert registry.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.resetDonorForm()
                                    viewModel.navigateTo(AppScreen.FIND_DONOR)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BloodRedPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("View Donors")
                            }

                            OutlinedButton(
                                onClick = { viewModel.resetDonorForm() },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Register Another")
                            }
                        }
                    }
                }
            }
        } else {
            // --- 3. REGISTRATION FORM FIELDS ---
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("donor_form_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Full Name
                        OutlinedTextField(
                            value = formState.fullName,
                            onValueChange = { viewModel.onDonorFullNameChange(it) },
                            label = { Text("Full Name *") },
                            placeholder = { Text("e.g. John Doe, MD") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = BloodRedPrimary)
                            },
                            isError = formState.errors.containsKey("fullName"),
                            supportingText = {
                                formState.errors["fullName"]?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("donor_form_name_input")
                        )

                        // Blood Group Selection Chips
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Blood Group *",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BloodGroup.allLabels.forEach { group ->
                                    val isSelected = formState.bloodGroup == group
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.onDonorBloodGroupChange(group) },
                                        label = { Text(group, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                                        leadingIcon = if (isSelected) {
                                            {
                                                Icon(
                                                    Icons.Default.Favorite,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BloodRedPrimary,
                                            selectedLabelColor = Color.White
                                        ),
                                        modifier = Modifier.testTag("form_blood_group_$group")
                                    )
                                }
                            }
                        }

                        // Phone Number
                        OutlinedTextField(
                            value = formState.phoneNumber,
                            onValueChange = { viewModel.onDonorPhoneChange(it) },
                            label = { Text("Phone Number *") },
                            placeholder = { Text("+1 (555) 000-0000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = BloodRedPrimary)
                            },
                            isError = formState.errors.containsKey("phoneNumber"),
                            supportingText = {
                                formState.errors["phoneNumber"]?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("donor_form_phone_input")
                        )

                        // Email
                        OutlinedTextField(
                            value = formState.email,
                            onValueChange = { viewModel.onDonorEmailChange(it) },
                            label = { Text("Email Address (Optional)") },
                            placeholder = { Text("john.doe@example.com") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = BloodRedPrimary)
                            },
                            isError = formState.errors.containsKey("email"),
                            supportingText = {
                                formState.errors["email"]?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("donor_form_email_input")
                        )

                        // City / Location
                        OutlinedTextField(
                            value = formState.city,
                            onValueChange = { viewModel.onDonorCityChange(it) },
                            label = { Text("City / Location *") },
                            placeholder = { Text("e.g. New York, NY") },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = BloodRedPrimary)
                            },
                            isError = formState.errors.containsKey("city"),
                            supportingText = {
                                formState.errors["city"]?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("donor_form_city_input")
                        )

                        // Last Donation Date
                        OutlinedTextField(
                            value = formState.lastDonationDate,
                            onValueChange = { viewModel.onDonorLastDonationChange(it) },
                            label = { Text("Last Donation Date (Optional)") },
                            placeholder = { Text("YYYY-MM-DD or Never") },
                            leadingIcon = {
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = BloodRedPrimary)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("donor_form_date_input")
                        )

                        // Availability Toggle
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Immediate Availability",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "I am ready and healthy to donate if an emergency occurs.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = formState.isAvailable,
                                    onCheckedChange = { viewModel.onDonorAvailabilityChange(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = BloodRedPrimary),
                                    modifier = Modifier.testTag("donor_form_availability_switch")
                                )
                            }
                        }

                        // Submit Button
                        Button(
                            onClick = { viewModel.submitDonorRegistration() },
                            colors = ButtonDefaults.buttonColors(containerColor = BloodRedPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("donor_form_submit_btn")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Complete Donor Registration", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}
