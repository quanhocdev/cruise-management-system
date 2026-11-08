
package com.project.cruise.android.ui.staff.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.staff.assignmentStatusLabel
import com.project.cruise.android.ui.theme.OceanTeal

@Composable
fun StaffAssignmentStatus(status: String?) {
    val color = when (status) {
        "PENDING" -> Color(0xFF805A0C)
        "ACCEPTED", "COMPLETED" -> OceanTeal
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        assignmentStatusLabel(status),
        color = color,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .background(
                color.copy(alpha = .1f),
                RoundedCornerShape(50)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 7.dp
            )
    )
}
