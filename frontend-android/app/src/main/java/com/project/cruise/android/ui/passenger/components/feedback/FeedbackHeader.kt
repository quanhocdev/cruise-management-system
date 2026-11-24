package com.project.cruise.android.ui.passenger.components.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.FeedbackFilterKey

/** Hero động: số lời mời đang chờ viết to màu vàng, ngôi sao mờ làm nền. */
@Composable
fun FeedbackHeroCard(pending: Int, completed: Int, total: Int, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
    ) {
        Icon(
            Icons.Filled.Star,
            contentDescription = null,
            tint = OceanGold.copy(alpha = .16f),
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 28.dp, y = (-18).dp).size(150.dp),
        )
        Icon(
            Icons.Filled.Star,
            contentDescription = null,
            tint = OceanGold.copy(alpha = .10f),
            modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-70).dp, y = 20.dp).size(70.dp),
        )
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("TRẢI NGHIỆM CHUYẾN ĐI", style = MaterialTheme.typography.labelSmall, color = OceanMint)
            if (pending > 0) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        pending.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = OceanGold,
                    )
                    Text(
                        "lời mời đang chờ bạn đánh giá",
                        modifier = Modifier.padding(bottom = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                }
                Text(
                    "Chia sẻ cảm nhận để chúng tôi phục vụ tốt hơn.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = .85f),
                )
            } else {
                Text(
                    "Cảm ơn bạn đã chia sẻ!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OceanGold,
                )
                Text(
                    "Bạn không còn lời mời đánh giá nào đang chờ.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = .85f),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroStat("Đã đánh giá", completed.toString())
                HeroStat("Tổng lời mời", total.toString())
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String) {
    Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = .16f)) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = OceanGold)
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White)
        }
    }
}

/** Bộ lọc trạng thái dạng chip cuộn ngang; chip đang chọn nền vàng. */
@Composable
fun FeedbackFilterChips(
    counts: Map<FeedbackFilterKey, Int>,
    active: FeedbackFilterKey,
    onSelect: (FeedbackFilterKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(FeedbackFilterKey.values().toList(), key = { it.name }) { key ->
            val on = key == active
            Surface(
                onClick = { onSelect(key) },
                shape = RoundedCornerShape(50),
                color = if (on) OceanGold else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (on) OceanGold else OceanLine),
                shadowElevation = if (on) 3.dp else 0.dp,
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        key.label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                        color = OceanNavy,
                    )
                    Surface(shape = RoundedCornerShape(50), color = if (on) OceanNavy else OceanMintSoft) {
                        Text(
                            (counts[key] ?: 0).toString(),
                            Modifier.padding(horizontal = 7.dp, vertical = 1.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (on) OceanGold else OceanTeal,
                        )
                    }
                }
            }
        }
    }
}

/** Thông báo kết quả/lỗi của một hành động (gửi đánh giá, bỏ qua...). */
@Composable
fun FeedbackMessageBanner(text: String, isError: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = if (isError) OceanCoral.copy(alpha = .12f) else OceanGoldSoft,
    ) {
        Row(
            Modifier.padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isError) OceanCoral else OceanNavy,
            )
            TextButton(onClick = onDismiss) {
                Text("Đóng", color = if (isError) OceanCoral else OceanGoldDeep, fontWeight = FontWeight.Bold)
            }
        }
    }
}