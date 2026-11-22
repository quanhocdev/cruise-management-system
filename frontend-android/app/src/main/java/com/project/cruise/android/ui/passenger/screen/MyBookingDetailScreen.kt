package com.project.cruise.android.ui.passenger.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.passenger.components.mybookingdetail.*
import com.project.cruise.android.ui.screens.passenger.BookingTicketPanel
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.*

/** Số item đứng trước tab sticky: header(0), nút thông báo(1), hero(2), tab(3). */
private const val TAB_HEADER_INDEX = 3

@Composable
fun MyBookingDetailScreen(
    bookingId: Long,
    viewModel: BookingViewModel,
    tripViewModel: PassengerTripDetailViewModel,
    onBack: () -> Unit,
    onSpending: (() -> Unit)? = null,
    onActivities: (() -> Unit)? = null,
    onBill: (() -> Unit)? = null,
    onFeedback: (() -> Unit)? = null,
    notificationButton: @Composable () -> Unit = {},
    mapContent: TripMapContent = DefaultTripMap,
) {
    val state by viewModel.bookingDetailState.collectAsState()
    val access by viewModel.bookingAccessState.collectAsState()
    val trip by tripViewModel.uiState.collectAsState()

    val listState = rememberLazyListState()
    var tabIndex by rememberSaveable { mutableStateOf(0) }
    var selectedDeck by rememberSaveable { mutableStateOf<String?>(null) }
    val tab = BookingDetailTab.values()[tabIndex]

    val booking = (state as? BookingDetailState.Success)?.booking
    val tourId = booking?.tourId?.takeIf { it.isNotBlank() }
    // Chỉ booking đã thanh toán (CONFIRMED) và có tourId mới mở các tab giám sát.
    val unlocked = booking?.status == BookingStatus.CONFIRMED && tourId != null

    LaunchedEffect(bookingId) { viewModel.fetchBookingDetail(bookingId) }
    TripPollingLifecycle(tripViewModel)

    LaunchedEffect(unlocked, tab, tourId) {
        if (!unlocked) return@LaunchedEffect
        val sections = tab.sections
        tripViewModel.selectSection(sections.first(), tourId, bookingId)
        sections.drop(1).forEach { tripViewModel.loadSection(it, tourId, bookingId) }
        // Hero cần tên tour/tuyến/trạng thái chuyến nên luôn tải tổng quan hành trình (không bật polling).
        tripViewModel.loadSection(TripSectionKey.TRACKING, tourId, bookingId)
        if (listState.firstVisibleItemIndex > TAB_HEADER_INDEX) listState.scrollToItem(TAB_HEADER_INDEX)
    }

    LaunchedEffect(trip.decks) {
        val first = trip.decks.minByOrNull { it.deckNumber ?: Int.MAX_VALUE }?.id
        if (selectedDeck == null && first != null) {
            selectedDeck = first
            tripViewModel.loadDeckAreas(tourId, first)
        }
    }

    MyBookingDetailContent(
        state = state,
        access = access,
        trip = trip,
        unlocked = unlocked,
        tab = tab,
        onTabChange = { tabIndex = it.ordinal },
        selectedDeckId = selectedDeck,
        onSelectDeck = { id ->
            selectedDeck = id
            tripViewModel.loadDeckAreas(tourId, id)
        },
        listState = listState,
        onBack = onBack,
        onRetry = { viewModel.fetchBookingDetail(bookingId) },
        onReloadTab = { tab.sections.forEach { tripViewModel.reloadSection(it, tourId, bookingId) } },
        onSpending = onSpending,
        onActivities = onActivities,
        onBill = onBill,
        onFeedback = onFeedback,
        notificationButton = notificationButton,
        mapContent = mapContent,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyBookingDetailContent(
    state: BookingDetailState,
    access: BookingAccessState,
    trip: PassengerTripDetailUiState,
    unlocked: Boolean,
    tab: BookingDetailTab,
    onTabChange: (BookingDetailTab) -> Unit,
    selectedDeckId: String?,
    onSelectDeck: (String) -> Unit,
    listState: LazyListState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onReloadTab: () -> Unit,
    onSpending: (() -> Unit)? = null,
    onActivities: (() -> Unit)? = null,
    onBill: (() -> Unit)? = null,
    onFeedback: (() -> Unit)? = null,
    notificationButton: @Composable () -> Unit = {},
    mapContent: TripMapContent = DefaultTripMap,
) {
    var selectedLeg by rememberSaveable { mutableStateOf<String?>(null) }
    var usageMode by rememberSaveable { mutableStateOf(0) }

    PassengerPage("Thông tin đặt chỗ", onBack = onBack, listState = listState) {
        when (state) {
            BookingDetailState.Idle,
            BookingDetailState.Loading -> item(key = "state-loading") { PassengerLoading() }
            is BookingDetailState.Error -> item(key = "state-error") { PassengerError(state.message, onRetry) }
            is BookingDetailState.Success -> {
                val booking = state.booking

                if (!unlocked) {
                    // Chưa thanh toán / đã hủy: giữ giao diện cũ (thanh toán, liên hệ, hành khách) và không gọi API giám sát.
                    val passengers = booking.bookingPassengers.orEmpty()
                    item(key = "l-notif") { notificationButton() }
                    item(key = "l-hero") { BookingDetailHero(booking.bookingCode ?: "#${booking.id}", booking.status) }
                    item(key = "l-summary") { BookingSummaryCard(booking) }
                    item(key = "l-ticket") { BookingTicketPanel(booking.status, access, onRetry) }
                    item(key = "l-contact-title") { PassengerSection("Người liên hệ") }
                    item(key = "l-contact") {
                        ContactInfoCard(
                            name = booking.primaryContactName,
                            phone = booking.primaryContactPhone,
                            email = booking.primaryContactEmail,
                        )
                    }
                    item(key = "l-pax-title") { PassengerSection("Hành khách", "Thông tin và trạng thái check-in từng người.") }
                    if (passengers.isEmpty()) {
                        item(key = "l-pax-empty") {
                            PassengerEmpty(
                                "Chưa có thông tin hành khách",
                                "Danh sách sẽ hiển thị khi được hệ thống cập nhật.",
                            )
                        }
                    }
                    itemsIndexed(passengers, key = { index, p -> "l-pax-${p.id ?: index}" }) { _, p ->
                        PassengerInfoCard(p)
                    }
                    return@PassengerPage
                }

                val overview = trip.routeOverview
                val route = listOfNotNull(overview?.resolvedDepartureName, overview?.resolvedDestinationName)
                    .takeIf { it.isNotEmpty() }?.joinToString(" → ")
                val dates = listOfNotNull(overview?.startDate, overview?.endDate)
                    .filter { it.isNotBlank() }
                    .joinToString(" – ") { passengerDate(it) }

                item(key = "notif") { notificationButton() }
                item(key = "hero") {
                    BookingHeroCompact(
                        bookingCode = trip.booking?.bookingCode ?: booking.bookingCode ?: "#${booking.id}",
                        status = booking.status,
                        tourName = overview?.tourName,
                        route = route,
                        dates = dates,
                        tripStatus = overview?.statusTrip,
                        cruiseName = trip.cruise?.name,
                        live = trip.vesselLocation?.latitude != null,
                    )
                }
                stickyHeader(key = "booking-tabs") {
                    Box(Modifier.fillMaxWidth().background(OceanSand)) {
                        BookingTabRow(selected = tab, onSelect = onTabChange)
                    }
                }

                when (tab) {
                    BookingDetailTab.BOOKING -> bookingTab(
                        booking = booking,
                        access = access,
                        trip = trip,
                        onRefreshBooking = onRetry,
                        onReloadTab = onReloadTab,
                        onActivities = onActivities,
                        onSpending = onSpending,
                        onBill = onBill,
                        onFeedback = onFeedback,
                    )
                    BookingDetailTab.JOURNEY -> journeyTab(
                        state = trip,
                        selectedLegKey = selectedLeg,
                        onSelectLeg = { selectedLeg = it },
                        onRetry = onReloadTab,
                        mapContent = mapContent,
                    )
                    BookingDetailTab.CRUISE -> cruiseTab(trip, selectedDeckId, onSelectDeck, onReloadTab)
                    BookingDetailTab.SERVICES -> servicesTab(trip, onReloadTab)
                    BookingDetailTab.USAGE -> benefitsUsageTab(trip, usageMode, { usageMode = it }, onReloadTab)
                    BookingDetailTab.BILLS -> billsTab(trip, onReloadTab)
                }
            }
        }
    }
}

/** Dừng polling GPS khi màn hình không còn hiển thị (sang màn khác hoặc app vào nền). */
@Composable
private fun TripPollingLifecycle(viewModel: PassengerTripDetailViewModel) {
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.setPollingPaused(false)
                Lifecycle.Event.ON_STOP -> viewModel.setPollingPaused(true)
                else -> Unit
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose {
            owner.lifecycle.removeObserver(observer)
            viewModel.setPollingPaused(true)
        }
    }
}