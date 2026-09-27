package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BloodDatabase
import com.example.data.model.BloodGroup
import com.example.data.model.BloodRequestEntity
import com.example.data.model.BloodStockEntity
import com.example.data.model.DonorEntity
import com.example.data.repository.BloodRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen(val title: String) {
    HOME("Home"),
    AVAILABILITY("Blood Stock"),
    FIND_DONOR("Find Donors"),
    REGISTER_DONOR("Register"),
    EMERGENCY_REQUEST("Emergency SOS")
}

data class DonorRegistrationFormState(
    val fullName: String = "",
    val bloodGroup: String = "O+",
    val phoneNumber: String = "",
    val email: String = "",
    val city: String = "",
    val isAvailable: Boolean = true,
    val lastDonationDate: String = "",
    val errors: Map<String, String> = emptyMap(),
    val isSuccess: Boolean = false
)

data class BloodRequestFormState(
    val patientName: String = "",
    val bloodGroup: String = "O+",
    val hospitalName: String = "",
    val city: String = "",
    val contactNumber: String = "",
    val emergencyLevel: String = "CRITICAL",
    val unitsNeeded: Int = 1,
    val notes: String = "",
    val errors: Map<String, String> = emptyMap(),
    val isSuccess: Boolean = false
)

class BloodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BloodRepository

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Search and filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedBloodGroupFilter = MutableStateFlow<String?>(null)
    val selectedBloodGroupFilter: StateFlow<String?> = _selectedBloodGroupFilter.asStateFlow()

    private val _onlyAvailableFilter = MutableStateFlow(false)
    val onlyAvailableFilter: StateFlow<Boolean> = _onlyAvailableFilter.asStateFlow()

    private val _emergencyFilterLevel = MutableStateFlow<String?>(null)
    val emergencyFilterLevel: StateFlow<String?> = _emergencyFilterLevel.asStateFlow()

    // Forms
    private val _donorForm = MutableStateFlow(DonorRegistrationFormState())
    val donorForm: StateFlow<DonorRegistrationFormState> = _donorForm.asStateFlow()

    private val _requestForm = MutableStateFlow(BloodRequestFormState())
    val requestForm: StateFlow<BloodRequestFormState> = _requestForm.asStateFlow()

    // Dialog & Sheets
    private val _showQuickEmergencyDialog = MutableStateFlow(false)
    val showQuickEmergencyDialog: StateFlow<Boolean> = _showQuickEmergencyDialog.asStateFlow()

    private val _selectedStockForEdit = MutableStateFlow<BloodStockEntity?>(null)
    val selectedStockForEdit: StateFlow<BloodStockEntity?> = _selectedStockForEdit.asStateFlow()

    private val _selectedBloodGroupForCompatibility = MutableStateFlow<String?>(null)
    val selectedBloodGroupForCompatibility: StateFlow<String?> = _selectedBloodGroupForCompatibility.asStateFlow()

    // UI Feedback Event
    private val _snackBarMessage = MutableSharedFlow<String>()
    val snackBarMessage: SharedFlow<String> = _snackBarMessage.asSharedFlow()

    init {
        val database = BloodDatabase.getInstance(application)
        repository = BloodRepository(database.bloodDao(), database)

        viewModelScope.launch {
            repository.checkAndInitializeData()
        }
    }

    // Raw flows
    val allStocks: StateFlow<List<BloodStockEntity>> = repository.allStocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawDonors: StateFlow<List<DonorEntity>> = repository.allDonors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawRequests: StateFlow<List<BloodRequestEntity>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered donors flow
    val filteredDonors: StateFlow<List<DonorEntity>> = combine(
        rawDonors,
        _searchQuery,
        _selectedBloodGroupFilter,
        _onlyAvailableFilter
    ) { donors, query, bloodGroup, onlyAvailable ->
        donors.filter { donor ->
            val matchesQuery = query.isBlank() ||
                donor.fullName.contains(query, ignoreCase = true) ||
                donor.city.contains(query, ignoreCase = true) ||
                donor.bloodGroup.contains(query, ignoreCase = true)

            val matchesGroup = bloodGroup == null || donor.bloodGroup.equals(bloodGroup, ignoreCase = true)
            val matchesAvailable = !onlyAvailable || donor.isAvailable

            matchesQuery && matchesGroup && matchesAvailable
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered requests flow
    val filteredRequests: StateFlow<List<BloodRequestEntity>> = combine(
        rawRequests,
        _searchQuery,
        _selectedBloodGroupFilter,
        _emergencyFilterLevel
    ) { requests, query, bloodGroup, emergencyLevel ->
        requests.filter { request ->
            val matchesQuery = query.isBlank() ||
                request.patientName.contains(query, ignoreCase = true) ||
                request.hospitalName.contains(query, ignoreCase = true) ||
                request.city.contains(query, ignoreCase = true) ||
                request.bloodGroup.contains(query, ignoreCase = true)

            val matchesGroup = bloodGroup == null || request.bloodGroup.equals(bloodGroup, ignoreCase = true)
            val matchesEmergency = emergencyLevel == null || request.emergencyLevel.equals(emergencyLevel, ignoreCase = true)

            matchesQuery && matchesGroup && matchesEmergency
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- NAVIGATION ---
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // --- SEARCH & FILTER ACTIONS ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectBloodGroupFilter(bloodGroup: String?) {
        _selectedBloodGroupFilter.value = if (_selectedBloodGroupFilter.value == bloodGroup) null else bloodGroup
    }

    fun toggleOnlyAvailableFilter() {
        _onlyAvailableFilter.value = !_onlyAvailableFilter.value
    }

    fun selectEmergencyFilterLevel(level: String?) {
        _emergencyFilterLevel.value = if (_emergencyFilterLevel.value == level) null else level
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedBloodGroupFilter.value = null
        _onlyAvailableFilter.value = false
        _emergencyFilterLevel.value = null
    }

    // --- QUICK EMERGENCY SEARCH MODAL ---
    fun openQuickEmergencyDialog() {
        _showQuickEmergencyDialog.value = true
    }

    fun closeQuickEmergencyDialog() {
        _showQuickEmergencyDialog.value = false
    }

    fun performQuickEmergencySearch(bloodGroup: String, city: String) {
        _selectedBloodGroupFilter.value = bloodGroup
        _searchQuery.value = city
        _showQuickEmergencyDialog.value = false
        _currentScreen.value = AppScreen.FIND_DONOR
    }

    // --- COMPATIBILITY INFO MODAL ---
    fun openCompatibilityModal(bloodGroup: String) {
        _selectedBloodGroupForCompatibility.value = bloodGroup
    }

    fun closeCompatibilityModal() {
        _selectedBloodGroupForCompatibility.value = null
    }

    // --- STOCK MANAGEMENT ---
    fun openStockEditor(stock: BloodStockEntity) {
        _selectedStockForEdit.value = stock
    }

    fun closeStockEditor() {
        _selectedStockForEdit.value = null
    }

    fun updateStockUnits(bloodGroup: String, units: Int) {
        viewModelScope.launch {
            repository.updateStockUnits(bloodGroup, units)
            _selectedStockForEdit.value = null
            _snackBarMessage.emit("Updated inventory for $bloodGroup to $units units")
        }
    }

    // --- DONOR ACTIONS ---
    fun toggleDonorAvailability(donor: DonorEntity) {
        viewModelScope.launch {
            val newStatus = !donor.isAvailable
            repository.setDonorAvailability(donor.id, newStatus)
            _snackBarMessage.emit("${donor.fullName} is now marked as ${if (newStatus) "Available" else "Unavailable"}")
        }
    }

    fun deleteDonor(donor: DonorEntity) {
        viewModelScope.launch {
            repository.deleteDonor(donor.id)
            _snackBarMessage.emit("Donor profile for ${donor.fullName} removed")
        }
    }

    // --- DONOR FORM ACTIONS ---
    fun onDonorFullNameChange(value: String) {
        _donorForm.value = _donorForm.value.copy(
            fullName = value,
            errors = _donorForm.value.errors - "fullName"
        )
    }

    fun onDonorBloodGroupChange(value: String) {
        _donorForm.value = _donorForm.value.copy(bloodGroup = value)
    }

    fun onDonorPhoneChange(value: String) {
        _donorForm.value = _donorForm.value.copy(
            phoneNumber = value,
            errors = _donorForm.value.errors - "phoneNumber"
        )
    }

    fun onDonorEmailChange(value: String) {
        _donorForm.value = _donorForm.value.copy(
            email = value,
            errors = _donorForm.value.errors - "email"
        )
    }

    fun onDonorCityChange(value: String) {
        _donorForm.value = _donorForm.value.copy(
            city = value,
            errors = _donorForm.value.errors - "city"
        )
    }

    fun onDonorAvailabilityChange(value: Boolean) {
        _donorForm.value = _donorForm.value.copy(isAvailable = value)
    }

    fun onDonorLastDonationChange(value: String) {
        _donorForm.value = _donorForm.value.copy(lastDonationDate = value)
    }

    fun resetDonorForm() {
        _donorForm.value = DonorRegistrationFormState()
    }

    fun submitDonorRegistration() {
        val form = _donorForm.value
        val errors = mutableMapOf<String, String>()

        if (form.fullName.trim().length < 2) {
            errors["fullName"] = "Please enter your full name"
        }
        if (form.phoneNumber.trim().length < 7) {
            errors["phoneNumber"] = "Valid phone number is required"
        }
        if (form.email.trim().isNotEmpty() && !form.email.contains("@")) {
            errors["email"] = "Please enter a valid email address"
        }
        if (form.city.trim().length < 2) {
            errors["city"] = "Please specify city or location"
        }

        if (errors.isNotEmpty()) {
            _donorForm.value = form.copy(errors = errors)
            viewModelScope.launch {
                _snackBarMessage.emit("Please fix form errors before submitting")
            }
            return
        }

        viewModelScope.launch {
            val newDonor = DonorEntity(
                fullName = form.fullName.trim(),
                bloodGroup = form.bloodGroup,
                phoneNumber = form.phoneNumber.trim(),
                email = form.email.trim(),
                city = form.city.trim(),
                isAvailable = form.isAvailable,
                lastDonationDate = form.lastDonationDate.trim()
            )
            repository.registerDonor(newDonor)
            _donorForm.value = form.copy(isSuccess = true, errors = emptyMap())
            _snackBarMessage.emit("Registered successfully as a verified blood donor!")
        }
    }

    // --- REQUEST ACTIONS ---
    fun toggleRequestFulfilled(request: BloodRequestEntity) {
        viewModelScope.launch {
            val newStatus = !request.isFulfilled
            repository.setRequestFulfilled(request.id, newStatus)
            _snackBarMessage.emit("Blood request for ${request.patientName} marked as ${if (newStatus) "Fulfilled" else "Active"}")
        }
    }

    fun deleteBloodRequest(request: BloodRequestEntity) {
        viewModelScope.launch {
            repository.deleteRequest(request.id)
            _snackBarMessage.emit("Request deleted")
        }
    }

    // --- REQUEST FORM ACTIONS ---
    fun onRequestPatientNameChange(value: String) {
        _requestForm.value = _requestForm.value.copy(
            patientName = value,
            errors = _requestForm.value.errors - "patientName"
        )
    }

    fun onRequestBloodGroupChange(value: String) {
        _requestForm.value = _requestForm.value.copy(bloodGroup = value)
    }

    fun onRequestHospitalNameChange(value: String) {
        _requestForm.value = _requestForm.value.copy(
            hospitalName = value,
            errors = _requestForm.value.errors - "hospitalName"
        )
    }

    fun onRequestCityChange(value: String) {
        _requestForm.value = _requestForm.value.copy(
            city = value,
            errors = _requestForm.value.errors - "city"
        )
    }

    fun onRequestContactNumberChange(value: String) {
        _requestForm.value = _requestForm.value.copy(
            contactNumber = value,
            errors = _requestForm.value.errors - "contactNumber"
        )
    }

    fun onRequestEmergencyLevelChange(value: String) {
        _requestForm.value = _requestForm.value.copy(emergencyLevel = value)
    }

    fun onRequestUnitsNeededChange(value: Int) {
        _requestForm.value = _requestForm.value.copy(unitsNeeded = value.coerceIn(1, 20))
    }

    fun onRequestNotesChange(value: String) {
        _requestForm.value = _requestForm.value.copy(notes = value)
    }

    fun resetRequestForm() {
        _requestForm.value = BloodRequestFormState()
    }

    fun submitBloodRequest() {
        val form = _requestForm.value
        val errors = mutableMapOf<String, String>()

        if (form.patientName.trim().length < 2) {
            errors["patientName"] = "Patient name is required"
        }
        if (form.hospitalName.trim().length < 2) {
            errors["hospitalName"] = "Hospital name is required"
        }
        if (form.city.trim().length < 2) {
            errors["city"] = "Location or city is required"
        }
        if (form.contactNumber.trim().length < 7) {
            errors["contactNumber"] = "Valid contact number is required"
        }

        if (errors.isNotEmpty()) {
            _requestForm.value = form.copy(errors = errors)
            viewModelScope.launch {
                _snackBarMessage.emit("Please fill in all required fields")
            }
            return
        }

        viewModelScope.launch {
            val newRequest = BloodRequestEntity(
                patientName = form.patientName.trim(),
                bloodGroup = form.bloodGroup,
                hospitalName = form.hospitalName.trim(),
                city = form.city.trim(),
                contactNumber = form.contactNumber.trim(),
                emergencyLevel = form.emergencyLevel,
                unitsNeeded = form.unitsNeeded,
                notes = form.notes.trim(),
                isFulfilled = false
            )
            repository.createBloodRequest(newRequest)
            _requestForm.value = form.copy(isSuccess = true, errors = emptyMap())
            _snackBarMessage.emit("Emergency blood request broadcasted to donors in ${form.city}!")
        }
    }
}
