//package com.project.cruise.android.ui.screens.passenger
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.project.cruise.android.ui.theme.*
//
//@Composable
//fun PaymentReturnScreen(status: String?, paymentId: Long?, onBookings: () -> Unit) {
//    val success = status == "SUCCESS"
//    OceanPage {
//        OceanBanner("VNPAY SANDBOX", "Kết quả thanh toán", "Kiểm tra booking để xem trạng thái mới nhất.")
//        OceanStatePanel(
//            title = if (success) "Thanh toán thành công" else "Thanh toán chưa thành công",
//            message = if (success) "Booking đã được xác nhận. Mở booking để xem QR."
//                else "Bạn có thể quay lại booking và thử thanh toán lần nữa.",
//            symbol = if (success) "✓" else "!",
//            error = !success
//        )
//        paymentId?.let { OceanNotice("Mã thanh toán: #$it") }
//        OceanPrimaryButton("Xem booking của tôi", onBookings)
//    }
//}
