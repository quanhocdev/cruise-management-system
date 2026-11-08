
package com.project.cruise.android.ui.staff.details

import androidx.compose.runtime.Composable
import com.project.cruise.android.data.dto.staff.StaffAssignment
import com.project.cruise.android.ui.staff.components.StaffDetailRow
import com.project.cruise.android.viewmodel.staff.StaffAssignmentsState

@Composable
fun ShoreAssignmentDetail(
    item: StaffAssignment,
    state: StaffAssignmentsState
) {
    val activity = state.shoreActivityOf(item)

    if (activity != null) {
        StaffDetailRow(
            "Hoạt động chuyến bờ",
            activity.name
        )

        StaffDetailRow("Bắt đầu", activity.startTime)
        StaffDetailRow("Kết thúc", activity.endTime)

        StaffDetailRow(
            "Số khách tối đa",
            activity.maxPassengers?.toString()
        )

        StaffDetailRow(
            "Mô tả",
            activity.description
        )
    } else {
        StaffDetailRow(
            "Hoạt động chuyến bờ",
            "Đang tải thông tin..."
        )
    }
}
