package com.project.cruise.android.data.repository.notification

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.project.cruise.android.data.dto.passenger.notification.NotificationResponse
import com.project.cruise.android.data.network.api.NotificationApiService

class NotificationRepository(
    private val api: NotificationApiService,
    private val gson: Gson = Gson()
) {
    private fun unwrap(raw: JsonElement): JsonElement {
        if (raw.isJsonObject) {
            val obj = raw.asJsonObject
            if (obj.has("data") && !obj.get("data").isJsonNull) return obj.get("data")
        }
        return raw
    }

    suspend fun getAllNotifications(): List<NotificationResponse> {
        val payload = unwrap(api.getAllNotifications())
        val arrayElement = when {
            payload.isJsonArray -> payload
            payload.isJsonObject && payload.asJsonObject.has("content") ->
                payload.asJsonObject.get("content")
            else -> null
        } ?: return emptyList()

        val type = object : TypeToken<List<NotificationResponse>>() {}.type
        return gson.fromJson(arrayElement, type) ?: emptyList()
    }

    suspend fun getUnreadCount(): Long {
        return try {
            val payload = unwrap(api.countUnreadNotifications())
            when {
                payload.isJsonPrimitive && payload.asJsonPrimitive.isNumber -> payload.asLong
                payload.isJsonObject && payload.asJsonObject.has("unreadCount") ->
                    payload.asJsonObject.get("unreadCount").asLong
                else -> 0L
            }
        } catch (_: Exception) {
            0L
        }
    }

    suspend fun markAsRead(id: String): NotificationResponse? {
        return try {
            val payload = unwrap(api.markAsRead(id))
            if (payload.isJsonObject) {
                gson.fromJson(payload, NotificationResponse::class.java)
            } else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun markAllAsRead(): Int {
        return try {
            val payload = unwrap(api.markAllAsRead())
            if (payload.isJsonObject && payload.asJsonObject.has("updatedCount")) {
                payload.asJsonObject.get("updatedCount").asInt
            } else 0
        } catch (_: Exception) {
            0
        }
    }
}