package com.project.cruise.android.ui.screens.passenger

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.passenger.bookingId
import com.project.cruise.android.data.network.ApiService
import com.project.cruise.android.data.repository.NotificationRepository
import com.project.cruise.android.viewmodel.passenger.NotificationInbox
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.project.cruise.android.ui.theme.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape

internal fun notificationTime(value: String?): String = runCatching {
    DateTimeFormatter.ofPattern("HH:mm · dd/MM/yyyy").withZone(ZoneId.systemDefault()).format(Instant.parse(value))
}.getOrDefault("Đang cập nhật")

@Composable
fun NotificationsScreen(api: ApiService, onBack: () -> Unit, onBooking: (Long) -> Unit) {
    val inbox = remember(NotificationApiKey(api)) {
        NotificationInbox(NotificationRepository(api), onFailure = { error ->
            // Log only error category/status; never tokens, message bodies or personal data.
            val status = (error as? retrofit2.HttpException)?.code()?.let { " HTTP $it" }.orEmpty()
            android.util.Log.w("NotificationInbox", "Request failed: ${error.javaClass.simpleName}$status")
        })
    }
    val state by inbox.state.collectAsState()
    val scope = rememberCoroutineScope()
    var selected by rememberSaveable { mutableStateOf<Long?>(null) }
    var unreadOnly by rememberSaveable { mutableStateOf(false) }
    var confirmAll by remember { mutableStateOf(false) }
    val detail = state.items.firstOrNull { it.id == selected }
    LaunchedEffect(inbox) { inbox.refresh() }
    BackHandler(enabled = selected != null) { selected = null }

    OceanTheme {
    if (confirmAll) AlertDialog(onDismissRequest = { confirmAll = false },
        title = { Text("Đánh dấu tất cả đã đọc?") },
        text = { Text("Áp dụng cho thông báo hiện có trên tài khoản của bạn.") },
        confirmButton = { TextButton(onClick = { confirmAll = false; scope.launch { inbox.readAll() } }) { Text("Xác nhận") } },
        dismissButton = { TextButton(onClick = { confirmAll = false }) { Text("Hủy") } })

    Surface(Modifier.fillMaxSize(), color = OceanMist) {
    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OceanHeader(if (detail == null) "Thông báo" else "Chi tiết thông báo",
            onBack = { if (selected != null) selected = null else onBack() }, enabled = !state.busy)
        if (state.loaded) Text("${state.unreadCount} chưa đọc", color = OceanTeal, style = MaterialTheme.typography.labelLarge)
        if (state.busy) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            if (!state.loaded) OceanStatePanel("Đang tải thông báo", "Các cập nhật mới đang được đồng bộ.", "≈")
        }
        state.error?.let { OceanStatePanel("Không tải được thông báo", it, "!", "Thử lại",
            { scope.launch { inbox.refresh() } }, true) }
        if (detail != null) {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text(if (detail.readAt == null) "● Chưa đọc" else "✓ Đã đọc", color = OceanTeal)
                            Text(notificationTime(detail.createdAt), style = MaterialTheme.typography.bodySmall)
                            Text(detail.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(detail.message, style = MaterialTheme.typography.bodyLarge)
                            OceanNotice("Mở chi tiết không tự động đánh dấu đã đọc.")
                        }
                    }
                }
                if (detail.readAt == null) item {
                    FilledTonalButton(onClick = { scope.launch { inbox.read(detail.id) } }, enabled = !state.busy,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("✓  Đánh dấu đã đọc") }
                }
                detail.bookingId()?.let { bookingId -> item {
                    Button(onClick = { onBooking(bookingId) }, enabled = !state.busy,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Mở booking liên quan  →") }
                } }
            }
        } else if (selected != null && state.loaded) {
            OceanStatePanel("Không tìm thấy thông báo", "Thông báo có thể đã được xử lý. Hãy quay lại hoặc tải lại.", "⌁")
            Spacer(Modifier.weight(1f))
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !unreadOnly, onClick = { unreadOnly = false }, label = { Text("Tất cả") })
                FilterChip(selected = unreadOnly, onClick = { unreadOnly = true }, label = { Text("Chưa đọc") })
            }
            val visible = state.items.filter { !unreadOnly || it.readAt == null }
            if (state.loaded && visible.isEmpty()) OceanStatePanel(
                if (unreadOnly) "Không còn tin chưa đọc" else "Chưa có thông báo",
                if (unreadOnly) "Bạn đã đọc hết các cập nhật." else "Các cập nhật hành trình sẽ xuất hiện tại đây.",
                "♧"
            )
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(visible, key = { it.id }) { entry ->
                    Card(onClick = { selected = entry.id }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = if (entry.readAt == null) OceanLavender else Color.White)) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(entry.title, style = MaterialTheme.typography.titleMedium, fontWeight = if (entry.readAt == null) FontWeight.Bold else FontWeight.Normal)
                            Text(if (entry.readAt == null) "● Chưa đọc" else "Đã đọc", color = MaterialTheme.colorScheme.primary)
                            Text(notificationTime(entry.createdAt))
                        }
                    }
                }
            }
            OutlinedButton(onClick = { confirmAll = true }, enabled = !state.busy && state.unreadCount > 0) { Text("Đánh dấu tất cả đã đọc") }
        }
        OutlinedButton(onClick = { scope.launch { inbox.refresh() } }, enabled = !state.busy,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Tải lại") }
    }
    }
    }
}
