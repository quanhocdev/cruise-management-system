package com.project.cruise.android.ui.passenger.components.mybookingdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*

@Composable
fun ContactInfoCard(name: String?, phone: String?, email: String?) {
    PassengerCard {
        DetailRow(Icons.Filled.Person, "Họ và tên", name)
        HorizontalDivider(color = OceanLine)
        DetailRow(Icons.Filled.Phone, "Số điện thoại", phone)
        HorizontalDivider(color = OceanLine)
        DetailRow(Icons.Filled.Email, "Email", email)
    }
}

/** Dòng "icon tròn + nhãn + giá trị", dùng chung cho các card trong màn chi tiết booking. */
@Composable
internal fun DetailRow(icon: ImageVector, label: String, value: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(OceanMintSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = OceanTeal, modifier = Modifier.size(18.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = OceanSlate)
            Text(
                value?.takeIf { it.isNotBlank() } ?: "Chưa cập nhật",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = OceanNavy,
            )
        }
    }
}