
package com.project.cruise.android.ui.staff.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.staff.StaffAssignment
import com.project.cruise.android.data.dto.staff.StaffRole
import com.project.cruise.android.ui.staff.details.ConvenienceAssignmentDetail
import com.project.cruise.android.ui.staff.details.OnboardAssignmentDetail
import com.project.cruise.android.ui.staff.details.ShoreAssignmentDetail
import com.project.cruise.android.viewmodel.staff.StaffAssignmentsState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StaffAssignmentDetail(
    item: StaffAssignment,
    role: StaffRole,
    state: StaffAssignmentsState
) {
    val tour = item.tourId?.let { state.tours[it] }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                state.targetName(item) ?: item.title(role),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            StaffAssignmentStatus(item.status)

            StaffDetailRow(
                "Chuyến du lịch",
                tour?.name ?: item.tourId
            )

            StaffDetailRow(
                "Mã phân công",
                item.id
            )

            when (item.assignmentType) {
                "PRODUCT", "SERVICE" -> {
                    ConvenienceAssignmentDetail(item, state)
                }

                "ACTIVITY_CRUISE", "PATROL" -> {
                    OnboardAssignmentDetail(item, state)
                }

                else -> {
                    ShoreAssignmentDetail(item, state)
                }
            }

            StaffDetailRow(
                "Người giao việc",
                item.assignedBy
            )

            StaffDetailRow(
                "Thời gian phân công",
                displayStaffDetailTime(item.assignedAt)
            )

            StaffDetailRow(
                "Thời gian phản hồi",
                displayStaffDetailTime(item.respondedAt)
            )

            if (!item.rejectionReason.isNullOrBlank()) {
                StaffDetailRow(
                    "Lý do từ chối",
                    item.rejectionReason
                )
            }
        }
    }
}

@Composable
fun StaffDetailRow(
    label: String,
    value: String?
) {
    if (value.isNullOrBlank()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun displayStaffDetailTime(value: String?): String {
    if (value.isNullOrBlank()) return "—"

    return runCatching {
        LocalDateTime.parse(value).format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        )
    }.getOrDefault(value)
}
