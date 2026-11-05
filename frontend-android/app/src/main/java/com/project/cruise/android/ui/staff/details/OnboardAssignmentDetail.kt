
package com.project.cruise.android.ui.staff.details

import androidx.compose.runtime.Composable
import com.project.cruise.android.data.dto.staff.StaffAssignment
import com.project.cruise.android.ui.staff.components.StaffDetailRow
import com.project.cruise.android.viewmodel.staff.StaffAssignmentsState

@Composable
fun OnboardAssignmentDetail(
    item: StaffAssignment,
    state: StaffAssignmentsState
) {
    when (item.assignmentType) {
        "ACTIVITY_CRUISE" -> {
            val activity = state.onboardActivityOf(item)

            if (activity != null) {
                StaffDetailRow(
                    "Hoạt động",
                    activity.activityCruiseName
                )

                StaffDetailRow(
                    "Khu vực",
                    activity.cruiseAreaName
                )

                StaffDetailRow("Bắt đầu", activity.startTime)
                StaffDetailRow("Kết thúc", activity.endTime)

                StaffDetailRow(
                    "Số khách tối đa",
                    activity.maxPassengers?.toString()
                )

                StaffDetailRow(
                    "Mô tả",
                    activity.activityCruiseDescription
                )
            } else {
                StaffDetailRow("Hoạt động", "Đang tải thông tin...")
            }
        }

        "PATROL" -> {
            StaffDetailRow(
                "Nhiệm vụ",
                "Tuần tra tầng tàu"
            )

            StaffDetailRow(
                "Mã tầng tàu",
                item.cruiseDeckId ?: "Chưa có thông tin"
            )
        }
    }
}
