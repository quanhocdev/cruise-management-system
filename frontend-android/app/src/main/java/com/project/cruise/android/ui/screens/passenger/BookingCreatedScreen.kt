package com.project.cruise.android.ui.screens.passenger

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.passenger.PassengerBookingResponse
import java.text.NumberFormat
import java.util.Locale
import com.project.cruise.android.ui.theme.*

@Composable
fun BookingCreatedScreen(booking: PassengerBookingResponse, onDone: () -> Unit) {
    BackHandler(onBack = onDone)
    OceanPage {
        OceanBanner("GIỮ CHỖ THÀNH CÔNG", "Đã tạo booking", "Thông tin đặt chỗ đã được ghi nhận.")
        Text("Mã booking: ${booking.bookingCode ?: "#${booking.id}"}", style = MaterialTheme.typography.titleLarge)
        Text("Người liên hệ: ${booking.primaryContactName}")
        Text("Điện thoại: ${booking.primaryContactPhone}")
        Text("Tổng tiền: ${NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(booking.totalAmount)}")
        Text("Trạng thái: " + when (booking.status) {
            "PENDING_PAYMENT" -> "Chờ thanh toán"
            "CONFIRMED" -> "Đã xác nhận"
            "CANCELLED" -> "Đã hủy"
            else -> booking.status
        })
        OceanNotice("Vào Booking của tôi trên ứng dụng để xem chi tiết và thanh toán nếu booking đang chờ thanh toán.")
        OceanPrimaryButton("Về Dashboard", onDone)
    }
}
