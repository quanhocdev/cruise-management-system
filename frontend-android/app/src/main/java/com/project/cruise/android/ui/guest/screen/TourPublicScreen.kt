package com.project.cruise.android.ui.guest.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.tour.*
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.chatbox.screen.PublicTourChatFloatingWidget
import com.project.cruise.android.ui.guest.components.tourcatalog.*
import com.project.cruise.android.viewmodel.auth.*
import com.project.cruise.android.viewmodel.chat.ChatViewModel
import com.project.cruise.android.viewmodel.tour.*

@Composable
fun TourPublicScreen(
    viewModel: TourViewModel,
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    onTourClick: (String) -> Unit,
    onNavigateToTourList: (TourSearchCriteria) -> Unit,
    onLoginClick: () -> Unit,
    onUserClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    onVesselTrackingClick: () -> Unit = {},
    unreadCount: Long = 0L,
    onHomeClick: () -> Unit = {},
) {
    val state by viewModel.tourListState.collectAsState()
    val session by authViewModel.sessionState.collectAsState()
    LaunchedEffect(Unit) { viewModel.fetchPublicTours() }
    val loggedIn = session is SessionState.Authenticated

    var departure by rememberSaveable { mutableStateOf("") }
    var destination by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }

    val criteria = TourSearchCriteria(
        departure = departure,
        destination = destination,
        date = date,
    )

    val tours = (state as? TourListState.Success)?.tours.orEmpty()

    Box(Modifier.fillMaxSize()) {
        PassengerPage(
            title = "CruiseTour",
            subtitle = "HÀNH TRÌNH TRÊN BIỂN",
            bottomBar = {
                OceanBottomBar(
                    isLoggedIn = loggedIn,
                    currentRoute = "passenger_tours",
                    unreadCount = unreadCount,
                    onHomeClick = onHomeClick,
                    onLoginClick = onLoginClick,
                    onUserClick = onUserClick,
                    onMyBookingsClick = onMyBookingsClick,
                    onMessagesClick = onMessagesClick,
                    onNotificationClick = onNotificationClick,
                    onVesselTrackingClick = onVesselTrackingClick,
                )
            },
            headerAction = {
                if (loggedIn) {
                    PublicTourChatFloatingWidget(chatViewModel)
                }
            },
        ) {
            item {
                PassengerDiscoveryHero()
            }
            item {
                TourSearchBox(
                    criteria = criteria,
                    onCriteriaChange = { updated ->
                        departure = updated.departure
                        destination = updated.destination
                        date = updated.date
                    },
                    onSearchClick = { onNavigateToTourList(criteria) },
                    onBrowseAllClick = { onNavigateToTourList(TourSearchCriteria()) },
                    searchButtonLabel = "Tìm tour",
                )
            }

            val currentState = state
            when (currentState) {
                TourListState.Idle,
                TourListState.Loading -> item { PassengerLoading() }

                is TourListState.Error -> item {
                    PassengerError(currentState.message) { viewModel.fetchPublicTours() }
                }

                is TourListState.Success -> {
                    val featured = selectTop3UpcomingTours(currentState.tours)
                    if (featured.isEmpty()) {
                        item {
                            PassengerEmpty(
                                "Chưa có hành trình mới chưa bắt đầu",
                                "Bấm \"Xem các tour hiện tại\" để xem toàn bộ danh sách tour.",
                            )
                        }
                    } else {
                        item {
                            UpcomingFeaturedCarousel(
                                tours = featured,
                                onTourClick = onTourClick,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TourListScreen(
    state: TourListState,
    initialCriteria: TourSearchCriteria = TourSearchCriteria(),
    onTourClick: (String) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    var departure by rememberSaveable { mutableStateOf(initialCriteria.departure) }
    var destination by rememberSaveable { mutableStateOf(initialCriteria.destination) }
    var date by rememberSaveable { mutableStateOf(initialCriteria.date) }
    var selectedTrip by rememberSaveable { mutableStateOf(TripFilterOption.ALL) }
    var selectedBooking by rememberSaveable { mutableStateOf(BookingFilterOption.ALL) }

    val criteria = TourSearchCriteria(
        departure = departure,
        destination = destination,
        date = date,
    )

    val tours = (state as? TourListState.Success)?.tours.orEmpty()
    val filtered = remember(tours, criteria, selectedTrip, selectedBooking) {
        tours.filter { tour ->
            val matchSearch = matchesTourSearch(tour, criteria)
            val matchTrip = selectedTrip == TripFilterOption.ALL ||
                    tour.statusTrip == TourStatusTrip.valueOf(selectedTrip.apiValue!!)
            val matchBooking = selectedBooking == BookingFilterOption.ALL ||
                    tour.statusBooking == TourBookingStatus.valueOf(selectedBooking.apiValue!!)
            matchSearch && matchTrip && matchBooking
        }
    }

    val isLoading = state is TourListState.Idle || state is TourListState.Loading

    PassengerPage(
        title = "Danh sách tour",
        subtitle = "TÌM KIẾM & BỘ LỌC",
        onBack = onBack,
    ) {
        item {
            TourSearchBox(
                criteria = criteria,
                onCriteriaChange = { updated ->
                    departure = updated.departure
                    destination = updated.destination
                    date = updated.date
                },
                onSearchClick = {},
                searchButtonLabel = "Lọc",
            )
        }

        item {
            TourFilterChips(
                tours = tours,
                loading = isLoading,
                shownCount = filtered.size,
                selectedTrip = selectedTrip,
                selectedBooking = selectedBooking,
                onTripChange = { selectedTrip = it },
                onBookingChange = { selectedBooking = it },
                onReset = {
                    departure = ""
                    destination = ""
                    date = ""
                    selectedTrip = TripFilterOption.ALL
                    selectedBooking = BookingFilterOption.ALL
                },
            )
        }

        when (state) {
            TourListState.Idle,
            TourListState.Loading -> item { PassengerLoading() }

            is TourListState.Error -> item { PassengerError(state.message, onRetry) }

            is TourListState.Success -> {
                if (filtered.isEmpty()) {
                    item {
                        TourCatalogEmpty(
                            hasTours = tours.isNotEmpty(),
                            onRetry = onRetry,
                            onClearFilters = {
                                departure = ""
                                destination = ""
                                date = ""
                                selectedTrip = TripFilterOption.ALL
                                selectedBooking = BookingFilterOption.ALL
                            },
                        )
                    }
                } else {
                    items(filtered, key = { it.id }) { tour ->
                        TourSummaryCard(tour) { onTourClick(tour.id) }
                    }
                }
            }
        }
    }
}