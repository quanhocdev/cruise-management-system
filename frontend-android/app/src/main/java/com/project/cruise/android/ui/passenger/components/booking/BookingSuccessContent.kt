package com.project.cruise.android.ui.passenger.components.booking

import androidx.compose.runtime.Composable
import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.ui.components.*

@Composable
fun BookingSuccessContent(booking: BookingResponse, onDone: () -> Unit) {
    OceanPage {
        OceanHeader("Đặt chỗ thành công")
        OceanBanner(
            "ĐẶT CHỖ CỦA BẠN",
            booking.bookingCode ?: "Booking #${booking.id}",
            bookingLabel(booking.status),
        )
        OceanStatePanel(
            "Tạo booking thành công",
            "Tổng giá trị: ${passengerMoney(booking.totalAmount)}. Xem thông tin và trạng thái đặt chỗ trong Chuyến đi của tôi.",
            "✓",
            "Xem chuyến đi của tôi",
            onDone,
        )
    }
}