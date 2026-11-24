package com.project.cruise.android.ui.guest.components.tourdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.cruise.android.ui.common.passengerMoney
import com.project.cruise.android.ui.theme.*
import java.math.BigDecimal

/** Ô nhỏ dùng trong băng chuyền ngang cho hoạt động trên tàu, dịch vụ và sản phẩm (đặt độ rộng bằng [modifier]). */
@Composable
fun TourOfferingCard(
    name: String?,
    description: String?,
    price: BigDecimal?,
    imageUrl: String?,
    detail: String? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier,
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OceanLine.copy(alpha = .55f)),
        shadowElevation = 2.dp,
    ) {
        Column {
            Box(
                Modifier.fillMaxWidth().height(112.dp)
                    .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Star, null, tint = OceanGold.copy(alpha = .7f), modifier = Modifier.size(34.dp))
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    name ?: "Đang cập nhật",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                    maxLines = 2,
                    minLines = 2,
                )
                Text(
                    description?.takeIf { it.isNotBlank() } ?: " ",
                    style = MaterialTheme.typography.bodySmall,
                    color = OceanSlate,
                    maxLines = 2,
                    minLines = 2,
                )
                Text(detail ?: " ", style = MaterialTheme.typography.labelSmall, color = OceanSlate, maxLines = 2)
                Surface(shape = RoundedCornerShape(50), color = OceanGoldSoft) {
                    Text(
                        passengerMoney(price),
                        Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = OceanGoldDeep,
                    )
                }
            }
        }
    }
}