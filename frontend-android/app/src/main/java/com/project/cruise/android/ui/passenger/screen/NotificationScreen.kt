package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.passenger.notification.NotificationResponse
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.OceanBanner
import com.project.cruise.android.viewmodel.passenger.NotificationState

@Composable
fun NotificationScreen(
    state: NotificationState,
    onRefresh: () -> Unit,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onNotificationClick: (NotificationResponse) -> Unit,
    onBack: () -> Unit
) {
    PassengerPage("Thông báo", onBack = onBack) {
        item {
            OceanBanner(
                "TRUNG TÂM THÔNG BÁO",
                "Cập nhật mới nhất",
                if (state.unreadCount > 0) {
                    "Bạn có ${state.unreadCount} thông báo chưa đọc."
                } else {
                    "Theo dõi các lời mời đánh giá và thông tin hành trình chuyến đi."
                }
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onRefresh) {
                    Text("Làm mới")
                }

                if (state.unreadCount > 0) {
                    TextButton(onClick = onMarkAllAsRead) {
                        Text("Đánh dấu tất cả đã đọc")
                    }
                }
            }
        }

        when {
            state.loading && state.notifications.isEmpty() -> item { PassengerLoading() }
            state.error != null && state.notifications.isEmpty() -> item {
                PassengerError(state.error!!) { onRefresh() }
            }
            state.notifications.isEmpty() -> item {
                PassengerEmpty("Không có thông báo", "Bạn chưa có thông báo nào.")
            }
            else -> {
                items(state.notifications, key = { it.id }) { notif ->
                    PassengerCard {
                        Surface(
                            onClick = {
                                if (!notif.isRead) {
                                    onMarkAsRead(notif.id)
                                }
                                onNotificationClick(notif)
                            },
                            color = if (notif.isRead) {
                                Color.Transparent
                            } else {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title ?: "Thông báo",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (notif.isRead) FontWeight.Normal else FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (!notif.isRead) {
                                        Badge(containerColor = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Text(
                                    text = notif.message ?: "",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}