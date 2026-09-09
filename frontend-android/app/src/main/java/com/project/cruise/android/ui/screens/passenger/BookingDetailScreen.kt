package com.project.cruise.android.ui.screens.passenger

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.passenger.BookingHistoryState
import java.text.NumberFormat
import java.util.Locale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.project.cruise.android.ui.theme.*
import androidx.compose.ui.graphics.Color

@Composable
fun BookingDetailScreen(
    state: BookingHistoryState,
    onRetry: () -> Unit,
    onStartPayment: () -> Unit,
    onPaymentUrlOpened: () -> Unit,
    onPaymentReturn: () -> Unit,
    onBack: () -> Unit
) {
    var showItinerary by rememberSaveable { mutableStateOf(false) }
    var showActivities by rememberSaveable { mutableStateOf(false) }
    var showCatalog by rememberSaveable { mutableStateOf(false) }
    if (showCatalog) {
        BookingCatalogDialog(state, onRetry, onClose = { showCatalog = false })
    }
    if (showActivities) {
        BookingActivitiesDialog(state, onRetry, onClose = { showActivities = false })
    }
    if (showItinerary) {
        BookingItineraryDialog(state, onRetry, onClose = { showItinerary = false })
    }
    val uriHandler = LocalUriHandler.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(state.paymentUrl) {
        state.paymentUrl?.let { url ->
            uriHandler.openUri(url)
            onPaymentUrlOpened()
        }
    }
    DisposableEffect(lifecycleOwner, state.awaitingPaymentReturn) {
        var leftApp = false
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP && state.awaitingPaymentReturn) leftApp = true
            if (event == Lifecycle.Event.ON_RESUME && leftApp) onPaymentReturn()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    OceanPage {
        OceanHeader("Chi tiết booking", onBack, !state.loading)
        OceanBanner("HÀNH TRÌNH CỦA BẠN", "Chi tiết booking")
        if (state.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            OceanStatePanel("Đang đồng bộ booking", "Thông tin thanh toán và QR đang được kiểm tra.", "≈")
        }
        state.error?.let { OceanStatePanel("Không tải được booking", it, "!", "Thử lại", onRetry, true) }
        state.detail?.let { booking ->
            Text(booking.bookingCode ?: "Booking #${booking.id}", style = MaterialTheme.typography.titleLarge)
            OceanNotice("Trạng thái: ${bookingStatus(booking.status)}")
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Chuyến đi của tôi", style = MaterialTheme.typography.titleMedium)
                    Text(state.trip?.tourName ?: "Thông tin tour đang cập nhật")
                    Text("Du thuyền: ${state.trip?.cruiseName ?: "Đang cập nhật"}")
                    Text("Ngày đi: ${tripDate(state.trip?.startDate)}")
                    Text("Ngày về: ${tripDate(state.trip?.endDate)}")
                    OutlinedButton(onClick = { showItinerary = true }) { Text("Xem lịch trình") }
                    OutlinedButton(onClick = { showActivities = true }) { Text("Hoạt động") }
                    OutlinedButton(onClick = { showCatalog = true }) { Text("Dịch vụ & sản phẩm") }
                    state.tripError?.let { Text(it, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall) }
                }
            }
            Text("Liên hệ: ${booking.primaryContactName} — ${booking.primaryContactPhone}")
            Text("Tổng tiền: ${NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(booking.totalAmount)}",
                style = MaterialTheme.typography.titleLarge, color = OceanTeal)
            Text(if (booking.paymentId != null) "Mã thanh toán: ${booking.paymentId}" else "Chưa có giao dịch thanh toán thành công")

            if (booking.status == "PENDING_PAYMENT") {
                OceanPrimaryButton("Thanh toán VNPay Sandbox", onStartPayment, loading = state.creatingPayment)
                if (state.awaitingPaymentReturn) {
                    Text("Sau khi thanh toán, quay lại ứng dụng để hệ thống tự kiểm tra kết quả.",
                        style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(onClick = onPaymentReturn) { Text("Kiểm tra kết quả ngay") }
                }
            }

            if (booking.status == "CONFIRMED") {
                Text("QR booking", style = MaterialTheme.typography.titleMedium)
                val bitmap = remember(state.qrBytes) { state.qrBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) } }
                if (bitmap != null) Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.padding(16.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Image(bitmap.asImageBitmap(), "QR booking", Modifier.size(240.dp))
                    }
                }
                else if (!state.loading) OceanStatePanel("Chưa tải được QR", "Bấm thử lại để lấy mã booking từ máy chủ.", "▦", "Thử lại", onRetry)
                Text("Đưa QR này cho nhân viên để tra booking. Nhân viên vẫn phải chọn đúng hành khách khi check-in.", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("QR chỉ xuất hiện sau khi booking được thanh toán và xác nhận.", style = MaterialTheme.typography.bodySmall)
            }

            Text("Hành khách", style = MaterialTheme.typography.titleMedium)
            booking.passengers.forEach { passenger ->
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(passenger.fullName, style = MaterialTheme.typography.titleSmall)
                    val room = state.trip?.rooms?.firstOrNull { it.roomId == passenger.cabinId }
                    Text("Phòng: ${if (passenger.cabinId == null) "Chưa gán" else room?.roomCode ?: "Đang cập nhật"}")
                    room?.roomTypeName?.let { Text("Loại phòng: $it") }
                    room?.deckNumber?.let { Text("Tầng: $it") }
                    Text("Đặt chỗ: ${passenger.passengerStatus}")
                    Text("Lên tàu: ${passenger.embarkationStatus}")
                    passenger.checkedInAt?.let { Text("Check-in: $it") }
                } }
            }
        }
        OutlinedButton(onClick = onRetry, enabled = !state.loading, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Làm mới thông tin") }
    }
}
