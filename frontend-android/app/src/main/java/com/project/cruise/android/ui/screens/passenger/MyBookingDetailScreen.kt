package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.*

@Composable
fun MyBookingDetailScreen(bookingId: Long, viewModel: BookingViewModel, onBack: () -> Unit) {
    val state by viewModel.bookingDetailState.collectAsState()
    LaunchedEffect(bookingId) { viewModel.fetchBookingDetail(bookingId) }
    MyBookingDetailContent(state, onBack, { viewModel.fetchBookingDetail(bookingId) })
}

@Composable
fun MyBookingDetailContent(state: BookingDetailState, onBack: () -> Unit, onRetry: () -> Unit) {
    PassengerPage("Thông tin đặt chỗ", onBack = onBack) {
        when (state) {
            BookingDetailState.Idle,
            BookingDetailState.Loading -> item { PassengerLoading() }
            is BookingDetailState.Error -> item { PassengerError(state.message, onRetry) }
            is BookingDetailState.Success -> {
                val booking = state.booking
                item {
                    OceanBanner(
                        "MÃ BOOKING",
                        booking.bookingCode ?: "#${booking.id}",
                        when (booking.status) {
                            BookingStatus.PENDING_PAYMENT ->
                                "Đặt chỗ đang chờ thanh toán và xác nhận."
                            BookingStatus.CONFIRMED -> "Đặt chỗ của bạn đã được xác nhận."
                            BookingStatus.CANCELLED -> "Đặt chỗ này đã được hủy."
                            null -> "Trạng thái đặt chỗ đang được cập nhật."
                        },
                    )
                }
                item {
                    PassengerCard {
                        PassengerPill(
                            bookingLabel(booking.status),
                            booking.status == BookingStatus.CONFIRMED,
                        )
                        Text(
                            passengerMoney(booking.totalAmount),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = OceanNavy,
                        )
                        Text(
                            "Tổng giá trị booking",
                            color = OceanSlate,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        HorizontalDivider(color = OceanLine)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(Modifier.weight(1f)) {
                                PassengerInfo("Ngày đặt", passengerDate(booking.createdAt))
                            }
                            Column(Modifier.weight(1f)) {
                                PassengerInfo(
                                    "Số hành khách",
                                    booking.numberPassengers?.let { "$it khách" }
                                        ?: booking.bookingPassengers?.size?.let { "$it khách" },
                                )
                            }
                        }
                    }
                }
                item { PassengerSection("Người liên hệ") }
                item {
                    PassengerCard {
                        PassengerInfo("Họ và tên", booking.primaryContactName)
                        PassengerInfo("Số điện thoại", booking.primaryContactPhone)
                        PassengerInfo("Email", booking.primaryContactEmail)
                    }
                }
                item {
                    PassengerSection("Hành khách", "Thông tin và trạng thái check-in từng người.")
                }
                if (booking.bookingPassengers.isNullOrEmpty())
                    item {
                        PassengerEmpty(
                            "Chưa có thông tin hành khách",
                            "Danh sách sẽ hiển thị khi được hệ thống cập nhật.",
                        )
                    }
                items(booking.bookingPassengers.orEmpty()) { passenger ->
                    PassengerInfoCard(passenger)
                }
            }
        }
    }
}

@Composable
fun PassengerInfoCard(passenger: BookingPassengerInfoResponse) {
    PassengerCard {
        Text(
            passenger.fullName ?: "Hành khách",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        val checkedIn = passenger.checkinStatus?.uppercase() in listOf("CHECKED_IN", "CHECKEDIN")
        PassengerPill(
            when (passenger.checkinStatus?.uppercase()) {
                "CHECKED_IN",
                "CHECKEDIN" -> "Đã check-in"
                "PENDING",
                "NOT_CHECKED_IN" -> "Chưa check-in"
                "CHECKED_OUT" -> "Đã check-out"
                null -> "Chưa có trạng thái check-in"
                else -> passenger.checkinStatus
            },
            checkedIn,
        )
        PassengerInfo(
            "Giới tính",
            when (passenger.gender?.uppercase()) {
                "MALE" -> "Nam"
                "FEMALE" -> "Nữ"
                "OTHER" -> "Khác"
                else -> passenger.gender
            },
        )
        passenger.phoneNumber
            ?.takeIf { it.isNotBlank() }
            ?.let { PassengerInfo("Số điện thoại", it) }
        passenger.email?.takeIf { it.isNotBlank() }?.let { PassengerInfo("Email", it) }
        passenger.checkedInAt?.let { PassengerInfo("Ngày check-in", passengerDate(it)) }
    }
}
