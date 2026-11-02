package com.project.cruise.android.ui.screens.passenger

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.booking.BookingStatus
import com.project.cruise.android.ui.components.PassengerCard
import com.project.cruise.android.viewmodel.passenger.BookingAccessState

@Composable
fun BookingTicketPanel(status: BookingStatus?, state: BookingAccessState, onRefresh: () -> Unit,
                       onOpenPayment: ((String) -> Unit)? = null) {
    if (status != BookingStatus.PENDING_PAYMENT && status != BookingStatus.CONFIRMED) return
    val uriHandler = LocalUriHandler.current
    var openError by remember { mutableStateOf<String?>(null) }
    PassengerCard {
        Text(if (status == BookingStatus.CONFIRMED) "QR booking" else "Thanh toán vé", style = MaterialTheme.typography.titleLarge)
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.message?.let { Text(it) }
        if (status == BookingStatus.CONFIRMED) {
            val bitmap = remember(state.qrBytes) { state.qrBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) } }
            bitmap?.let {
                Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Image(it.asImageBitmap(), "QR booking", Modifier.size(240.dp))
                    }
                }
            }
            Text("Đưa mã này cho nhân viên tra cứu booking và đối chiếu hành khách khi check-in.")
        } else {
            val url = state.payment?.payableUrl()
            if (url != null) Button(onClick = {
                val current = state.payment?.payableUrl()
                if (current == null) onRefresh() else runCatching {
                    if (onOpenPayment != null) onOpenPayment(current) else uriHandler.openUri(current)
                }.onFailure { openError = "Không mở được trình duyệt. Vui lòng thử lại." }
            }, modifier = Modifier.fillMaxWidth()) { Text("Thanh toán VNPay Sandbox") }
            else if (!state.loading && state.message == null) Text(when (state.payment?.status) {
                "SUCCESS" -> "Đã nhận thanh toán; đang chờ xác nhận booking."
                "EXPIRED", "FAILED" -> "Giao dịch đã hết hạn hoặc chưa thành công. Vui lòng liên hệ hỗ trợ."
                else -> "Chưa có link thanh toán còn hiệu lực. Vui lòng cập nhật trạng thái."
            })
            Text("Sau khi thanh toán, quay lại và cập nhật trạng thái. Chỉ xuất vé khi booking đã được xác nhận.")
        }
        openError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        OutlinedButton(onClick = { openError = null; onRefresh() }, enabled = !state.loading) { Text("Cập nhật vé & thanh toán") }
    }
}
