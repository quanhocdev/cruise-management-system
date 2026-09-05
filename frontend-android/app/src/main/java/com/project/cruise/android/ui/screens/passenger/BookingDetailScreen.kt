package com.project.cruise.android.ui.screens.passenger

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.passenger.BookingHistoryState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BookingDetailScreen(state: BookingHistoryState, onRetry: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onBack) { Text("← Danh sách booking") }
        Text("Chi tiết booking", style = MaterialTheme.typography.headlineMedium)
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        state.detail?.let { booking ->
            Text(booking.bookingCode ?: "Booking #${booking.id}", style = MaterialTheme.typography.titleLarge)
            Text("Trạng thái: ${bookingStatus(booking.status)}")
            Text("Chuyến: ${booking.voyageId}")
            Text("Liên hệ: ${booking.primaryContactName} — ${booking.primaryContactPhone}")
            Text("Tổng tiền: ${NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(booking.totalAmount)}")
            Text(if (booking.paymentId != null) "Mã thanh toán: ${booking.paymentId}" else "Chưa có giao dịch thanh toán thành công")

            if (booking.status == "CONFIRMED") {
                Text("QR booking", style = MaterialTheme.typography.titleMedium)
                val bitmap = remember(state.qrBytes) { state.qrBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) } }
                if (bitmap != null) Image(bitmap.asImageBitmap(), "QR booking", Modifier.size(240.dp))
                else if (!state.loading) Text("Chưa tải được QR. Bấm thử lại.")
                Text("Đưa QR này cho nhân viên để tra booking. Nhân viên vẫn phải chọn đúng hành khách khi check-in.", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("QR chỉ xuất hiện sau khi booking được thanh toán và xác nhận.", style = MaterialTheme.typography.bodySmall)
            }

            Text("Hành khách", style = MaterialTheme.typography.titleMedium)
            booking.passengers.forEach { passenger ->
                Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(passenger.fullName, style = MaterialTheme.typography.titleSmall)
                    Text("Phòng: ${passenger.cabinId ?: "Chưa gán"}")
                    Text("Đặt chỗ: ${passenger.passengerStatus}")
                    Text("Lên tàu: ${passenger.embarkationStatus}")
                    passenger.checkedInAt?.let { Text("Check-in: $it") }
                } }
            }
        }
        OutlinedButton(onClick = onRetry, enabled = !state.loading) { Text("Thử lại") }
    }
}
