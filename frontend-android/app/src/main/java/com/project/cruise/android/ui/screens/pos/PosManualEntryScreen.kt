package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.local.pos.PosScanType
import com.project.cruise.android.data.repository.PosTransactionQueue
import kotlinx.coroutines.launch

@Composable
fun PosManualEntryScreen(
    role: PosRole,
    onBackClick: () -> Unit,
    onSaved: (String) -> Unit
) {
    val context = LocalContext.current
    val queue = remember { PosTransactionQueue(context) }
    val scope = rememberCoroutineScope()
    var code by rememberSaveable { mutableStateOf("") }
    var saving by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    PosTheme {
    Column(
        Modifier
            .fillMaxSize().background(PosBackground)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBackClick, enabled = !saving) { Text("← Quay lại POS") }
        Text(
            when (role) {
                PosRole.ONBOARD -> "Nhập mã vé khách"
                PosRole.SHORE -> "Nhập mã khách tham quan"
                else -> "Nhập mã booking"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            when (role) {
                PosRole.ONBOARD -> "Nhập nguyên nội dung QR trên vé. Tính năng này dùng để kiểm thử trên máy ảo hoặc khi camera không đọc được mã."
                PosRole.SHORE -> "Nhập nguyên nội dung QR của khách. Dùng để kiểm thử máy ảo hoặc khi camera không đọc được mã."
                else -> "Nhập nguyên mã định danh in dưới QR hoặc trong email của hành khách."
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = code,
            onValueChange = {
                code = it
                error = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(if (role == PosRole.FINANCE) "Mã booking" else "Mã QR vé") },
            placeholder = { Text(if (role == PosRole.FINANCE) "Mã trong email xác nhận" else "Nội dung in dưới QR") },
            enabled = !saving,
            minLines = 2,
            shape = RoundedCornerShape(16.dp)
        )
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = {
                saving = true
                scope.launch {
                    runCatching {
                        queue.enqueue(
                            scanType = PosScanType.QR,
                            scannedValue = code.trim(),
                            operatorRole = role.apiRole,
                            operation = role.scanOperation
                        )
                    }
                        .onSuccess(onSaved)
                        .onFailure {
                            error = "Không thể lưu mã trên thiết bị"
                            saving = false
                        }
                }
            },
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            enabled = code.isNotBlank() && !saving,
            shape = RoundedCornerShape(14.dp)
        ) {
            if (saving) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text(if (role == PosRole.FINANCE) "Gửi mã booking" else "Kiểm tra mã vé")
        }
        Text(
            when (role) {
                PosRole.ONBOARD -> "Mã được lưu trên thiết bị. Chưa được coi là đã lên tàu cho đến khi backend xác nhận thành công."
                PosRole.SHORE -> "Mã được lưu trên thiết bị. Chưa xác nhận khách đã rời tàu hoặc quay lại."
                else -> "Mã được lưu trên thiết bị trước khi gửi. Hoàn tất thủ tục tại quầy lễ tân sau khi gửi mã."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

}
