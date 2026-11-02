package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.network.RetrofitClient

interface ActivityInboxSource {
    suspend fun list(page: Int,unreadOnly: Boolean): List<ActivityNotice>
    suspend fun unreadCount(): Long
    suspend fun markRead(id: String): ActivityReadResult
}

class ActivityInboxRepository(context: Context): ActivityInboxSource {
    private val api=RetrofitClient.createApiService(TokenManager(context.applicationContext))
    private val commands=OnlinePosCommands(context.applicationContext)
    override suspend fun list(page: Int,unreadOnly: Boolean)=api.getActivityNotices(page,unreadOnly)
    override suspend fun unreadCount()=api.getActivityUnreadCount().unreadCount
    override suspend fun markRead(id: String)=commands.execute("ACTIVITY_READ:$id",mapOf("notificationId" to id)) { key ->
        api.readActivityNotice(id,RegistrationCommand(key))
    }
}
