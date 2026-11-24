package com.project.cruise.android.ui.passenger.components.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.*

/** Bộ lọc Tất cả / Chưa đọc (nền navy, mục đang chọn nền vàng) và nút đánh dấu tất cả đã đọc. */
@Composable
fun NotificationToolbar(
    selectedFilter: Int,
    onFilterChange: (Int) -> Unit,
    unreadCount: Long,
    onMarkAllAsRead: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(50), color = OceanNavy) {
            Row(Modifier.padding(4.dp)) {
                FilterChip("Tất cả", selectedFilter == 0, Modifier.weight(1f)) { onFilterChange(0) }
                FilterChip(
                    if (unreadCount > 0) "Chưa đọc ($unreadCount)" else "Chưa đọc",
                    selectedFilter == 1,
                    Modifier.weight(1f),
                ) { onFilterChange(1) }
            }
        }
        if (unreadCount > 0) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onMarkAllAsRead) {
                    Text(
                        "Đánh dấu tất cả đã đọc",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = OceanGoldDeep,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = if (selected) OceanGold else Color.Transparent,
    ) {
        Text(
            label,
            modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (selected) OceanNavy else Color.White.copy(alpha = .85f),
        )
    }
}

/** Nhãn nhóm: "Mới" (chấm vàng) hoặc "Trước đó", kèm số lượng. */
@Composable
fun NotificationSectionLabel(text: String, count: Int, highlight: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(if (highlight) OceanGold else OceanLine))
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        Surface(shape = RoundedCornerShape(50), color = if (highlight) OceanGoldSoft else OceanMintSoft) {
            Text(
                count.toString(),
                Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (highlight) OceanGoldDeep else OceanTeal,
            )
        }
    }
}

/** Nút làm mới tròn đặt ở header; đang tải thì hiện vòng xoay vàng. */
@Composable
fun NotificationRefreshButton(loading: Boolean, onRefresh: () -> Unit) {
    Surface(
        onClick = onRefresh,
        enabled = !loading,
        modifier = Modifier.size(46.dp).semantics { contentDescription = "Làm mới thông báo" },
        shape = CircleShape,
        color = Color.White,
        border = BorderStroke(1.dp, OceanLine),
        shadowElevation = 2.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (loading) {
                CircularProgressIndicator(Modifier.size(20.dp), color = OceanGold, strokeWidth = 2.5.dp)
            } else {
                Icon(Icons.Outlined.Refresh, null, tint = OceanNavy, modifier = Modifier.size(22.dp))
            }
        }
    }
}