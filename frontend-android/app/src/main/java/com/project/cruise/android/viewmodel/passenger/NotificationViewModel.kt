package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.passenger.notification.NotificationResponse
import com.project.cruise.android.data.repository.notification.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class NotificationState(
    val loading: Boolean = false,
    val notifications: List<NotificationResponse> = emptyList(),
    val unreadCount: Long = 0L,
    val error: String? = null
)

class NotificationViewModel(
    private val repository: NotificationRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state = _state.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val list = repository.getAllNotifications()
                val count = repository.getUnreadCount()
                _state.value = _state.value.copy(
                    loading = false,
                    notifications = list,
                    unreadCount = count
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = "Không thể tải thông báo"
                )
            }
        }
    }

    fun markAsRead(id: UUID) {
        viewModelScope.launch {
            try {
                repository.markAsRead(id)
                val updatedList = _state.value.notifications.map {
                    if (it.id == id) it.copy(isRead = true) else it
                }
                val newCount = updatedList.count { !it.isRead }.toLong()
                _state.value = _state.value.copy(
                    notifications = updatedList,
                    unreadCount = newCount
                )
            } catch (_: Exception) {}
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                repository.markAllAsRead()
                val updatedList = _state.value.notifications.map { it.copy(isRead = true) }
                _state.value = _state.value.copy(
                    notifications = updatedList,
                    unreadCount = 0L
                )
            } catch (_: Exception) {}
        }
    }

    class Factory(
        private val repository: NotificationRepository,
        private val tokenManager: TokenManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NotificationViewModel(repository, tokenManager) as T
        }
    }
}