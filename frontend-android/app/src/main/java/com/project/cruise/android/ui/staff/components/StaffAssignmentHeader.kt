
package com.project.cruise.android.ui.staff.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.staff.StaffRole

@Composable
fun StaffAssignmentHeader(
    role: StaffRole,
    username: String,
    selectedId: String?,
    responding: Boolean,
    busy: Boolean,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selectedId != null) {
            TextButton(
                onClick = onBack,
                enabled = !responding
            ) {
                Text("← Phân công")
            }
        } else {
            Text(
                text = "Công việc của tôi",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        if (selectedId != null) {
            Spacer(Modifier.weight(1f))
        }

        TextButton(
            onClick = onLogout,
            enabled = !busy
        ) {
            Text("Đăng xuất")
        }
    }
}


@Composable
fun StaffSummaryTile(
    label: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = androidx.compose.ui.graphics.Color.White
    ) {
        androidx.compose.foundation.layout.Column(
            Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                count.toString(),
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = com.project.cruise.android.ui.theme.OceanNavy
            )

            Text(
                label,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
        }
    }
}
