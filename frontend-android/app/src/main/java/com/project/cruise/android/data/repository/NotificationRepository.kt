package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.passenger.PassengerNotification
import com.project.cruise.android.data.network.ApiService
import retrofit2.HttpException

interface NotificationSource {
    suspend fun list(): List<PassengerNotification>
    suspend fun read(id: Long): PassengerNotification
    suspend fun readAll()
}

class NotificationRepository(private val api: ApiService) : NotificationSource {
    override suspend fun list() = api.getNotifications()
    override suspend fun read(id: Long) = api.readNotification(id)
    override suspend fun readAll() {
        val response = api.readAllNotifications()
        if (!response.isSuccessful) throw HttpException(response)
    }
}
