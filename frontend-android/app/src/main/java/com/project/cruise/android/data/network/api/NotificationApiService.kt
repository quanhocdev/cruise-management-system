package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.booking.ActivityNotice
import com.project.cruise.android.data.dto.booking.ActivityReadResult
import com.project.cruise.android.data.dto.booking.ActivityUnreadCount
import com.project.cruise.android.data.dto.booking.RegistrationCommand
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {
    @GET("api/v1/notifications/activities")
    suspend fun getActivityNotices(
        @Query("page") page: Int = 0,
        @Query("unreadOnly") unreadOnly: Boolean = false
    ): List<ActivityNotice>

    @GET("api/v1/notifications/activities/unread-count")
    suspend fun getActivityUnreadCount(): ActivityUnreadCount

    @POST("api/v1/notifications/activities/{id}/read")
    suspend fun readActivityNotice(
        @Path("id") id: String,
        @Body command: RegistrationCommand
    ): ActivityReadResult
}