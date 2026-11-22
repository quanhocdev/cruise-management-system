package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.passenger.tripdetail.*
import com.project.cruise.android.data.repository.passenger.PassengerTripDetailRepository
import kotlinx.coroutines.CancellationException
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
    /** Các tầng đang tải khu vực (mỗi tầng một spinner riêng). */
    val loadingDeckIds: Set<String> = emptySet(),
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
    private val sectionJobs = mutableMapOf<TripSectionKey, Job>()
    private val deckJobs = mutableMapOf<String, Job>()
    private var vesselPollingJob: Job? = null
    private var currentTourId: String? = null
    private var currentBookingId: Long? = null
    private var pollingPaused = false

    /** Đổi booking/tour: hủy mọi request đang chạy để dữ liệu cũ không ghi đè state mới. */
    fun bindContext(tourId: String?, bookingId: Long?) {
        val contextChanged = currentTourId != tourId || currentBookingId != bookingId
        currentTourId = tourId
        currentBookingId = bookingId

        if (contextChanged) {
            sectionJobs.values.forEach { it.cancel() }
            sectionJobs.clear()
            deckJobs.values.forEach { it.cancel() }
            deckJobs.clear()
            loadedSections.clear()
            _uiState.value = PassengerTripDetailUiState(activeSection = _uiState.value.activeSection)
            updateVesselPollingState()
        }
    }

    /** UI gọi khi màn hình không còn hiển thị (sang màn khác, app vào nền) để dừng polling GPS. */
    fun setPollingPaused(paused: Boolean) {
        if (pollingPaused == paused) return
        pollingPaused = paused
        updateVesselPollingState()
    }

    fun selectSection(section: TripSectionKey, tourId: String?, bookingId: Long?) {
        bindContext(tourId, bookingId)
        _uiState.update { it.copy(activeSection = section) }
        updateVesselPollingState()
        loadSection(section, tourId = tourId, bookingId = bookingId, force = false)
    }

    fun loadSection(
        section: TripSectionKey,
        tourId: String?,
        bookingId: Long?,
        force: Boolean = false
    ) {
        bindContext(tourId, bookingId)
        if (!force && (loadedSections.contains(section) || sectionJobs[section]?.isActive == true)) return

        when (section) {
            TripSectionKey.TRACKING -> loadTracking(tourId)
            TripSectionKey.ITINERARY -> loadItinerary(tourId)
            TripSectionKey.CRUISE -> loadCruise(tourId)
            TripSectionKey.FACILITIES -> loadFacilitiesAndPackages(tourId)
            TripSectionKey.BOOKING -> loadBooking(bookingId)
            TripSectionKey.PASSENGERS -> loadPassengers(bookingId)
            TripSectionKey.BENEFITS -> {
                loadSection(TripSectionKey.BOOKING, tourId, bookingId, force)
                loadSection(TripSectionKey.FACILITIES, tourId, bookingId, force)
            }
            TripSectionKey.USAGES -> loadUsages(bookingId)
            TripSectionKey.BILLS -> loadBills(bookingId)
        }
    }

    fun reloadSection(section: TripSectionKey, tourId: String?, bookingId: Long?) {
        val related = when (section) {
            TripSectionKey.BENEFITS -> setOf(section, TripSectionKey.BOOKING, TripSectionKey.FACILITIES)
            else -> setOf(section)
        }
        loadedSections.removeAll(related)
        loadSection(section, tourId = tourId, bookingId = bookingId, force = true)
    }

    fun loadDeckAreas(tourId: String?, deckId: String, force: Boolean = false) {
        if (tourId.isNullOrBlank() || deckId.isBlank()) return
        if (!force && (_uiState.value.deckAreas.containsKey(deckId) || deckJobs[deckId]?.isActive == true)) return
        deckJobs[deckId]?.cancel()
        deckJobs[deckId] = viewModelScope.launch {
            _uiState.update { it.copy(loadingDeckIds = it.loadingDeckIds + deckId, areasError = null) }
            try {
                val areas = repository.getCruiseDeckAreas(tourId, deckId)
                _uiState.update { state ->
                    state.copy(
                        deckAreas = state.deckAreas + (deckId to areas),
                        loadingDeckIds = state.loadingDeckIds - deckId
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loadingDeckIds = it.loadingDeckIds - deckId,
                        areasError = e.message ?: "Không thể tải khu vực của tầng tàu."
                    )
                }
            }
        }
    }

    // ---------------------------------------------------------------- GPS polling

    private fun updateVesselPollingState() {
        val tourId = currentTourId
        val shouldPoll = !pollingPaused &&
                _uiState.value.activeSection == TripSectionKey.TRACKING &&
                !tourId.isNullOrBlank()

        if (!shouldPoll) {
            vesselPollingJob?.cancel()
            vesselPollingJob = null
            return
        }
        if (vesselPollingJob?.isActive == true) return

        vesselPollingJob = viewModelScope.launch {
            while (true) {
                fetchVesselOnce(tourId!!)
                delay(GPS_POLLING_INTERVAL_MS)
            }
        }
    }

    private suspend fun fetchVesselOnce(tourId: String) {
        _uiState.update { it.copy(vesselLoading = it.vesselLocation == null, vesselError = null) }
        try {
            val location = repository.getVesselLocation(tourId)
            _uiState.update {
                it.copy(vesselLocation = location, vesselLoading = false, vesselError = null)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    vesselLoading = false,
                    vesselError = e.message ?: "Không thể cập nhật vị trí tàu."
                )
            }
        }
    }

    // ---------------------------------------------------------------- loaders

    private fun launchSection(section: TripSectionKey, block: suspend () -> Unit) {
        sectionJobs[section]?.cancel()
        sectionJobs[section] = viewModelScope.launch { block() }
    }

    /** Như runCatching nhưng không nuốt CancellationException. */
    private suspend fun <T> attempt(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    private fun loadTracking(tourId: String?) {
        if (tourId.isNullOrBlank()) return
        launchSection(TripSectionKey.TRACKING) {
            _uiState.update { it.copy(routeLoading = true, routeError = null) }
            val overviewResult = attempt { repository.getRouteOverview(tourId) }
            val legsResult = attempt { repository.getRouteLegs(tourId) }
            val overview = overviewResult.getOrNull()
            val legs = legsResult.getOrDefault(emptyList())

            if (overview == null && legs.isEmpty()) {
                val message = overviewResult.exceptionOrNull()?.message
                    ?: legsResult.exceptionOrNull()?.message
                    ?: "Không thể tải dữ liệu hành trình."
                _uiState.update { it.copy(routeLoading = false, routeError = message) }
            } else {
                val lat = overview?.departurePort?.latitude ?: overview?.currentLatitude
                val lon = overview?.departurePort?.longitude ?: overview?.currentLongitude
                val weather = if (lat != null && lon != null) {
                    attempt { repository.getWeather(lat, lon, overview?.startDate, overview?.endDate) }.getOrNull()
                } else null

                loadedSections.add(TripSectionKey.TRACKING)
                _uiState.update {
                    it.copy(
                        routeOverview = overview,
                        routeLegs = legs,
                        weather = weather,
                        routeLoading = false,
                        routeError = null
                    )
                }
            }
        }
    }

    private fun loadItinerary(tourId: String?) {
        if (tourId.isNullOrBlank()) return
        launchSection(TripSectionKey.ITINERARY) {
            _uiState.update { it.copy(itineraryLoading = true, itineraryError = null) }
            try {
                val schedules = repository.getItinerary(tourId)
                loadedSections.add(TripSectionKey.ITINERARY)
                _uiState.update { it.copy(schedules = schedules, itineraryLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(itineraryLoading = false, itineraryError = e.message ?: "Không thể tải lịch trình.")
                }
            }
        }
    }

    private fun loadCruise(tourId: String?) {
        if (tourId.isNullOrBlank()) return
        launchSection(TripSectionKey.CRUISE) {
            _uiState.update { it.copy(cruiseLoading = true, cruiseError = null) }
            val cruiseResult = attempt { repository.getCruise(tourId) }
            val decksResult = attempt { repository.getCruiseDecks(tourId) }
            val cruise = cruiseResult.getOrNull()
            val decks = decksResult.getOrDefault(emptyList())

            if (cruise == null && decks.isEmpty()) {
                val message = cruiseResult.exceptionOrNull()?.message
                    ?: decksResult.exceptionOrNull()?.message
                    ?: "Không thể tải thông tin du thuyền."
                _uiState.update { it.copy(cruiseLoading = false, cruiseError = message) }
            } else {
                loadedSections.add(TripSectionKey.CRUISE)
                _uiState.update { it.copy(cruise = cruise, decks = decks, cruiseLoading = false, cruiseError = null) }
            }
        }
    }

    private fun loadFacilitiesAndPackages(tourId: String?) {
        if (tourId.isNullOrBlank()) return
        launchSection(TripSectionKey.FACILITIES) {
            _uiState.update { it.copy(facilitiesLoading = true, facilitiesError = null) }
            val productsR = attempt { repository.getProducts(tourId) }
            val servicesR = attempt { repository.getServices(tourId) }
            val activitiesR = attempt { repository.getCruiseActivities(tourId) }
            val visitsR = attempt { repository.getVisits(tourId) }
            val packagesR = attempt { repository.getPackages(tourId) }

            val results = listOf(productsR, servicesR, activitiesR, visitsR, packagesR)
            if (results.all { it.isFailure }) {
                val message = results.firstNotNullOfOrNull { it.exceptionOrNull()?.message }
                    ?: "Không thể tải tiện ích và gói tour."
                _uiState.update { it.copy(facilitiesLoading = false, facilitiesError = message) }
            } else {
                val products = productsR.getOrDefault(emptyList())
                val services = servicesR.getOrDefault(emptyList())
                val activities = activitiesR.getOrDefault(emptyList())
                val visits = visitsR.getOrDefault(emptyList())
                val packages = packagesR.getOrDefault(emptyList())

                loadedSections.add(TripSectionKey.FACILITIES)
                _uiState.update { state ->
                    val (matchedPkg, resolved) = resolveBookingPackageBenefits(
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
                        resolvedBenefits = resolved,
                        facilitiesLoading = false,
                        facilitiesError = null
                    )
                }
            }
        }
    }

    private fun loadBooking(bookingId: Long?) {
        if (bookingId == null || bookingId <= 0L) return
        launchSection(TripSectionKey.BOOKING) {
            _uiState.update { it.copy(bookingLoading = true, bookingError = null) }
            try {
                val detail = repository.getBookingTripDetail(bookingId)
                loadedSections.add(TripSectionKey.BOOKING)
                _uiState.update { state ->
                    val (matchedPkg, resolved) = resolveBookingPackageBenefits(
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
                        resolvedBenefits = resolved,
                        bookingLoading = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(bookingLoading = false, bookingError = e.message ?: "Không thể tải thông tin đơn đặt chỗ.")
                }
            }
        }
    }

    private fun loadPassengers(bookingId: Long?) {
        if (bookingId == null || bookingId <= 0L) return
        launchSection(TripSectionKey.PASSENGERS) {
            _uiState.update { it.copy(passengersLoading = true, passengersError = null) }
            try {
                val list = repository.getBookingPassengers(bookingId)
                loadedSections.add(TripSectionKey.PASSENGERS)
                _uiState.update { it.copy(passengers = list, passengersLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(passengersLoading = false, passengersError = e.message ?: "Không thể tải danh sách hành khách.")
                }
            }
        }
    }

    private fun loadUsages(bookingId: Long?) {
        if (bookingId == null || bookingId <= 0L) return
        launchSection(TripSectionKey.USAGES) {
            _uiState.update { it.copy(usagesLoading = true, usagesError = null) }
            val prodR = attempt { repository.getProductUsages(bookingId) }
            val servR = attempt { repository.getServiceUsages(bookingId) }
            val actR = attempt { repository.getCruiseActivityUsages(bookingId) }
            val visR = attempt { repository.getVisitUsages(bookingId) }

            val results = listOf(prodR, servR, actR, visR)
            if (results.all { it.isFailure }) {
                val message = results.firstNotNullOfOrNull { it.exceptionOrNull()?.message }
                    ?: "Không thể tải lượt sử dụng."
                _uiState.update { it.copy(usagesLoading = false, usagesError = message) }
            } else {
                loadedSections.add(TripSectionKey.USAGES)
                _uiState.update {
                    it.copy(
                        productUsages = prodR.getOrDefault(emptyList()),
                        serviceUsages = servR.getOrDefault(emptyList()),
                        activityCruiseUsages = actR.getOrDefault(emptyList()),
                        visitUsages = visR.getOrDefault(emptyList()),
                        usagesLoading = false,
                        usagesError = null
                    )
                }
            }
        }
    }

    private fun loadBills(bookingId: Long?) {
        if (bookingId == null || bookingId <= 0L) return
        launchSection(TripSectionKey.BILLS) {
            _uiState.update { it.copy(billsLoading = true, billsError = null) }
            try {
                val billList = repository.getBills(bookingId)
                loadedSections.add(TripSectionKey.BILLS)
                _uiState.update { it.copy(bills = billList, billsLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(billsLoading = false, billsError = e.message ?: "Không thể tải danh sách hóa đơn.")
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