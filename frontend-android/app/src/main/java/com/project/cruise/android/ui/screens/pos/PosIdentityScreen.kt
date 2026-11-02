package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.pos.PosScanState

@Composable
fun PosIdentityScreen(role: PosRole, state: PosScanState, onRetry: () -> Unit, onBack: () -> Unit) {
    val success = state.response?.success == true
    val color = if (success || state.localReadOnly) role.accent() else PosAmber
    PosPage {
        PosTopBar("Kết quả xử lý", role, onBack, !state.loading)
        Spacer(Modifier.height(12.dp))
        PosPanel {
            Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Box(Modifier.size(104.dp).background(color.copy(alpha=.08f), CircleShape).padding(16.dp).background(color.copy(alpha=.12f), CircleShape), contentAlignment = Alignment.Center) {
                    if(state.loading) CircularProgressIndicator(color = role.accent())
                    else PosGlyph(if(success) PosSymbol.CHECK else if(state.localReadOnly) PosSymbol.NFC else PosSymbol.INFO, color, Modifier.size(38.dp))
                }
                PosBadge(when { state.loading -> "Đang xử lý"; success -> "Máy chủ đã nhận mã"; state.localReadOnly -> "Đã lưu trên máy"; else -> "Cần kiểm tra" }, color)
                Text(when { state.loading -> "Đang gửi mã booking"; state.localReadOnly -> "Đã đọc vòng NFC"; success -> "Đã gửi mã booking"; else -> "Chưa xác nhận kết quả" },
                    style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Text(when {
                    state.loading -> "Vui lòng chờ phản hồi trước khi tiếp tục."
                    state.localReadOnly -> "Mã vòng đã được lưu trên thiết bị. Thông tin hành khách chưa được xác minh."
                    success -> "Tiếp tục làm thủ tục cho khách tại quầy lễ tân."
                    else -> state.error ?: state.response?.message ?: "Chưa có phản hồi xác nhận."
                }, color = PosMuted, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            }
            if(success) state.response?.bookingId?.let { HorizontalDivider(); PosDetailRow("Tham chiếu booking", it.toString()) }
        }
        if(!state.loading) {
            PosPrimaryButton("Về trang tác vụ", role, onBack)
            if(success) PosNotice("Gửi mã thành công chưa phải hoàn tất check-in.")
            if(!success && !state.localReadOnly && role == PosRole.FINANCE) {
                PosNotice("Kiểm tra lịch sử và quầy lễ tân trước khi gửi lại để tránh lặp thao tác.", warning = true)
                OutlinedButton(onClick = onRetry, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Thử lại sau khi kiểm tra") }
            }
        }
    }
}
