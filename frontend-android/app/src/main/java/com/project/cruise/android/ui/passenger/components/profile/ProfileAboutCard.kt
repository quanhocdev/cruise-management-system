package com.project.cruise.android.ui.passenger.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

@Composable
fun ProfileAboutCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = OceanMintSoft.copy(alpha = .5f),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                "OceanCruise",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
            )
            Text(
                "Thông tin chuyến đi và đặt chỗ được đồng bộ từ hệ thống quản lý du thuyền.",
                style = MaterialTheme.typography.bodySmall,
                color = OceanSlate,
            )
        }
    }
}