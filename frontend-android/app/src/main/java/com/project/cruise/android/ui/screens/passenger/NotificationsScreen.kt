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

internal fun notificationTime(value: String?): String = runCatching {
    DateTimeFormatter.ofPattern("HH:mm · dd/MM/yyyy").withZone(ZoneId.systemDefault()).format(Instant.parse(value))
}.getOrDefault("Đang cập nhật")

@Composable
fun NotificationsScreen(api: ApiService, onBack: () -> Unit, onBooking: (Long) -> Unit) {
    val inbox = remember(api) { NotificationInbox(NotificationRepository(api)) }
    val state by inbox.state.collectAsState()
    val scope = rememberCoroutineScope()
    var selected by rememberSaveable { mutableStateOf<Long?>(null) }
    var unreadOnly by rememberSaveable { mutableStateOf(false) }
    var confirmAll by remember { mutableStateOf(false) }
    val detail = state.items.firstOrNull { it.id == selected }
    LaunchedEffect(inbox) { inbox.refresh() }
    BackHandler(enabled = selected != null) { selected = null }

    if (confirmAll) AlertDialog(onDismissRequest = { confirmAll = false },
        title = { Text("Đánh dấu tất cả đã đọc?") },
        text = { Text("Áp dụng cho thông báo hiện có trên tài khoản của bạn.") },
        confirmButton = { TextButton(onClick = { confirmAll = false; scope.launch { inbox.readAll() } }) { Text("Xác nhận") } },
        dismissButton = { TextButton(onClick = { confirmAll = false }) { Text("Hủy") } })

    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = { if (selected != null) selected = null else onBack() }) {
            Text(if (selected == null) "← Dashboard" else "← Danh sách thông báo")
        }
        Text("Thông báo", style = MaterialTheme.typography.headlineMedium)
        if (state.loaded) Text("${state.unreadCount} chưa đọc")
        if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (detail != null) {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { Text(detail.title, style = MaterialTheme.typography.titleLarge) }
                item { Text(notificationTime(detail.createdAt)) }
                item { Text(if (detail.readAt == null) "Chưa đọc" else "Đã đọc") }
                item { Text(detail.message) }
                if (detail.readAt == null) item {
                    OutlinedButton(onClick = { scope.launch { inbox.read(detail.id) } }, enabled = !state.busy) { Text("Đánh dấu đã đọc") }
                }
                detail.bookingId()?.let { bookingId -> item {
                    Button(onClick = { onBooking(bookingId) }, enabled = !state.busy) { Text("Mở booking liên quan") }
                } }
            }
        } else if (selected != null && state.loaded) {
            Text("Thông báo không còn trong danh sách. Hãy quay lại hoặc tải lại.")
            Spacer(Modifier.weight(1f))
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !unreadOnly, onClick = { unreadOnly = false }, label = { Text("Tất cả") })
                FilterChip(selected = unreadOnly, onClick = { unreadOnly = true }, label = { Text("Chưa đọc") })
            }
            val visible = state.items.filter { !unreadOnly || it.readAt == null }
            if (state.loaded && visible.isEmpty()) Text(if (unreadOnly) "Không có thông báo chưa đọc." else "Chưa có thông báo.")
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(visible, key = { it.id }) { entry ->
                    OutlinedCard(onClick = { selected = entry.id }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(entry.title, style = MaterialTheme.typography.titleMedium)
                            Text(if (entry.readAt == null) "● Chưa đọc" else "Đã đọc", color = MaterialTheme.colorScheme.primary)
                            Text(notificationTime(entry.createdAt))
                        }
                    }
                }
            }
            OutlinedButton(onClick = { confirmAll = true }, enabled = !state.busy && state.unreadCount > 0) { Text("Đánh dấu tất cả đã đọc") }
        }
        OutlinedButton(onClick = { scope.launch { inbox.refresh() } }, enabled = !state.busy) { Text("Tải lại") }
    }
}
