package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.passenger.notification.NotificationResponse
import com.project.cruise.android.data.network.socket.NotificationSocketService
import com.project.cruise.android.data.repository.notification.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationState(
    val loading: Boolean = false,
    val notifications: List<NotificationResponse> = emptyList(),
    val unreadCount: Long = 0L,
    val socketConnected: Boolean = false,
    val error: String? = null
)

class NotificationViewModel(
    private val repository: NotificationRepository,
    private val tokenManager: TokenManager,
    private val baseUrl: String = "http://10.0.2.2:8080/api"
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state = _state.asStateFlow()

    private val socketService = NotificationSocketService()

    init {
        loadInitialData()
        connectRealtime(baseUrl)
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val list = repository.getAllNotifications()
                val count = repository.getUnreadCount()
                _state.update {
                    it.copy(
                        loading = false,
                        notifications = list,
                        unreadCount = count
                    )
                }
            } catch (_: Exception) {
                _state.update {
                    it.copy(
                        loading = false,
                        error = "Không thể tải thông báo"
                    )
                }
            }
        }
    }

    fun connectRealtime(url: String = baseUrl) {
        val token = tokenManager.getAccessToken().orEmpty()
        if (token.isBlank()) return

        socketService.connect(
            baseUrl = url,
            accessToken = token,
            onConnected = {
                _state.update { it.copy(socketConnected = true, error = null) }
            },
            onMessage = { payload ->
                _state.update { current ->
                    val nextList = if (
                        payload.eventType.equals("NEW_NOTIFICATION", ignoreCase = true) &&
                        payload.notification != null
                    ) {
                        val incoming = payload.notification
                        if (current.notifications.any { it.id == incoming.id }) {
                            current.notifications
                        } else {
                            listOf(incoming) + current.notifications
                        }
                    } else {
                        current.notifications
                    }

                    current.copy(
                        notifications = nextList,
                        unreadCount = payload.unreadCount
                    )
                }
            },
            onError = {
                _state.update { it.copy(socketConnected = false) }
            }
        )
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            try {
                val updatedServer = repository.markAsRead(id)
                _state.update { current ->
                    val target = current.notifications.firstOrNull { it.id == id }
                    val wasUnread = target?.isRead == false
                    val nextList = current.notifications.map { item ->
                        if (item.id == id) {
                            updatedServer ?: item.copy(isRead = true)
                        } else {
                            item
                        }
                    }
                    val nextCount = if (wasUnread) {
                        (current.unreadCount - 1L).coerceAtLeast(0L)
                    } else {
                        current.unreadCount
                    }
                    current.copy(
                        notifications = nextList,
                        unreadCount = nextCount
                    )
                }
            } catch (_: Exception) {
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                repository.markAllAsRead()
                _state.update { current ->
                    current.copy(
                        notifications = current.notifications.map { it.copy(isRead = true) },
                        unreadCount = 0L
                    )
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun onCleared() {
        socketService.disconnect()
        super.onCleared()
    }

    class Factory(
        private val repository: NotificationRepository,
        private val tokenManager: TokenManager,
        private val baseUrl: String = "http://10.0.2.2:8080/api"
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NotificationViewModel(repository, tokenManager, baseUrl) as T
        }
    }
}