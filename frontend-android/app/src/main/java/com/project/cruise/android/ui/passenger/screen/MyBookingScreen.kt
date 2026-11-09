package com.project.cruise.android.ui.passenger.screen

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.ui.common.OceanBottomBar
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.passenger.components.mybooking.*
import com.project.cruise.android.viewmodel.passenger.*

@Composable
fun MyBookingsScreen(
    viewModel: BookingViewModel,
    onBookingClick: (Long) -> Unit,
    onBack: () -> Unit,
    onBrowseTours: () -> Unit = onBack,
    onAccount: (() -> Unit)? = null,
    onMessagesClick: () -> Unit = {},
) {
    val state by viewModel.bookingListState.collectAsState()
    LaunchedEffect(Unit) { viewModel.fetchMyBookings() }
    MyBookingsContent(
        state,
        onBookingClick,
        onBack,
        { viewModel.fetchMyBookings() },
        onBrowseTours,
        bottomBar = {
            if (onAccount != null)
                OceanBottomBar(
                    true,
                    "passenger_bookings",
                    onBrowseTours,
                    {},
                    onAccount,
                    {},
                    onMessagesClick = onMessagesClick
                )
        },
    )
}

@Composable
fun MyBookingsContent(
    state: BookingListState,
    onBookingClick: (Long) -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onBrowseTours: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    val bookings = (state as? BookingListState.Success)?.bookings.orEmpty()
    val filtered =
        remember(bookings, query, selected) { bookings.filterBookings(query, selected) }
    val isLoaded = state is BookingListState.Success

    PassengerPage(
        "Chuyến đi của tôi",
        "Theo dõi đặt chỗ và thông tin hành khách",
        onBack,
        bottomBar,
    ) {
        item { BookingSummaryHeader(bookings = bookings, isLoaded = isLoaded) }
        item { BookingSearchField(query = query, onQueryChange = { query = it }) }
        item {
            BookingStatusFilters(
                selected = selected,
                onSelect = { selected = it },
                resultCount = if (isLoaded) filtered.size else null,
            )
        }
        when (state) {
            BookingListState.Idle,
            BookingListState.Loading -> item { PassengerLoading() }
            is BookingListState.Error -> item { PassengerError(state.message, onRetry) }
            is BookingListState.Success -> {
                if (filtered.isEmpty())
                    item {
                        BookingListEmpty(
                            hasBookings = bookings.isNotEmpty(),
                            canBrowseTours = onBrowseTours != null,
                            onBrowseTours = { (onBrowseTours ?: onBack)() },
                            onClearFilters = {
                                query = ""
                                selected = null
                            },
                        )
                    }
                items(filtered, key = { it.id }) { booking ->
                    BookingCard(booking) { onBookingClick(booking.id) }
                }
            }
        }
    }
}

private fun List<BookingResponse>.filterBookings(
    query: String,
    status: String?,
): List<BookingResponse> = filter {
    (status == null || it.status?.name == status) &&
            (query.isBlank() ||
                    listOf(it.bookingCode, it.primaryContactName).any { text ->
                        text?.contains(query.trim(), ignoreCase = true) == true
                    })
}