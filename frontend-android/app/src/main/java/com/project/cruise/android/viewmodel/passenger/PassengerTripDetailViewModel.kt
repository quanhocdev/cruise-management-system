package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.passenger.tripdetail.*
import com.project.cruise.android.data.repository.passenger.PassengerTripDetailRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TripSectionKey(val key: String, val label: String) {
    TRACKING("tracking", "Theo dõi hành trình"),
    ITINERARY("itinerary", "Lịch trình"),
    CRUISE("cruise", "Du thuyền"),
    FACILITIES("facilities", "Loại hình phục vụ"),
    BOOKING("booking", "Đơn đặt"),
    PASSENGERS("passengers", "Hành khách"),
    BENEFITS("benefits", "Quyền lợi"),
    USAGES("usages", "Lượt sử dụng"),
    BILLS("bills", "Hóa đơn")
}

data class PassengerTripDetailUiState(
    val activeSection: TripSectionKey = TripSectionKey.TRACKING,

    // Route & Vessel & Weather
    val routeOverview: TripRouteOverviewDto? = null,
    val routeLegs: List<TripRouteLegDto> = emptyList(),
    val routeLoading: Boolean = false,
    val routeError: String? = null,

    val vesselLocation: VesselLocationDto? = null,
    val vesselLoading: Boolean = false,
    val vesselError: String? = null,

    val weather: TripWeatherDto? = null,

    // Itinerary
    val schedules: List<TripScheduleDto> = emptyList(),
    val itineraryLoading: Boolean = false,
    val itineraryError: String? = null,

    // Cruise & Decks & Areas
    val cruise: TripCruiseDto? = null,
    val decks: List<TripCruiseDeckDto> = emptyList(),
    val deckAreas: Map<String, List<TripCruiseDeckAreaDto>> = emptyMap(),
    val cruiseLoading: Boolean = false,
    val cruiseError: String? = null,
    val areasLoading: Boolean = false,
    val areasError: String? = null,

    // Facilities & Packages
    val products: List<TripProductDto> = emptyList(),
    val services: List<TripServiceDto> = emptyList(),
    val cruiseActivities: List<TripOnboardActivityDto> = emptyList(),
    val visits: List<TripVisitActivityDto> = emptyList(),
    val packages: List<TripPackageDto> = emptyList(),
    val facilitiesLoading: Boolean = false,
    val facilitiesError: String? = null,

    // Booking
    val booking: PassengerBookingDetailDto? = null,
    val bookingLoading: Boolean = false,
    val bookingError: String? = null,

    // Passengers
    val passengers: List<BookingPassengerItemDto> = emptyList(),
    val passengersLoading: Boolean = false,
    val passengersError: String? = null,

    // Benefits (đối chiếu từ booking.tourPackageId + packages + facilities)
    val matchedPackage: TripPackageDto? = null,
    val resolvedBenefits: List<ResolvedBenefitUiModel> = emptyList(),

    // Usages
    val productUsages: List<TripUsageItemDto> = emptyList(),
    val serviceUsages: List<TripUsageItemDto> = emptyList(),
    val activityCruiseUsages: List<TripUsageItemDto> = emptyList(),
    val visitUsages: List<TripUsageItemDto> = emptyList(),
    val usagesLoading: Boolean = false,
    val usagesError: String? = null,

    // Bills
    val bills: List<TripBillDto> = emptyList(),
    val billsLoading: Boolean = false,
    val billsError: String? = null
)

