package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.project.cruise.android.data.dto.passenger.notification.NotificationResponse
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.passenger.components.notification.*
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
    // 0 = Tất cả, 1 = Chưa đọc
    var filter by rememberSaveable { mutableStateOf(0) }
    val unread = remember(state.notifications) { state.notifications.filter { !it.isRead } }
    val read = remember(state.notifications) { state.notifications.filter { it.isRead } }
    val error = state.error

    PassengerPage(
        title = "Thông báo",
        subtitle = if (state.unreadCount > 0) "${state.unreadCount} thông báo chưa đọc" else "Bạn đã xem hết thông báo",
        onBack = onBack,
        headerAction = { NotificationRefreshButton(loading = state.loading, onRefresh = onRefresh) },
    ) {
        item(key = "toolbar") {
            NotificationToolbar(
                selectedFilter = filter,
                onFilterChange = { filter = it },
                unreadCount = state.unreadCount,
                onMarkAllAsRead = onMarkAllAsRead,
            )
        }

        when {
            state.loading && state.notifications.isEmpty() -> item(key = "loading") { PassengerLoading() }
            error != null && state.notifications.isEmpty() -> item(key = "error") {
                PassengerError(error) { onRefresh() }
            }
            state.notifications.isEmpty() -> item(key = "empty") {
                PassengerEmpty("Không có thông báo", "Bạn chưa có thông báo nào.")
            }
            else -> {
                if (unread.isNotEmpty()) {
                    item(key = "hdr-unread") { NotificationSectionLabel("Mới", unread.size, highlight = true) }
                    items(unread, key = { "n-${it.id}" }) { notif ->
                        NotificationItemCard(
                            notification = notif,
                            onClick = { onNotificationClick(notif) },
                            onMarkRead = { onMarkAsRead(notif.id) },
                        )
                    }
                } else if (filter == 1) {
                    item(key = "no-unread") {
                        PassengerEmpty("Đã đọc hết", "Bạn không còn thông báo chưa đọc nào.")
                    }
                }

                if (filter == 0 && read.isNotEmpty()) {
                    item(key = "hdr-read") { NotificationSectionLabel("Trước đó", read.size, highlight = false) }
                    items(read, key = { "n-${it.id}" }) { notif ->
                        NotificationItemCard(
                            notification = notif,
                            onClick = { onNotificationClick(notif) },
                            onMarkRead = {},
                        )
                    }
                }
            }
        }
    }
}