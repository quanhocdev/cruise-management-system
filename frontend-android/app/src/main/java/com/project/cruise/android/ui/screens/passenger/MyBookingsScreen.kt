package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.passenger.BookingHistoryState
import java.text.NumberFormat
import java.util.Locale

fun bookingStatus(value: String) = when (value) {
    "PENDING_PAYMENT" -> "Chờ thanh toán"
    "CONFIRMED" -> "Đã thanh toán"
    "CANCELLED" -> "Đã hủy"
    "COMPLETED" -> "Hoàn thành"
    else -> value
}

@Composable
fun MyBookingsScreen(state: BookingHistoryState, onRefresh: () -> Unit, onBookingClick: (Long) -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp)) {
        TextButton(onClick = onBack) { Text("← Trang chủ") }
        Text("Booking của tôi", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (!state.loading && state.bookings.isEmpty()) {
            Text("Bạn chưa có booking nào.", modifier = Modifier.padding(vertical = 24.dp))
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
            items(state.bookings, key = { it.id }) { booking ->
                Card(Modifier.fillMaxWidth().clickable { onBookingClick(booking.id) }) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(booking.bookingCode ?: "Booking #${booking.id}", style = MaterialTheme.typography.titleMedium)
                        Text("Trạng thái: ${bookingStatus(booking.status)}")
                        Text("Tổng tiền: ${NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(booking.totalAmount)}")
                        Text("${booking.passengers.size} hành khách")
                    }
                }
            }
        }
        OutlinedButton(onClick = onRefresh, enabled = !state.loading) { Text("Tải lại") }
    }
}
