package com.project.cruise.android.ui.screens.passenger

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.project.cruise.android.data.network.ApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun NotificationDashboardButton(api: ApiService, onClick: () -> Unit) {
    var count by remember { mutableStateOf<Long?>(null) }
    val owner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    DisposableEffect(owner, NotificationApiKey(api)) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) scope.launch {
                count = null
                try { count = api.getUnreadNotifications().unreadCount.coerceAtLeast(0) }
                catch (cancel: CancellationException) { throw cancel }
                catch (_: Exception) { count = null }
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    com.project.cruise.android.ui.theme.OceanAction("Thông báo",
        count?.let { "$it chưa đọc · Cập nhật hành trình" } ?: "Chưa tải được số chưa đọc", "♧", onClick)
}
