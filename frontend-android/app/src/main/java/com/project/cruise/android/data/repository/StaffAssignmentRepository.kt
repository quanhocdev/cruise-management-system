package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.staff.StaffAssignment
import com.project.cruise.android.data.dto.staff.StaffResponseRequest
import com.project.cruise.android.data.dto.staff.StaffRole
import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.data.dto.onboard.ActivityCruiseTourResponse
import com.project.cruise.android.data.dto.shore.ActivityVisitTourResponse
import com.project.cruise.android.data.network.api.StaffApiService
import com.project.cruise.android.data.network.api.TourApiService
import com.project.cruise.android.data.network.api.ConvenienceApiService
import com.project.cruise.android.data.network.api.OnboardApiService
import com.project.cruise.android.data.network.api.ShoreApiService
import java.util.UUID

class StaffAssignmentRepository(
    private val staffApi: StaffApiService,
    private val tourApi: TourApiService,
    private val convenienceApi: ConvenienceApiService,
    private val onboardApi: OnboardApiService,
    private val shoreApi: ShoreApiService,
    val role: StaffRole
) {

    suspend fun assignments(): List<StaffAssignment> {
        return staffApi.getMyStaffAssignments(role.department)
    }

    suspend fun respond(
        id: String,
        request: StaffResponseRequest
    ): StaffAssignment {
        require(isValidUuid(id)) {
            "Mã phân công không hợp lệ"
        }

        require(
            request.status == "ACCEPTED" ||
                    request.status == "REJECTED"
        ) {
            "Trạng thái phản hồi không hợp lệ"
        }

        require(
            request.status != "REJECTED" ||
                    !request.rejectionReason.isNullOrBlank()
        ) {
            "Vui lòng nhập lý do từ chối"
        }

        return staffApi.respondToStaffAssignment(
            department = role.department,
            id = id,
            request = request
        )
    }

    suspend fun tour(id: String): PublicTourDetailResponse {
        return tourApi.getPublicTourDetail(id)
    }

    suspend fun products(
        tourId: String
    ): List<ProductTourResponse> {
        return convenienceApi.getConvenienceProductTours(tourId)
    }

    suspend fun services(
        tourId: String
    ): List<ServiceTourResponse> {
        return convenienceApi.getConvenienceServiceTours(tourId)
    }

    suspend fun onboardActivities(
        tourId: String
    ): List<ActivityCruiseTourResponse> {
        return onboardApi.getOnboardActivityCruiseTours(tourId)
    }

    suspend fun shoreActivities(
        tourId: String
    ): List<ActivityVisitTourResponse> {
        return shoreApi.getShoreActivityVisitTours(tourId)
    }
}

private fun isValidUuid(value: String?): Boolean {
    if (value.isNullOrBlank()) return false

    return runCatching {
        UUID.fromString(value)
    }.isSuccess
}