package com.project.cruise.android.data.network.api

import com.project.cruise.android.data.dto.booking.ActivityRegistration
import com.project.cruise.android.data.dto.booking.RegistrationAttendance
import com.project.cruise.android.data.dto.staff.StaffAssignment
import com.project.cruise.android.data.dto.staff.StaffResponseRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StaffApiService {
    @GET("api/{department}/assignments")
    suspend fun getMyStaffAssignments(
        @Path("department") department: String
    ): List<StaffAssignment>

    @PATCH("api/{department}/assignments/{id}/respond")
    suspend fun respondToStaffAssignment(
        @Path("department") department: String,
        @Path("id") id: String,
        @Body request: StaffResponseRequest
    ): StaffAssignment

    @GET("api/{department}/{usage}/registrations")
    suspend fun getStaffRegistrations(
        @Path("department") department: String,
        @Path("usage") usage: String,
        @Query("bookingCode") bookingCode: String,
        @Query("search") search: String,
        @Query("page") page: Int
    ): List<ActivityRegistration>

    @POST("api/{department}/{usage}/registrations/{id}/attend")
    suspend fun attendRegistration(
        @Path("department") department: String,
        @Path("usage") usage: String,
        @Path("id") id: Long,
        @Body request: RegistrationAttendance
    ): ActivityRegistration
}