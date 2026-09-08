package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun PaymentReturnScreen(status: String?, paymentId: Long?, onBookings: () -> Unit) {
    val success = status == "SUCCESS"
    OceanPage {
        OceanBanner("VNPAY SANDBOX", "Kết quả thanh toán", "Kiểm tra booking để xem trạng thái mới nhất.")
        Text(if (success) "Thanh toán thành công" else "Thanh toán chưa thành công",
            style = MaterialTheme.typography.headlineMedium,
            color = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(12.dp))
        Text(if (success) "Booking đã được xác nhận." else "Bạn có thể quay lại booking và thử thanh toán lần nữa.")
        paymentId?.let { Text("Mã thanh toán: #$it", modifier = Modifier.padding(top = 8.dp)) }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onBookings, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text("Xem booking của tôi") }
    }
}
