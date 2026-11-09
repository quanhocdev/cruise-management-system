package com.project.cruise.android.ui.auth.components.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

/** Huy hiệu tròn gradient chứa một icon, dùng làm hình minh họa ở đầu các màn xác thực. */
@Composable
fun AuthIconBadge(icon: ImageVector, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal))),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = OceanMint, modifier = Modifier.size(38.dp))
    }
}