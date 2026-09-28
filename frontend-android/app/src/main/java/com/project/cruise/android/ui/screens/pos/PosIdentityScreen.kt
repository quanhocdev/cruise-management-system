package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.pos.PosScanState

@Composable
fun PosIdentityScreen(role: PosRole, state: PosScanState, onRetry: () -> Unit, onBack: () -> Unit) {
    val success = state.response?.success == true
    PosTheme {
        Surface(color = PosBackground, modifier = Modifier.fillMaxSize()) {
            Column(Modifier.safeDrawingPadding().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)) {
                TextButton(onClick = onBack, enabled = !state.loading) { Text("← Quay lại POS") }
                PosBadge(role.title, role.accent())
                PosPanel {
                    if (state.loading) CircularProgressIndicator(color = role.accent())
                    else PosBadge(when { success -> "Đã gửi"; state.localReadOnly -> "Chỉ lưu trên thiết bị"; else -> "Chưa xác nhận" },
                        if (success) role.accent() else Color(0xFF995417))
                    Text(when {
                        state.loading -> "Đang gửi mã booking"
                        state.localReadOnly -> "Đã đọc mã vòng NFC"
                        success -> "Đã gửi mã booking"
                        else -> "Kiểm tra kết quả"
                    }, style = MaterialTheme.typography.headlineMedium, color = PosInk, fontWeight = FontWeight.Bold)
                    Text(when {
                        state.loading -> "Vui lòng chờ phản hồi trước khi thực hiện thao tác tiếp theo."
                        state.localReadOnly -> "Mã vòng đã được lưu. Chưa xác nhận danh tính, quyền tham gia hoặc việc sử dụng dịch vụ của hành khách."
                        success -> "Tiếp tục làm thủ tục tại quầy lễ tân. Kết quả này chưa xác nhận khách đã check-in."
                        else -> state.error ?: state.response?.message ?: "Chưa có kết quả xác nhận."
                    }, color = PosMuted)
                    if (success) state.response?.bookingId?.let {
                        HorizontalDivider()
                        Text("Tham chiếu booking • $it", color = PosInk)
                    }
                }
                if (!state.loading) {
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = role.accent())) { Text("Về trang tác vụ") }
                    if (!success && !state.localReadOnly && role == PosRole.FINANCE) {
                        Text("Nếu kết nối bị gián đoạn, hãy kiểm tra lịch sử và quầy lễ tân trước khi gửi lại để tránh lặp thao tác.",
                            color = PosMuted, style = MaterialTheme.typography.bodySmall)
                        OutlinedButton(onClick = onRetry, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Thử lại sau khi kiểm tra") }
                    }
                }
            }
        }
    }
}
