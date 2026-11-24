package com.project.cruise.android.ui.passenger.components.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.passenger.notification.NotificationResponse
import com.project.cruise.android.ui.common.PassengerGlyph
import com.project.cruise.android.ui.common.PassengerIcon
import com.project.cruise.android.ui.theme.*

private enum class NotificationKind(val actionLabel: String?) {
    FEEDBACK("Viết đánh giá"),
    BOOKING("Xem đặt chỗ"),
    GENERAL(null),
}

private fun notificationKind(n: NotificationResponse): NotificationKind {
    val ref = n.referenceType?.uppercase()
    val type = n.type?.uppercase()
    return when {
        ref == "FEEDBACK_INVITATION" || type == "FEEDBACK_INVITATION" -> NotificationKind.FEEDBACK
        ref == "BOOKING" -> NotificationKind.BOOKING
        else -> NotificationKind.GENERAL
    }
}

/**
 * Thẻ thông báo. Chưa đọc: nền vàng nhạt, viền vàng, thanh vàng bên trái, tiêu đề đậm, nhãn MỚI.
 * Đã đọc: thẻ trắng phẳng, chữ nhạt hơn để lùi xuống nền.
 */
@Composable
fun NotificationItemCard(
    notification: NotificationResponse,
    onClick: () -> Unit,
    onMarkRead: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unread = !notification.isRead
    val kind = notificationKind(notification)

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = if (unread) Color(0xFFFFFBEB) else Color.White,
        border = BorderStroke(if (unread) 1.5.dp else 1.dp, if (unread) OceanGold else OceanLine.copy(alpha = .6f)),
        shadowElevation = if (unread) 4.dp else 0.dp,
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            if (unread) Box(Modifier.width(5.dp).fillMaxHeight().background(OceanGold))
            Column(
                Modifier.weight(1f).padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                    KindIcon(kind, unread)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                notification.title ?: "Thông báo",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (unread) FontWeight.Bold else FontWeight.Medium,
                                color = if (unread) OceanNavy else OceanNavy.copy(alpha = .7f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (unread) GoldTag("MỚI")
                        }
                        val body = notification.message.orEmpty()
                        if (body.isNotBlank()) {
                            Text(
                                body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (unread) OceanNavy.copy(alpha = .85f) else OceanSlate,
                                maxLines = if (unread) 4 else 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                if (kind.actionLabel != null || unread) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            kind.actionLabel?.let { "$it →" } ?: "",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = OceanGoldDeep,
                        )
                        if (unread) {
                            TextButton(
                                onClick = onMarkRead,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            ) {
                                Icon(Icons.Filled.Check, null, tint = OceanTeal, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Đã đọc",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = OceanTeal,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KindIcon(kind: NotificationKind, unread: Boolean) {
    val bg = if (unread) OceanNavy else OceanMintSoft
    val tint = if (unread) OceanGold else OceanTeal
    Box(
        Modifier.size(44.dp).clip(CircleShape).background(bg),
        contentAlignment = Alignment.Center,
    ) {
        when (kind) {
            NotificationKind.FEEDBACK -> Icon(Icons.Filled.Star, null, tint = tint, modifier = Modifier.size(22.dp))
            NotificationKind.BOOKING -> PassengerIcon(PassengerGlyph.TICKET, tint, Modifier.size(22.dp))
            NotificationKind.GENERAL -> Icon(Icons.Filled.Notifications, null, tint = tint, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun GoldTag(text: String) {
    Surface(shape = RoundedCornerShape(50), color = OceanGold) {
        Text(
            text,
            Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
    }
}