package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.local.pos.PosSyncStatus
import com.project.cruise.android.data.repository.PosTransactionQueue
import java.text.DateFormat
import java.util.Date

@Composable
fun PosHistoryScreen(role: PosRole, onBackClick: () -> Unit, onIdentify: (String) -> Unit) {
    val context = LocalContext.current
    val queue = remember { PosTransactionQueue(context) }
    val transactions by queue.observeAll(role.apiRole).collectAsState(initial = emptyList())
    val visibleTransactions = if (role == PosRole.FINANCE) transactions
        else transactions.filter { it.scanType == "NFC" }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        TextButton(onClick = onBackClick) { Text("← Quay lại POS") }
        Text(
            "Lịch sử giao dịch",
            modifier = Modifier.padding(top = 18.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "${visibleTransactions.size} giao dịch ${if (role == PosRole.FINANCE) "QR/NFC" else "NFC"} lưu trên thiết bị",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 5.dp, bottom = 20.dp)
        )

        if (visibleTransactions.isEmpty()) {
            Text(
                if (role == PosRole.FINANCE) "Chưa có giao dịch QR hoặc NFC."
                else "Chưa có giao dịch NFC.",
                modifier = Modifier.padding(top = 30.dp)
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(visibleTransactions, key = { it.localId }) { transaction ->
                    val status = runCatching { PosSyncStatus.valueOf(transaction.status) }
                        .getOrDefault(PosSyncStatus.PENDING_SYNC)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(transaction.scanType, fontWeight = FontWeight.Bold, color = Color(0xFF126A70))
                                Text(statusLabel(status), color = statusColor(status), style = MaterialTheme.typography.labelMedium)
                            }
                            Text(if (transaction.scannedValue.startsWith("POS:")) "QR định danh (đã ẩn mã)" else transaction.scannedValue,
                                modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.SemiBold)
                            Text(
                                operationLabel(transaction.operation),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                            TextButton(onClick = { onIdentify(transaction.localId) }) { Text("Xác minh hành khách") }
                            Text(
                                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(transaction.createdAt)),
                                modifier = Modifier.padding(top = 5.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            transaction.lastError?.let {
                                Text(it, modifier = Modifier.padding(top = 7.dp), color = Color(0xFF9A650E), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun operationLabel(operation: String) = when (operation) {
    "CHECK_IN" -> "Nghiệp vụ: check-in"
    "CHECK_OUT" -> "Nghiệp vụ: checkout"
    "CONVENIENCE_USAGE" -> "Nghiệp vụ: sử dụng tiện ích"
    "ONBOARD_PARTICIPATION" -> "Nghiệp vụ: hoạt động trên tàu"
    "SHORE_PARTICIPATION" -> "Nghiệp vụ: hoạt động bờ"
    else -> "Nghiệp vụ: nhận diện"
}

private fun statusLabel(status: PosSyncStatus) = when (status) {
    PosSyncStatus.PENDING_SYNC -> "Chờ đồng bộ"
    PosSyncStatus.SYNCING -> "Đang đồng bộ"
    PosSyncStatus.SYNCED -> "Đã đồng bộ"
    PosSyncStatus.FAILED -> "Đồng bộ lỗi"
    PosSyncStatus.CANCELLED -> "Đã hủy"
}

private fun statusColor(status: PosSyncStatus) = when (status) {
    PosSyncStatus.SYNCED -> Color(0xFF176B5B)
    PosSyncStatus.FAILED, PosSyncStatus.CANCELLED -> Color(0xFFA63E32)
    else -> Color(0xFF9A650E)
}
