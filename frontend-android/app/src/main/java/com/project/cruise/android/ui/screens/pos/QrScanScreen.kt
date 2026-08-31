package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.local.pos.PosScanType
import com.project.cruise.android.data.repository.PosTransactionQueue
import kotlinx.coroutines.launch

@Composable
fun QrScanScreen(
    onBackClick: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val queue = remember { PosTransactionQueue(context) }
    val scope = rememberCoroutineScope()
    var qrValue by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.Start),
            enabled = !isSaving
        ) { Text("← Quay lại POS") }

        Spacer(Modifier.height(24.dp))
        Text("Quét mã QR", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Chế độ giả lập: nhập hoặc dán nội dung QR của vé/booking.",
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth()
                .height(190.dp)
                .background(Color(0xFF092F3D), RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("▦", color = Color.White, style = MaterialTheme.typography.displayLarge)
                Text("Đưa mã QR vào khung", color = Color(0xFFD1EBE8))
            }
        }

        OutlinedTextField(
            value = qrValue,
            onValueChange = { qrValue = it; error = null },
            label = { Text("Nội dung QR") },
            placeholder = { Text("Ví dụ: BOOKING:CR00000001") },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            enabled = !isSaving,
            singleLine = true
        )

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 10.dp))
        }

        Button(
            onClick = {
                val value = qrValue.trim()
                if (value.isBlank()) {
                    error = "Vui lòng nhập nội dung mã QR"
                    return@Button
                }
                isSaving = true
                scope.launch {
                    runCatching { queue.enqueue(PosScanType.QR, value) }
                        .onSuccess { onSaved() }
                        .onFailure {
                            error = "Không thể lưu giao dịch trên thiết bị"
                            isSaving = false
                        }
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.height(22.dp), strokeWidth = 2.dp)
            } else {
                Text("Giả lập quét và lưu")
            }
        }

        Spacer(Modifier.weight(1f))
        Text(
            "Dữ liệu được lưu bằng Room trước, sau đó WorkManager sẽ đồng bộ khi có mạng.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
