package com.project.cruise.android.data.repository.notification

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.network.core.RetrofitClient
import com.project.cruise.android.data.repository.pos.OnlinePosCommands

interface ActivityInboxSource {
    suspend fun list(page: Int, unreadOnly: Boolean): List<ActivityNotice>
    suspend fun unreadCount(): Long
    suspend fun markRead(id: String): ActivityReadResult
}

class ActivityInboxRepository(context: Context) : ActivityInboxSource {
    // Đổi sang NotificationApiService
    private val api = RetrofitClient.createNotificationApiService(TokenManager(context.applicationContext))
    private val commands = OnlinePosCommands(context.applicationContext)

    override suspend fun list(page: Int, unreadOnly: Boolean) = api.getActivityNotices(page, unreadOnly)
    override suspend fun unreadCount() = api.getActivityUnreadCount().unreadCount
    override suspend fun markRead(id: String) = commands.execute("ACTIVITY_READ:$id", mapOf("notificationId" to id)) { key ->
        api.readActivityNotice(id, RegistrationCommand(key))
    }
}