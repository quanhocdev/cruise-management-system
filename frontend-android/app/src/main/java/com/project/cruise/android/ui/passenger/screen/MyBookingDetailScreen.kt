package com.project.cruise.android.ui.passenger.screen

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.passenger.components.mybookingdetail.*
import com.project.cruise.android.ui.screens.passenger.BookingTicketPanel
import com.project.cruise.android.viewmodel.passenger.*
@Composable
fun MyBookingDetailScreen(
    bookingId: Long,
    viewModel: BookingViewModel,
    onBack: () -> Unit,
    onSpending: (() -> Unit)? = null,
    onActivities: (() -> Unit)? = null,
    onBill: (() -> Unit)? = null,
    onFeedback: (() -> Unit)? = null,
    notificationButton: @Composable () -> Unit = {},
) {
    val state by viewModel.bookingDetailState.collectAsState()
    val access by viewModel.bookingAccessState.collectAsState()
    LaunchedEffect(bookingId) { viewModel.fetchBookingDetail(bookingId) }
    MyBookingDetailContent(
        state = state,
        onBack = onBack,
        onRetry = { viewModel.fetchBookingDetail(bookingId) },
        onSpending = onSpending,
        onActivities = onActivities,
        onBill = onBill,
        onFeedback = onFeedback,
        notificationButton = notificationButton,
        access = access,
    )
}

@Composable
fun MyBookingDetailContent(
    state: BookingDetailState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onSpending: (() -> Unit)? = null,
    onActivities: (() -> Unit)? = null,
    onBill: (() -> Unit)? = null,
    onFeedback: (() -> Unit)? = null,
    notificationButton: @Composable () -> Unit = {},
    access: BookingAccessState = BookingAccessState(),
) {
    PassengerPage("Thông tin đặt chỗ", onBack = onBack) {
        when (state) {
            BookingDetailState.Idle,
            BookingDetailState.Loading -> item { PassengerLoading() }
            is BookingDetailState.Error -> item { PassengerError(state.message, onRetry) }
            is BookingDetailState.Success -> {
                val booking = state.booking
                val passengers = booking.bookingPassengers.orEmpty()

                item { notificationButton() }
                item { BookingDetailHero(booking.bookingCode ?: "#${booking.id}", booking.status) }
                item { BookingSummaryCard(booking) }
                item { BookingTicketPanel(booking.status, access, onRetry) }
                item {
                    BookingActionButtons(
                        onActivities = onActivities.takeIf { booking.status == BookingStatus.CONFIRMED },
                        onSpending = onSpending,
                        onBill = onBill,
                        onFeedback = onFeedback,
                    )
                }

                item { PassengerSection("Người liên hệ") }
                item {
                    ContactInfoCard(
                        name = booking.primaryContactName,
                        phone = booking.primaryContactPhone,
                        email = booking.primaryContactEmail,
                    )
                }

                item {
                    PassengerSection("Hành khách", "Thông tin và trạng thái check-in từng người.")
                }
                if (passengers.isEmpty())
                    item {
                        PassengerEmpty(
                            "Chưa có thông tin hành khách",
                            "Danh sách sẽ hiển thị khi được hệ thống cập nhật.",
                        )
                    }
                items(passengers) { passenger -> PassengerInfoCard(passenger) }
            }
        }
    }
}