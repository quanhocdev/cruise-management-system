
package com.project.cruise.android.ui.staff.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.staff.StaffAssignment
import com.project.cruise.android.data.dto.staff.StaffRole
import com.project.cruise.android.ui.theme.OceanStatePanel
import com.project.cruise.android.viewmodel.staff.StaffAssignmentsState

@Composable
fun StaffAssignmentList(
    rows: List<StaffAssignment>,
    filter: String,
    role: StaffRole,
    state: StaffAssignmentsState,
    onFilterChange: (String) -> Unit,
    onSelect: (String?) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "ALL" to "Tất cả",
                "PENDING" to "Chờ phản hồi",
                "ACCEPTED" to "Đã nhận",
                "HISTORY" to "Lịch sử"
            ).forEach { (key, label) ->
                FilterChip(
                    selected = filter == key,
                    onClick = { onFilterChange(key) },
                    label = { Text(label) }
                )
            }
        }

        if (
            rows.isEmpty() &&
            !state.loading &&
            state.error == null
        ) {
            OceanStatePanel(
                "Chưa có phân công",
                "Công việc được giao sẽ xuất hiện ở đây. Bạn có thể tải lại để cập nhật.",
                "☷"
            )
        } else {
            rows.forEach { assignment ->
                StaffAssignmentCard(
                    item = assignment,
                    role = role,
                    state = state,
                    onClick = {
                        assignment.id?.let(onSelect)
                    }
                )
            }
        }
    }
}
