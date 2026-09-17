package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.pos.PosScanState

@Composable
fun PosIdentityScreen(
    role: PosRole,
    state: PosScanState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Quay lại POS") }

        Text("Gửi mã quét đến máy chủ", style = MaterialTheme.typography.headlineMedium)

        if (state.loading) {
            CircularProgressIndicator()
            Text("Đang truyền dữ liệu và kích hoạt màn hình lễ tân…")
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        state.response?.let { res ->
            if (res.success) {
                Text("Gửi mã thành công!", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
                Text("Thông điệp từ hệ thống: ${res.message}")
                if (res.bookingId != null) {
                    Text("Mã định danh Booking ID: ${res.bookingId}")
                }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Text(
                        "Mã QR đã được truyền tới hệ thống. Màn hình Web của lễ tân đã tự động hiển thị thông tin hành khách để gán phòng và check-in.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text("Máy chủ từ chối", color = MaterialTheme.colorScheme.error)
                Text("Lý do: ${res.message}")
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onRetry, enabled = !state.loading) {
            Text("Thử gửi lại")
        }
    }
}