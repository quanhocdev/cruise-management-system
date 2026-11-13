package com.project.cruise.android.data.repository.notification

import com.project.cruise.android.data.dto.passenger.notification.NotificationResponse
import com.project.cruise.android.data.network.api.NotificationApiService
import java.util.UUID

class NotificationRepository(
    private val api: NotificationApiService
) {
    suspend fun getAllNotifications(): List<NotificationResponse> {
        return api.getAllNotifications()
    }

    suspend fun getUnreadCount(): Long {
        return try {
            val response = api.countUnreadNotifications()
            response["unreadCount"] ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    suspend fun markAsRead(id: UUID): NotificationResponse {
        return api.markAsRead(id)
    }

    suspend fun markAllAsRead(): Int {
        return try {
            val response = api.markAllAsRead()
            response["updatedCount"] ?: 0
        } catch (_: Exception) {
            0
        }
    }
}