class PassengerTripDetailViewModel(
    private val repository: PassengerTripDetailRepository
) : ViewModel() {

    companion object {
        private const val GPS_POLLING_INTERVAL_MS = 10_000L
    }

    private val _uiState = MutableStateFlow(PassengerTripDetailUiState())
    val uiState: StateFlow<PassengerTripDetailUiState> = _uiState.asStateFlow()

    private val loadedSections = mutableSetOf<TripSectionKey>()
    private var vesselPollingJob: Job? = null
    private var currentTourId: String? = null
    private var currentBookingId: Long? = null

    fun bindContext(tourId: String?, bookingId: Long?) {
        val contextChanged = currentTourId != tourId || currentBookingId != bookingId
        currentTourId = tourId
        currentBookingId = bookingId

        if (contextChanged) {
            loadedSections.clear()
            _uiState.value = PassengerTripDetailUiState(activeSection = _uiState.value.activeSection)
            updateVesselPollingState()
        }
    }

    fun selectSection(section: TripSectionKey, tourId: String?, bookingId: Long?) {
        bindContext(tourId, bookingId)
        _uiState.update { it.copy(activeSection = section) }
        updateVesselPollingState()
        loadSection(section, tourId = tourId, bookingId = bookingId, force = false)

        // Tải kèm gói & danh mục dịch vụ khi mở booking/benefits để map tên gói và quyền lợi
        if (section == TripSectionKey.BOOKING || section == TripSectionKey.BENEFITS) {
            loadSection(TripSectionKey.FACILITIES, tourId = tourId, bookingId = bookingId, force = false)
        }
    }

    fun loadSection(
        section: TripSectionKey,
        tourId: String?,
        bookingId: Long?,
        force: Boolean = false
    ) {
        bindContext(tourId, bookingId)
        if (!force && loadedSections.contains(section)) return

        when (section) {
            TripSectionKey.TRACKING -> loadTracking(tourId)
            TripSectionKey.ITINERARY -> loadItinerary(tourId)
            TripSectionKey.CRUISE -> loadCruise(tourId)
            TripSectionKey.FACILITIES -> loadFacilitiesAndPackages(tourId)
            TripSectionKey.BOOKING -> loadBooking(bookingId)
            TripSectionKey.PASSENGERS -> loadPassengers(bookingId)
            TripSectionKey.BENEFITS -> {
                loadBooking(bookingId)
                loadFacilitiesAndPackages(tourId)
            }
            TripSectionKey.USAGES -> loadUsages(bookingId)
            TripSectionKey.BILLS -> loadBills(bookingId)
        }
    }

    fun reloadSection(section: TripSectionKey, tourId: String?, bookingId: Long?) {
        loadedSections.remove(section)
        if (section == TripSectionKey.BOOKING || section == TripSectionKey.BENEFITS) {
            loadedSections.remove(TripSectionKey.FACILITIES)
        }
        loadSection(section, tourId = tourId, bookingId = bookingId, force = true)
    }

    fun loadDeckAreas(tourId: String?, deckId: String) {
        if (tourId.isNullOrBlank() || deckId.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(areasLoading = true, areasError = null) }
            try {
                val areas = repository.getCruiseDeckAreas(tourId, deckId)
                _uiState.update { state ->
                    state.copy(
                        deckAreas = state.deckAreas + (deckId to areas),
                        areasLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        areasLoading = false,
                        areasError = e.message ?: "Không thể tải khu vực của tầng tàu."
                    )
                }
            }
        }
    }

    private fun updateVesselPollingState() {
        vesselPollingJob?.cancel()
        vesselPollingJob = null

        val tourId = currentTourId
        val shouldPoll = _uiState.value.activeSection == TripSectionKey.TRACKING && !tourId.isNullOrBlank()
        if (!shouldPoll) return

        vesselPollingJob = viewModelScope.launch {
            while (true) {
                fetchVesselOnce(tourId)
                delay(GPS_POLLING_INTERVAL_MS)
            }
        }
    }

    private suspend fun fetchVesselOnce(tourId: String) {
        _uiState.update { it.copy(vesselLoading = it.vesselLocation == null, vesselError = null) }
        try {
            val location = repository.getVesselLocation(tourId)
            _uiState.update {
                it.copy(
                    vesselLocation = location,
                    vesselLoading = false,
                    vesselError = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    vesselLoading = false,
                    vesselError = e.message ?: "Không thể cập nhật vị trí tàu."
                )
            }
        }
    }

    private fun loadTracking(tourId: String?) {
        if (tourId.isNullOrBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(routeLoading = true, routeError = null) }
            try {
                val overview = runCatching { repository.getRouteOverview(tourId) }.getOrNull()
                val legs = runCatching { repository.getRouteLegs(tourId) }.getOrDefault(emptyList())

                val lat = overview?.departurePort?.latitude ?: overview?.currentLatitude
                val lon = overview?.departurePort?.longitude ?: overview?.currentLongitude
                val weatherData = if (lat != null && lon != null) {
                    runCatching {
                        repository.getWeather(lat, lon, overview?.startDate, overview?.endDate)
                    }.getOrNull()
                } else {
                    null
                }

                loadedSections.add(TripSectionKey.TRACKING)
                _uiState.update {
                    it.copy(
                        routeOverview = overview,
                        routeLegs = legs,
                        weather = weatherData,
                        routeLoading = false,
                        routeError = if (overview == null && legs.isEmpty()) "Không thể tải dữ liệu hành trình." else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        routeLoading = false,
                        routeError = e.message ?: "Không thể tải dữ liệu hành trình."
                    )
                }
            }
        }
    }

    private fun loadItinerary(tourId: String?) {
        if (tourId.isNullOrBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(itineraryLoading = true, itineraryError = null) }
            try {
                val schedules = repository.getItinerary(tourId)
                loadedSections.add(TripSectionKey.ITINERARY)
                _uiState.update {
                    it.copy(
                        schedules = schedules,
                        itineraryLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        itineraryLoading = false,
                        itineraryError = e.message ?: "Không thể tải lịch trình."
                    )
                }
            }
        }
    }

    private fun loadCruise(tourId: String?) {
        if (tourId.isNullOrBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(cruiseLoading = true, cruiseError = null) }
            try {
                val cruise = runCatching { repository.getCruise(tourId) }.getOrNull()
                val decks = runCatching { repository.getCruiseDecks(tourId) }.getOrDefault(emptyList())
                loadedSections.add(TripSectionKey.CRUISE)
                _uiState.update {
                    it.copy(
                        cruise = cruise,
                        decks = decks,
                        cruiseLoading = false,
                        cruiseError = if (cruise == null && decks.isEmpty()) "Không thể tải thông tin du thuyền." else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        cruiseLoading = false,
                        cruiseError = e.message ?: "Không thể tải thông tin du thuyền."
                    )
                }
            }
        }
    }

    private fun loadFacilitiesAndPackages(tourId: String?) {
        if (tourId.isNullOrBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(facilitiesLoading = true, facilitiesError = null) }
            try {
                val products = runCatching { repository.getProducts(tourId) }.getOrDefault(emptyList())
                val services = runCatching { repository.getServices(tourId) }.getOrDefault(emptyList())
                val activities = runCatching { repository.getCruiseActivities(tourId) }.getOrDefault(emptyList())
                val visits = runCatching { repository.getVisits(tourId) }.getOrDefault(emptyList())
                val packages = runCatching { repository.getPackages(tourId) }.getOrDefault(emptyList())

                loadedSections.add(TripSectionKey.FACILITIES)
                _uiState.update { state ->
                    val (matchedPkg, resolvedBens) = resolveBookingPackageBenefits(
                        booking = state.booking,
                        packages = packages,
                        products = products,
                        services = services,
                        cruiseActivities = activities,
                        visits = visits
                    )
                    state.copy(
                        products = products,
                        services = services,
                        cruiseActivities = activities,
                        visits = visits,
                        packages = packages,
                        matchedPackage = matchedPkg,
                        resolvedBenefits = resolvedBens,
                        facilitiesLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        facilitiesLoading = false,
                        facilitiesError = e.message ?: "Không thể tải tiện ích và gói tour."
                    )
                }
            }
        }
    }

    private fun loadBooking(bookingId: Long?) {
        if (bookingId == null || bookingId <= 0L) return
        viewModelScope.launch {
            _uiState.update { it.copy(bookingLoading = true, bookingError = null) }
            try {
                val detail = repository.getBookingTripDetail(bookingId)
                loadedSections.add(TripSectionKey.BOOKING)
                _uiState.update { state ->
                    val (matchedPkg, resolvedBens) = resolveBookingPackageBenefits(
                        booking = detail,
                        packages = state.packages,
                        products = state.products,
                        services = state.services,
                        cruiseActivities = state.cruiseActivities,
                        visits = state.visits
                    )
                    state.copy(
                        booking = detail,
                        matchedPackage = matchedPkg,
                        resolvedBenefits = resolvedBens,
                        bookingLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        bookingLoading = false,
                        bookingError = e.message ?: "Không thể tải thông tin đơn đặt chỗ."
                    )
                }
            }
        }
    }

    private fun loadPassengers(bookingId: Long?) {
        if (bookingId == null || bookingId <= 0L) return
        viewModelScope.launch {
            _uiState.update { it.copy(passengersLoading = true, passengersError = null) }
            try {
                val list = repository.getBookingPassengers(bookingId)
                loadedSections.add(TripSectionKey.PASSENGERS)
                _uiState.update {
                    it.copy(
                        passengers = list,
                        passengersLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        passengersLoading = false,
                        passengersError = e.message ?: "Không thể tải danh sách hành khách."
                    )
                }
            }
        }
    }

    private fun loadUsages(bookingId: Long?) {
        if (bookingId == null || bookingId <= 0L) return
        viewModelScope.launch {
            _uiState.update { it.copy(usagesLoading = true, usagesError = null) }
            try {
                val prodUsages = runCatching { repository.getProductUsages(bookingId) }.getOrDefault(emptyList())
                val servUsages = runCatching { repository.getServiceUsages(bookingId) }.getOrDefault(emptyList())
                val actUsages = runCatching { repository.getCruiseActivityUsages(bookingId) }.getOrDefault(emptyList())
                val visUsages = runCatching { repository.getVisitUsages(bookingId) }.getOrDefault(emptyList())

                loadedSections.add(TripSectionKey.USAGES)
                _uiState.update {
                    it.copy(
                        productUsages = prodUsages,
                        serviceUsages = servUsages,
                        activityCruiseUsages = actUsages,
                        visitUsages = visUsages,
                        usagesLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        usagesLoading = false,
                        usagesError = e.message ?: "Không thể tải lượt sử dụng."
                    )
                }
            }
        }
    }

    private fun loadBills(bookingId: Long?) {
        if (bookingId == null || bookingId <= 0L) return
        viewModelScope.launch {
            _uiState.update { it.copy(billsLoading = true, billsError = null) }
            try {
                val billList = repository.getBills(bookingId)
                loadedSections.add(TripSectionKey.BILLS)
                _uiState.update {
                    it.copy(
                        bills = billList,
                        billsLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        billsLoading = false,
                        billsError = e.message ?: "Không thể tải danh sách hóa đơn."
                    )
                }
            }
        }
    }

    override fun onCleared() {
        vesselPollingJob?.cancel()
        super.onCleared()
    }

    class Factory(
        private val repository: PassengerTripDetailRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PassengerTripDetailViewModel(repository) as T
        }
    }
}