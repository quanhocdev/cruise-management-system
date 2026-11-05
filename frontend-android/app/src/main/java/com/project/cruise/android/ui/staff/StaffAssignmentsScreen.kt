
package com.project.cruise.android.ui.staff

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.staff.StaffRole
import com.project.cruise.android.ui.staff.components.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.staff.StaffAssignmentsState

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
    var confirm by rememberSaveable(state.selectedId) {
        mutableStateOf<String?>(null)
    }
    var reason by rememberSaveable(state.selectedId) {
        mutableStateOf("")
    }
    var logoutConfirmation by remember {
        mutableStateOf(false)
    }

    val selected = state.selected

    BackHandler(state.selectedId != null) {
        if (!state.responding) {
            onSelect(null)
        }
    }

    val rows = state.assignments.filter {
        when (filter) {
            "PENDING", "ACCEPTED" -> it.status == filter
            "HISTORY" -> it.status !in listOf("PENDING", "ACCEPTED")
            else -> true
        }
    }

    OceanTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = OceanMist
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .imePadding()
                    .testTag("staff-assignments"),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    StaffAssignmentHeader(
                        role = role,
                        username = username,
                        selectedId = state.selectedId,
                        responding = state.responding,
                        busy = state.busy,
                        onBack = { onSelect(null) },
                        onLogout = { logoutConfirmation = true }
                    )
                }

                if (state.selectedId == null) {
                    item {
                        OceanBanner(
                            role.title.uppercase(),
                            "Xin chào, $username",
                            "Theo dõi công việc và phản hồi phân công của bạn."
                        )
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StaffSummaryTile(
                                label = "Chờ phản hồi",
                                count = state.assignments.count {
                                    it.status == "PENDING"
                                },
                                modifier = Modifier.weight(1f)
                            )

                            StaffSummaryTile(
                                label = "Đã nhận",
                                count = state.assignments.count {
                                    it.status == "ACCEPTED"
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selected != null) {
                                "Chi tiết công việc"
                            } else {
                                "Phân công của bạn"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(
                            onClick = onRefresh,
                            enabled = !state.busy
                        ) {
                            Text("Tải lại")
                        }
                    }

                    if (state.busy) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                state.error?.let { error ->
                    item {
                        OceanNotice(error, error = true)
                    }
                }

                state.message?.let { message ->
                    item {
                        OceanNotice(message)
                    }
                }

                if (state.selectedId != null) {
                    if (selected == null) {
                        item {
                            OceanStatePanel(
                                "Phân công không còn trong danh sách",
                                "Quay lại danh sách để chọn công việc khác.",
                                "↻"
                            )
                        }
                    } else {
                        item {
                            StaffAssignmentDetail(
                                item = selected,
                                role = role,
                                state = state
                            )
                        }

                        if (selected.canRespond) {
                            item {
                                StaffAssignmentActions(
                                    enabled = state.canRespond(selected),
                                    loading = state.responding,
                                    onAccept = {
                                        confirm = "ACCEPTED"
                                    },
                                    onReject = {
                                        confirm = "REJECTED"
                                    }
                                )
                            }
                        }
                    }
                } else {
                    item {
                        StaffAssignmentList(
                            rows = rows,
                            filter = filter,
                            role = role,
                            state = state,
                            onFilterChange = { filter = it },
                            onSelect = onSelect
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }

    if (confirm != null && selected != null) {
        StaffRejectDialog(
            confirmation = confirm!!,
            reason = reason,
            canRespond = state.canRespond(selected),
            onReasonChange = { reason = it },
            onDismiss = { confirm = null },
            onConfirm = { accepting ->
                onRespond(accepting, reason)
                confirm = null
            }
        )
    }

    if (logoutConfirmation) {
        AlertDialog(
            onDismissRequest = {
                logoutConfirmation = false
            },
            title = {
                Text("Đăng xuất tài khoản?")
            },
            text = {
                Text("Bạn có thể đăng nhập lại để tiếp tục xem phân công.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        logoutConfirmation = false
                        onLogout()
                    }
                ) {
                    Text("Đăng xuất")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        logoutConfirmation = false
                    }
                ) {
                    Text("Ở lại")
                }
            }
        )
    }
}
