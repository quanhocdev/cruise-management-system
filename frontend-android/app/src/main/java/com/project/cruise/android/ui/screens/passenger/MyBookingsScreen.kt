//package com.project.cruise.android.ui.screens.passenger
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.project.cruise.android.viewmodel.passenger.BookingHistoryState
//import java.text.NumberFormat
//import java.util.Locale
//import com.project.cruise.android.ui.theme.*
//import androidx.compose.ui.graphics.Color
//
//fun bookingStatus(value: String) = when (value) {
//    "PENDING_PAYMENT" -> "Chờ thanh toán"
//    "CONFIRMED" -> "Đã thanh toán"
//    "CANCELLED" -> "Đã hủy"
//    "COMPLETED" -> "Hoàn thành"
//    else -> value
//}
//
//@Composable
//fun MyBookingsScreen(state: BookingHistoryState, onRefresh: () -> Unit, onBookingClick: (Long) -> Unit, onBack: () -> Unit) {
//    TourPage("Trang chủ", onBack) {
//        item { OceanBanner("CHUYẾN ĐI CỦA BẠN", "Booking của tôi", "Quản lý đặt chỗ, thanh toán và vé lên tàu.") }
//        if (state.loading) item {
//            LinearProgressIndicator(Modifier.fillMaxWidth())
//            OceanStatePanel("Đang tải booking", "Các chuyến đi của bạn đang được đồng bộ.", "≈")
//        }
//        state.error?.let { message -> item {
//            OceanStatePanel("Không tải được booking", message, "!", "Thử lại", onRefresh, true)
//        } }
//        if (!state.loading && state.error == null && state.bookings.isEmpty()) item {
//            OceanStatePanel("Bạn chưa có booking", "Booking mới sẽ xuất hiện tại đây sau khi giữ chỗ.", "▤")
//        }
//            items(state.bookings, key = { it.id }) { booking ->
//                Card(onClick = { onBookingClick(booking.id) }, modifier = Modifier.fillMaxWidth(),
//                    colors = CardDefaults.cardColors(containerColor = Color.White)) {
//                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                        Text(booking.bookingCode ?: "Booking #${booking.id}", style = MaterialTheme.typography.titleMedium)
//                        Text("Trạng thái: ${bookingStatus(booking.status)}")
//                        Text("Tổng tiền: ${NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(booking.totalAmount)}")
//                        Text("${booking.passengers.size} hành khách")
//                        Text("Xem chi tiết →", color = OceanTeal, style = MaterialTheme.typography.labelLarge)
//                    }
//                }
//            }
//        item { OutlinedButton(onClick = onRefresh, enabled = !state.loading,
//            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Tải lại") } }
//    }
//}
