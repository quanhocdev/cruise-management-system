
package com.project.cruise.android.ui.staff.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.staff.StaffAssignment
import com.project.cruise.android.data.dto.staff.StaffRole
import com.project.cruise.android.ui.theme.OceanTeal
import com.project.cruise.android.viewmodel.staff.StaffAssignmentsState

@Composable
fun StaffAssignmentCard(
    item: StaffAssignment,
    role: StaffRole,
    state: StaffAssignmentsState,
    onClick: () -> Unit
) {
    val tour = item.tourId?.let { state.tours[it] }

    Card(
        onClick = onClick,
        enabled = item.id != null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StaffAssignmentStatus(item.status)

                Spacer(Modifier.weight(1f))

                Text(
                    "Chi tiết ›",
                    color = OceanTeal,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Text(
                text = state.targetName(item) ?: item.title(role),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = tour?.name?.takeIf { it.isNotBlank() }
                    ?: "Chuyến ${item.tourId?.take(8) ?: "chưa xác định"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                "Phân công: ${displayStaffTime(item.assignedAt)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
private fun displayStaffTime(value: String?): String {
    if (value.isNullOrBlank()) return "—"

    return runCatching {
        java.time.LocalDateTime.parse(value).format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        )
    }.getOrDefault(value)
}
