package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BloodCompatibilityDialog
import com.example.ui.components.EditStockDialog
import com.example.ui.components.QuickEmergencySearchDialog
import com.example.ui.screens.AvailabilityScreen
import com.example.ui.screens.EmergencyRequestScreen
import com.example.ui.screens.FindDonorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RegisterDonorScreen
import com.example.ui.theme.BloodRedPrimary
import com.example.ui.theme.EmergencyAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BloodViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: BloodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                BloodAppContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodAppContent(viewModel: BloodViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val stocks by viewModel.allStocks.collectAsStateWithLifecycle()
    val rawDonors by viewModel.rawDonors.collectAsStateWithLifecycle()
    val rawRequests by viewModel.rawRequests.collectAsStateWithLifecycle()

    val filteredDonors by viewModel.filteredDonors.collectAsStateWithLifecycle()
    val filteredRequests by viewModel.filteredRequests.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedBloodGroup by viewModel.selectedBloodGroupFilter.collectAsStateWithLifecycle()
    val onlyAvailable by viewModel.onlyAvailableFilter.collectAsStateWithLifecycle()
    val emergencyLevelFilter by viewModel.emergencyFilterLevel.collectAsStateWithLifecycle()

    val donorFormState by viewModel.donorForm.collectAsStateWithLifecycle()
    val requestFormState by viewModel.requestForm.collectAsStateWithLifecycle()

    val showQuickSearchDialog by viewModel.showQuickEmergencyDialog.collectAsStateWithLifecycle()
    val stockToEdit by viewModel.selectedStockForEdit.collectAsStateWithLifecycle()
    val bloodGroupForCompatibility by viewModel.selectedBloodGroupForCompatibility.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Collect snackbar messages
    LaunchedEffect(Unit) {
        viewModel.snackBarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Handle back button to return to Home if on a sub-screen
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.navigateTo(AppScreen.HOME)
    }

    val criticalRequestCount = rawRequests.count { !it.isFulfilled && it.emergencyLevel == "CRITICAL" }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (currentScreen) {
                            AppScreen.HOME -> "Blood Donor Network"
                            AppScreen.AVAILABILITY -> "Blood Availability"
                            AppScreen.FIND_DONOR -> "Emergency Donors"
                            AppScreen.REGISTER_DONOR -> "Donor Registration"
                            AppScreen.EMERGENCY_REQUEST -> "Emergency SOS"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openQuickEmergencyDialog() },
                        modifier = Modifier.testTag("app_bar_sos_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (criticalRequestCount > 0) {
                                    Badge(containerColor = EmergencyAccent) {
                                        Text("$criticalRequestCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Emergency Blood Search",
                                tint = EmergencyAccent
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                // 1. Home
                NavigationBarItem(
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodRedPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )

                // 2. Availability
                NavigationBarItem(
                    selected = currentScreen == AppScreen.AVAILABILITY,
                    onClick = { viewModel.navigateTo(AppScreen.AVAILABILITY) },
                    icon = { Icon(Icons.Default.LocalHospital, contentDescription = "Stock") },
                    label = { Text("Stock", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodRedPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_availability")
                )

                // 3. Find Donor
                NavigationBarItem(
                    selected = currentScreen == AppScreen.FIND_DONOR,
                    onClick = { viewModel.navigateTo(AppScreen.FIND_DONOR) },
                    icon = { Icon(Icons.Default.People, contentDescription = "Donors") },
                    label = { Text("Donors", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodRedPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_find_donor")
                )

                // 4. Register
                NavigationBarItem(
                    selected = currentScreen == AppScreen.REGISTER_DONOR,
                    onClick = { viewModel.navigateTo(AppScreen.REGISTER_DONOR) },
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Register") },
                    label = { Text("Register", fontSize = 11.sp, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BloodRedPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_register")
                )

                // 5. Emergency SOS
                NavigationBarItem(
                    selected = currentScreen == AppScreen.EMERGENCY_REQUEST,
                    onClick = { viewModel.navigateTo(AppScreen.EMERGENCY_REQUEST) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (criticalRequestCount > 0) {
                                    Badge(containerColor = EmergencyAccent) {
                                        Text("$criticalRequestCount")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = "Emergency")
                        }
                    },
                    label = { Text("SOS", fontSize = 11.sp, maxLines = 1, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmergencyAccent,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_emergency")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 840.dp)
            ) {
                when (currentScreen) {
                    AppScreen.HOME -> HomeScreen(
                        viewModel = viewModel,
                        stocks = stocks,
                        donors = rawDonors,
                        requests = rawRequests
                    )
                    AppScreen.AVAILABILITY -> AvailabilityScreen(
                        viewModel = viewModel,
                        stocks = stocks
                    )
                    AppScreen.FIND_DONOR -> FindDonorScreen(
                        viewModel = viewModel,
                        donors = filteredDonors,
                        searchQuery = searchQuery,
                        selectedBloodGroup = selectedBloodGroup,
                        onlyAvailable = onlyAvailable
                    )
                    AppScreen.REGISTER_DONOR -> RegisterDonorScreen(
                        viewModel = viewModel,
                        formState = donorFormState
                    )
                    AppScreen.EMERGENCY_REQUEST -> EmergencyRequestScreen(
                        viewModel = viewModel,
                        requests = filteredRequests,
                        formState = requestFormState,
                        selectedEmergencyLevel = emergencyLevelFilter,
                        searchQuery = searchQuery
                    )
                }
            }
        }
    }

    // --- MODALS AND DIALOGS ---
    if (showQuickSearchDialog) {
        QuickEmergencySearchDialog(
            onDismiss = { viewModel.closeQuickEmergencyDialog() },
            onSearch = { group, city ->
                viewModel.performQuickEmergencySearch(group, city)
            }
        )
    }

    stockToEdit?.let { stock ->
        EditStockDialog(
            stock = stock,
            onDismiss = { viewModel.closeStockEditor() },
            onSave = { group, units ->
                viewModel.updateStockUnits(group, units)
            }
        )
    }

    bloodGroupForCompatibility?.let { group ->
        BloodCompatibilityDialog(
            bloodGroup = group,
            onDismiss = { viewModel.closeCompatibilityModal() }
        )
    }
}
