
package com.project.cruise.android.ui.staff.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.OceanPrimaryButton

@Composable
fun StaffAssignmentActions(
    enabled: Boolean,
    loading: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OceanPrimaryButton(
            "Chấp nhận phân công",
            onClick = onAccept,
            enabled = enabled,
            loading = loading
        )

        OutlinedButton(
            onClick = onReject,
            enabled = enabled && !loading,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Từ chối phân công")
        }
    }
}
