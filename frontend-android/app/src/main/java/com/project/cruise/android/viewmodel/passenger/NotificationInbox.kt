package com.project.cruise.android.viewmodel.passenger

import com.project.cruise.android.data.dto.passenger.PassengerNotification
import com.project.cruise.android.data.repository.NotificationSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationInboxState(
    val items: List<PassengerNotification> = emptyList(),
    val busy: Boolean = false,
    val loaded: Boolean = false,
    val error: String? = null
) {
    val unreadCount: Int get() = items.count { it.readAt == null }
}

/** Screen-owned state: no disk cache or shared inbox across signed-in accounts. */
class NotificationInbox(private val source: NotificationSource) {
    private val mutable = MutableStateFlow(NotificationInboxState())
    val state = mutable.asStateFlow()

    suspend fun refresh() = operation("Không tải được thông báo. Kiểm tra kết nối rồi thử lại.") {
        mutable.value = mutable.value.copy(items = source.list(), loaded = true)
    }

    suspend fun read(id: Long) {
        if (mutable.value.items.none { it.id == id && it.readAt == null }) return
        operation("Chưa đánh dấu đã đọc được. Vui lòng thử lại.") {
            val updated = source.read(id)
            check(updated.id == id && updated.readAt != null)
            mutable.value = mutable.value.copy(items = mutable.value.items.map { if (it.id == id) updated else it })
        }
    }

    suspend fun readAll() = operation("Chưa xác nhận được trạng thái mới. Hãy tải lại thông báo.") {
        source.readAll()
        // Fetch server timestamps, never invent local read receipts.
        mutable.value = mutable.value.copy(items = source.list(), loaded = true)
    }

    private suspend fun operation(message: String, block: suspend () -> Unit) {
        if (mutable.value.busy) return
        mutable.value = mutable.value.copy(busy = true, error = null)
        try { block() }
        catch (cancel: CancellationException) { throw cancel }
        catch (_: Exception) { mutable.value = mutable.value.copy(error = message) }
        finally { mutable.value = mutable.value.copy(busy = false) }
    }
}
