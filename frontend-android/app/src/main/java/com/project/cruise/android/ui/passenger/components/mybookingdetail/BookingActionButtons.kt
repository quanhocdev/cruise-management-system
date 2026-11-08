package com.project.cruise.android.ui.passenger.components.mybookingdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

/** Action nào truyền null thì sẽ không hiển thị. */
@Composable
fun BookingActionButtons(
    onActivities: (() -> Unit)?,
    onSpending: (() -> Unit)?,
    onBill: (() -> Unit)?,
    onFeedback: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    if (listOf(onActivities, onSpending, onBill, onFeedback).all { it == null }) return
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        onActivities?.let {
            Button(
                onClick = it,
                modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OceanTeal, contentColor = Color.White),
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Đăng ký hoạt động & tham quan bờ", fontWeight = FontWeight.SemiBold)
            }
        }
        onSpending?.let { ActionRow(Icons.Filled.ShoppingCart, "Xem chi tiêu trong chuyến", it) }
        onBill?.let { ActionRow(Icons.Filled.Info, "Bill dịch vụ & biên nhận", it) }
        onFeedback?.let { ActionRow(Icons.Filled.Star, "Xem đánh giá của tôi", it) }
    }
}

@Composable
private fun ActionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, OceanLine),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(38.dp).clip(CircleShape).background(OceanMintSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = OceanTeal, modifier = Modifier.size(20.dp))
            }
            Text(
                label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = OceanNavy,
            )
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = OceanSlate)
        }
    }
}