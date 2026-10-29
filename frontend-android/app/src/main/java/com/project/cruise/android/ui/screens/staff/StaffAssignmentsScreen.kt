package com.project.cruise.android.ui.screens.staff

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.staff.*
import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.staff.StaffAssignmentsState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StaffAssignmentsScreen(
    role: StaffRole,
    username: String,
    state: StaffAssignmentsState,
    onRefresh: () -> Unit,
    onSelect: (String?) -> Unit,
    onRespond: (Boolean, String) -> Unit,
    onLogout: () -> Unit
) {
    var filter by rememberSaveable { mutableStateOf("ALL") }
    var confirm by rememberSaveable(state.selectedId) { mutableStateOf<String?>(null) }
    var reason by rememberSaveable(state.selectedId) { mutableStateOf("") }
    var logoutConfirmation by remember { mutableStateOf(false) }
    val selected = state.selected
    BackHandler(state.selectedId != null) { if (!state.responding) onSelect(null) }
    val rows = state.assignments.filter {
        when (filter) {
            "PENDING", "ACCEPTED" -> it.status == filter
            "HISTORY" -> it.status !in listOf("PENDING", "ACCEPTED")
            else -> true
        }
    }
    OceanTheme {
        Surface(Modifier.fillMaxSize(), color = OceanMist) {
            LazyColumn(
                Modifier.fillMaxSize().safeDrawingPadding().imePadding().testTag("staff-assignments"),
                contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        if (state.selectedId != null) {
                            TextButton(onClick = { onSelect(null) }, enabled = !state.responding) { Text("← Phân công") }
                        } else {
                            Text("Công việc của tôi", fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        }
                        if (state.selectedId != null) Spacer(Modifier.weight(1f))
                        TextButton(onClick = { logoutConfirmation = true }, enabled = !state.busy) { Text("Đăng xuất") }
                    }
                }
                if (state.selectedId == null) {
                    item {
                        OceanBanner(role.title.uppercase(), "Xin chào, $username",
                            "Theo dõi công việc và phản hồi phân công của bạn.")
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SummaryTile("Chờ phản hồi", state.assignments.count { it.status == "PENDING" }, Modifier.weight(1f))
                            SummaryTile("Đã nhận", state.assignments.count { it.status == "ACCEPTED" }, Modifier.weight(1f))
                        }
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(if (selected != null) "Chi tiết công việc" else "Phân công của bạn",
                            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        TextButton(onClick = onRefresh, enabled = !state.busy) { Text("Tải lại") }
                    }
                    if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                }
                state.error?.let { item { OceanNotice(it, error = true) } }
                state.message?.let { item { OceanNotice(it) } }
                if (state.selectedId != null) {
                    if (selected == null) {
                        item { OceanStatePanel("Phân công không còn trong danh sách", "Quay lại danh sách để chọn công việc khác.", "↻") }
                    } else {
                        item {
                            AssignmentDetail(selected, role, state.tours[selected.tourId])
                        }
                        if (selected.canRespond) {
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OceanPrimaryButton("Chấp nhận phân công", { confirm = "ACCEPTED" },
                                        enabled = state.canRespond(selected), loading = state.responding)
                                    OutlinedButton(onClick = { confirm = "REJECTED" }, enabled = state.canRespond(selected),
                                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(18.dp)) {
                                        Text("Từ chối phân công")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("ALL" to "Tất cả", "PENDING" to "Chờ phản hồi", "ACCEPTED" to "Đã nhận", "HISTORY" to "Lịch sử").forEach { (key, label) ->
                                FilterChip(selected = filter == key, onClick = { filter = key }, label = { Text(label) })
                            }
                        }
                    }
                    if (rows.isEmpty() && !state.loading && state.error == null) {
                        item { OceanStatePanel("Chưa có phân công", "Công việc được giao sẽ xuất hiện ở đây. Bạn có thể tải lại để cập nhật.", "☷") }
                    }
                    itemsIndexed(rows) { _, assignment ->
                        AssignmentCard(assignment, role, state.tours[assignment.tourId]) {
                            assignment.id?.let(onSelect)
                        }
                    }
                }
                item { Spacer(Modifier.height(12.dp)) }
            }
        }
        if (confirm != null && selected != null) {
            val accepting = confirm == "ACCEPTED"
            AlertDialog(
                onDismissRequest = { confirm = null },
                title = { Text(if (accepting) "Nhận công việc này?" else "Từ chối phân công?") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(if (accepting) "Xác nhận bạn nhận phân công này. Sau khi gửi, bạn không thể tự đổi phản hồi."
                            else "Cho người phụ trách biết lý do bạn không thể nhận công việc.")
                        if (!accepting) OutlinedTextField(value = reason, onValueChange = { reason = it },
                            label = { Text("Lý do từ chối (bắt buộc)") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    }
                },
                confirmButton = {
                    TextButton(enabled = state.canRespond(selected) && (accepting || reason.isNotBlank()), onClick = {
                        onRespond(accepting, reason)
                        confirm = null
                    }) { Text(if (accepting) "Xác nhận nhận việc" else "Gửi lý do từ chối") }
                },
                dismissButton = { TextButton(onClick = { confirm = null }) { Text("Quay lại") } }
            )
        }
        if (logoutConfirmation) AlertDialog(onDismissRequest = { logoutConfirmation = false },
            title = { Text("Đăng xuất tài khoản?") }, text = { Text("Bạn có thể đăng nhập lại để tiếp tục xem phân công.") },
            confirmButton = { TextButton(onClick = { logoutConfirmation = false; onLogout() }) { Text("Đăng xuất") } },
            dismissButton = { TextButton(onClick = { logoutConfirmation = false }) { Text("Ở lại") } })
    }
}

@Composable
private fun SummaryTile(label: String, count: Int, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(20.dp), color = Color.White) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(count.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = OceanNavy)
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun StatusPill(status: String?) {
    val color = when (status) {
        "PENDING" -> Color(0xFF805A0C)
        "ACCEPTED", "COMPLETED" -> OceanTeal
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(assignmentStatusLabel(status), color = color, style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.background(color.copy(alpha = .1f), RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 7.dp))
}

@Composable
private fun AssignmentCard(item: StaffAssignment, role: StaffRole, tour: PublicTourDetailResponse?, onClick: () -> Unit) {
    Card(onClick = onClick, enabled = item.id != null, modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                StatusPill(item.status)
                Spacer(Modifier.weight(1f))
                Text("Chi tiết ›", color = OceanTeal, style = MaterialTheme.typography.labelLarge)
            }
            Text(targetName(item, tour) ?: item.title(role), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(tour?.name?.takeIf { it.isNotBlank() } ?: "Chuyến ${item.tourId?.take(8) ?: "chưa xác định"}",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Phân công: ${displayTime(item.assignedAt)}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AssignmentDetail(item: StaffAssignment, role: StaffRole, tour: PublicTourDetailResponse?) {
    Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            StatusPill(item.status)
            Text(targetName(item, tour) ?: item.title(role), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            DetailField("Công việc", item.title(role))
            DetailField("Chuyến tàu", tour?.name?.takeIf { it.isNotBlank() } ?: "Chưa có tên chuyến")
            tour?.cruise?.name?.let { DetailField("Tàu", it) }
            DetailField("Mã chuyến", item.tourId ?: "—")
            DetailField(if (item.assignmentType == "PATROL") "Mã tầng được giao" else "Mã mục được giao", item.targetReference ?: "—")
            HorizontalDivider()
            DetailField("Ngày phân công", displayTime(item.assignedAt))
            item.respondedAt?.let { DetailField("Ngày phản hồi", displayTime(it)) }
            item.rejectionReason?.takeIf { it.isNotBlank() }?.let { DetailField("Lý do từ chối", it) }
            Text("Thông tin ca làm cụ thể: liên hệ người phụ trách.", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun targetName(item: StaffAssignment, tour: PublicTourDetailResponse?): String? = when (item.assignmentType) {
    "ACTIVITY_CRUISE" -> tour?.onboardActivities?.find { it.id == item.activityCruiseTourId }?.activityName
    "PRODUCT" -> tour?.products?.find { it.id == item.targetId }?.productName
    "SERVICE" -> tour?.services?.find { it.id == item.targetId }?.serviceName
    else -> tour?.schedules?.flatMap { it.stops.orEmpty() }?.mapNotNull { it.visitActivity }
        ?.find { it.id == item.visitTourId }?.visitName
}?.takeIf { it.isNotBlank() }

private fun displayTime(value: String?): String = value?.let {
    runCatching { LocalDateTime.parse(it).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) }.getOrDefault(it)
} ?: "—"
