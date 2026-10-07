package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.local.pos.PosSyncStatus
import com.project.cruise.android.data.repository.PosTransactionQueue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PosHistoryScreen(role: PosRole, onBackClick: () -> Unit, onIdentify: (String) -> Unit) {
    val context = LocalContext.current
    val queue = remember(context) { PosTransactionQueue(context) }
    val transactions by queue.observeAll(role.apiRole).collectAsState(initial = emptyList())
    val visible = transactions.filter { role == PosRole.FINANCE || it.scanType == (if(role == PosRole.CONVENIENCE) "NFC" else "QR") }.sortedByDescending { it.createdAt }
    var onlyPending by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = visible.filter { (!onlyPending || it.status != PosSyncStatus.SYNCED.name) && it.scannedValue.contains(query, ignoreCase = true) }
    PosTheme {
        Box(Modifier.fillMaxSize().background(PosBackground).safeDrawingPadding().imePadding(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(Modifier.widthIn(max = 640.dp).fillMaxWidth(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item { PosTopBar("Lịch sử thao tác", role, onBackClick) }
                item {
                    PosPanel {
                        Text("${visible.size} bản ghi", style = MaterialTheme.typography.headlineMedium)
                        Text("Lịch sử lưu trên thiết bị này", style = MaterialTheme.typography.bodySmall, color = PosMuted)
                        OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Tìm mã đã quét") },
                            leadingIcon = { PosGlyph(PosSymbol.SEARCH, PosMuted) }, singleLine = true, shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(!onlyPending, { onlyPending = false }, label = { Text("Tất cả") })
                            FilterChip(onlyPending, { onlyPending = true }, label = { Text("Chưa xác nhận") })
                        }
                    }
                }
                if(filtered.isEmpty()) item { PosEmptyState(if(query.isNotBlank()) "Không tìm thấy mã" else "Chưa có thao tác", "Các lượt quét phù hợp sẽ xuất hiện tại đây.") }
                items(filtered, key = { it.localId }) { transaction ->
                    val status = runCatching { PosSyncStatus.valueOf(transaction.status) }.getOrDefault(PosSyncStatus.PENDING_SYNC)
                    PosPanel {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            PosGlyph(if(transaction.scanType == "NFC") PosSymbol.NFC else PosSymbol.QR, role.accent())
                            Text(transaction.scanType, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            PosBadge(statusLabel(status), statusColor(status))
                        }
                        Text(posDisplayCode(transaction.scannedValue), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(operationLabel(transaction.operation), color = PosMuted, style = MaterialTheme.typography.bodySmall)
                        HorizontalDivider()
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PosGlyph(PosSymbol.CLOCK, PosMuted, Modifier.size(16.dp))
                            Text(SimpleDateFormat("HH:mm · dd/MM/yyyy", Locale.getDefault()).format(Date(transaction.createdAt)), color = PosMuted, style = MaterialTheme.typography.bodySmall)
                        }
                        transaction.lastError?.let { PosNotice(it, warning = true) }
                        when {
                            role == PosRole.FINANCE && transaction.scanType == "QR" && status != PosSyncStatus.SYNCED && status != PosSyncStatus.SYNCING -> {
                                Text("Kiểm tra quầy lễ tân trước khi gửi lại.", color = PosMuted, style = MaterialTheme.typography.bodySmall)
                                TextButton(onClick = { onIdentify(transaction.localId) }) { Text("Kiểm tra / gửi lại mã") }
                            }
                            role == PosRole.ONBOARD || role == PosRole.SHORE || role == PosRole.CONVENIENCE -> {
                                TextButton(onClick = { onIdentify(transaction.localId) }) { Text("Xem chi tiết") }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun operationLabel(value: String) = when(value) {
    "CHECK_IN" -> "Hỗ trợ thủ tục tại quầy"; "CONVENIENCE_USAGE" -> "Kiểm tra tiện ích"
    "ONBOARD_PARTICIPATION" -> "Kiểm tra vé khách"; "SHORE_PARTICIPATION" -> "Kiểm tra tham quan"
    else -> "Nhận diện"
}
private fun statusLabel(value: PosSyncStatus) = when(value) {
    PosSyncStatus.PENDING_SYNC -> "Đã lưu trên máy"; PosSyncStatus.SYNCING -> "Đang gửi"
    PosSyncStatus.SYNCED -> "Đã gửi"; PosSyncStatus.FAILED -> "Cần kiểm tra"; PosSyncStatus.CANCELLED -> "Đã hủy"
}
private fun statusColor(value: PosSyncStatus) = when(value) {
    PosSyncStatus.SYNCED -> PosRole.FINANCE.accent()
    PosSyncStatus.FAILED, PosSyncStatus.CANCELLED -> androidx.compose.ui.graphics.Color(0xFFAF3939)
    else -> PosAmber
}
