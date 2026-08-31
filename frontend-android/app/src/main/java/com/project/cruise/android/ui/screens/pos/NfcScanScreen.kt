package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
fun NfcScanScreen(onBackClick: () -> Unit, onSaved: () -> Unit) {
    val context = LocalContext.current
    val queue = remember { PosTransactionQueue(context) }
    val scope = rememberCoroutineScope()
    var cardId by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(onClick = onBackClick, modifier = Modifier.align(Alignment.Start), enabled = !isSaving) {
            Text("← Quay lại POS")
        }
        Spacer(Modifier.height(24.dp))
        Text("Quét thẻ NFC", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Chế độ giả lập: nhập UID của thẻ hoặc vòng đeo tay.",
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier.padding(top = 36.dp).background(Color(0xFFDDF3F0), CircleShape).padding(44.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("NFC", color = Color(0xFF126A70), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        }
        Text("Chạm thẻ vào mặt sau thiết bị", modifier = Modifier.padding(top = 18.dp), fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = cardId,
            onValueChange = { cardId = it.uppercase(); error = null },
            label = { Text("UID thẻ NFC") },
            placeholder = { Text("Ví dụ: 04A1B2C3D4") },
            modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
            enabled = !isSaving,
            singleLine = true
        )
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 10.dp)) }
        Button(
            onClick = {
                val value = cardId.trim()
                if (value.isBlank()) {
                    error = "Vui lòng nhập UID thẻ NFC"
                    return@Button
                }
                isSaving = true
                scope.launch {
                    runCatching { queue.enqueue(PosScanType.NFC, value) }
                        .onSuccess { onSaved() }
                        .onFailure { error = "Không thể lưu giao dịch trên thiết bị"; isSaving = false }
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.height(22.dp), strokeWidth = 2.dp)
            else Text("Giả lập chạm thẻ và lưu")
        }
        Spacer(Modifier.weight(1f))
        Text(
            "UID được lưu cục bộ trước và không được xem là đã xác nhận cho tới khi backend phản hồi.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
