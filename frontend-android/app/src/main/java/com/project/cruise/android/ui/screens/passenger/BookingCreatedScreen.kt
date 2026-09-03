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

@Composable
fun BookingCreatedScreen(booking: PassengerBookingResponse, onDone: () -> Unit) {
    BackHandler(onBack = onDone)
    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Đã tạo booking", style = MaterialTheme.typography.headlineMedium)
        Text("Mã booking: ${booking.bookingCode}", style = MaterialTheme.typography.titleLarge)
        Text("Người liên hệ: ${booking.primaryContactName}")
        Text("Điện thoại: ${booking.primaryContactPhone}")
        Text("Tổng tiền: ${NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(booking.totalAmount)}")
        Text("Trạng thái: " + when (booking.status) {
            "PENDING_PAYMENT" -> "Chờ thanh toán"
            "CONFIRMED" -> "Đã xác nhận"
            "CANCELLED" -> "Đã hủy"
            else -> booking.status
        })
        Text("Chưa thực hiện thanh toán trên Android. Bạn có thể xem booking này trong mục Booking của tôi trên web.")
        Button(onClick = onDone) { Text("Về Dashboard") }
    }
}
