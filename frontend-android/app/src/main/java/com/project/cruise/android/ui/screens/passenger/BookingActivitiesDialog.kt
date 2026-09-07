package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.activity.compose.BackHandler
import com.project.cruise.android.viewmodel.passenger.BookingHistoryState

@Composable
fun BookingActivitiesDialog(state: BookingHistoryState, onRetry: () -> Unit, onClose: () -> Unit) {
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    var group by rememberSaveable { mutableStateOf("ONBOARD") }
    val activities = state.trip?.activities
    val detail = activities?.firstOrNull { "${it.type}:${it.id}" == selected }
    Dialog(onDismissRequest = { if (selected != null) selected = null else onClose() },
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
        BackHandler(enabled = selected != null) { selected = null }
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = { if (selected != null) selected = null else onClose() }) {
                    Text(if (selected != null) "← Danh sách hoạt động" else "← Chi tiết booking")
                }
                Text(if (selected != null) "Chi tiết hoạt động" else "Hoạt động chuyến đi", style = MaterialTheme.typography.headlineSmall)
                when {
                    state.loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                    state.error != null || state.tripError != null || activities == null ->
                        Text("Chưa tải được hoạt động. Vui lòng thử lại.", color = MaterialTheme.colorScheme.error)
                    selected != null && detail == null -> Text("Hoạt động không còn trong danh sách được cung cấp.")
                    detail != null -> LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { Text(detail.name, style = MaterialTheme.typography.titleLarge) }
                        item { Text(detail.description?.takeIf { it.isNotBlank() } ?: "Chưa có mô tả.") }
                        item { Text("Bắt đầu: ${activityTime(detail.startTime)}") }
                        item { Text("Kết thúc: ${activityTime(detail.endTime)}") }
                        item { Text("Địa điểm: ${detail.location ?: "Đang cập nhật"}") }
                        item { Text("Giá: ${activityPrice(detail.price)}") }
                        item { Text("Sức chứa tối đa: ${detail.maxPassengers?.toString() ?: "Đang cập nhật"}") }
                        item { Text("Chỗ còn lại: Chưa có dữ liệu") }
                        item { Text("Trạng thái: ${activityStatus(detail.status)}") }
                        item { Text("Thông tin đồng bộ từ bộ phận phụ trách. Chưa hỗ trợ đăng ký hoạt động trên màn này.", style = MaterialTheme.typography.bodySmall) }
                    }
                    else -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = group == "ONBOARD", onClick = { group = "ONBOARD" }, label = { Text("Trên tàu") })
                            FilterChip(selected = group == "SHORE", onClick = { group = "SHORE" }, label = { Text("Tham quan") })
                        }
                        val visible = activities.filter { it.type == group }
                        if (visible.isEmpty()) Text("Chưa có hoạt động đã cấu hình trong nhóm này.")
                        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(visible, key = { "${it.type}:${it.id}" }) { activity ->
                                OutlinedCard(onClick = { selected = "${activity.type}:${activity.id}" }, modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(activity.name, style = MaterialTheme.typography.titleMedium)
                                        Text(activityTime(activity.startTime))
                                        Text(activityPrice(activity.price))
                                        Text(activityStatus(activity.status))
                                        Text("Xem chi tiết", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
                OutlinedButton(onClick = onRetry, enabled = !state.loading) { Text("Tải lại hoạt động") }
            }
        }
    }
}
