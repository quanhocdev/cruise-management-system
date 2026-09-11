package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.pos.PosIdentityState

@Composable
fun PosIdentityScreen(state: PosIdentityState, onRetry: () -> Unit, onCheckIn: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextButton(onClick = onBack) { Text("← Quay lại POS") }
        Text("Nhận diện hành khách", style = MaterialTheme.typography.headlineMedium)
        if (state.loading) {
            CircularProgressIndicator()
            Text("Đang kiểm tra với máy chủ…")
        }
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        state.result?.let { result ->
            if (result.status == "IDENTIFIED") {
                Text("Đã xác minh danh tính", color = MaterialTheme.colorScheme.primary)
                Text(result.fullName.orEmpty(), style = MaterialTheme.typography.titleLarge)
                Text("Booking: ${result.bookingCode}")
                Text("Chuyến: ${result.voyageId}")
                Text("Mã phòng: ${result.cabinId ?: "Chưa gán"}")
                Text("Trạng thái lên tàu: " + when (result.embarkationStatus) {
                    "NOT_CHECKED_IN" -> "Chưa check-in"
                    "CHECKED_IN" -> "Đã check-in"
                    "BOARDED" -> "Đã lên tàu"
                    else -> "Chưa xác định"
                })
                val checkIn = state.checkInResult
                if (checkIn == null && result.embarkationStatus == "NOT_CHECKED_IN") {
                    Button(onClick = onCheckIn, enabled = !state.checkingIn) {
                        if (state.checkingIn) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("Xác nhận check-in")
                    }
                }
                checkIn?.let {
                    val successful = it.status == "CHECKED_IN" || it.status == "ALREADY_CHECKED_IN"
                    Text(when (it.status) {
                        "CHECKED_IN" -> "Check-in thành công"
                        "ALREADY_CHECKED_IN" -> "Hành khách đã check-in trước đó"
                        "ALREADY_BOARDED" -> "Hành khách đã lên tàu"
                        else -> "Không thể check-in: ${it.reason ?: "dữ liệu không hợp lệ"}"
                    }, color = if (successful) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium)
                    it.checkedInAt?.let { time -> Text("Thời gian máy chủ: $time") }
                    it.terminalCode?.let { terminal -> Text("Thiết bị: $terminal") }
                }
            } else {
                Text("Không xác minh được", color = MaterialTheme.colorScheme.error)
                Text(when (result.reason) {
                    "TERMINAL_NOT_ASSIGNED" -> "Admin chưa gán máy POS vào chuyến."
                    "INVALID_CODE" -> "Mã không đúng định dạng. Hãy dùng QR định danh hoặc thẻ NFC do Admin cấp."
                    "UNKNOWN_CREDENTIAL" -> "Thẻ/mã chưa được đăng ký."
                    "CREDENTIAL_REVOKED" -> "Thẻ/mã đã bị khóa."
                    "WRONG_VOYAGE" -> "Hành khách không thuộc chuyến của máy POS này."
                    "BOOKING_NOT_CONFIRMED" -> "Booking chưa xác nhận hoặc hành khách đã bị hủy."
                    else -> "Dữ liệu booking không hợp lệ. Liên hệ Admin."
                })
            }
        }
        Text("Quét mã chỉ nhận diện. Check-in chỉ được thực hiện sau khi nhân viên bấm xác nhận; thao tác này không thanh toán và không cấp quyền mua dịch vụ.",
            style = MaterialTheme.typography.bodySmall)
        OutlinedButton(onClick = onRetry, enabled = !state.loading) { Text("Xác minh lại") }
    }
}
