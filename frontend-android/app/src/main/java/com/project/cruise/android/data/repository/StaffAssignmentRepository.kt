package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.staff.StaffAssignment
import com.project.cruise.android.data.dto.staff.StaffResponseRequest
import com.project.cruise.android.data.dto.staff.StaffRole
import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.data.dto.onboard.ActivityCruiseTourResponse
import com.project.cruise.android.data.dto.shore.ActivityVisitTourResponse
import com.project.cruise.android.data.network.ApiService
import java.util.UUID

class StaffAssignmentRepository(
    private val api: ApiService,
    val role: StaffRole
) {

    suspend fun assignments(): List<StaffAssignment> {
        return api.getMyStaffAssignments(role.department)
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

        return api.respondToStaffAssignment(
            department = role.department,
            id = id,
            request = request
        )
    }

    suspend fun tour(id: String): PublicTourDetailResponse {
        return api.getPublicTourDetail(id)
    }

    suspend fun products(
        tourId: String
    ): List<ProductTourResponse> {
        return api.getConvenienceProductTours(tourId)
    }

    suspend fun services(
        tourId: String
    ): List<ServiceTourResponse> {
        return api.getConvenienceServiceTours(tourId)
    }

    suspend fun onboardActivities(
        tourId: String
    ): List<ActivityCruiseTourResponse> {
        return api.getOnboardActivityCruiseTours(tourId)
    }

    suspend fun shoreActivities(
        tourId: String
    ): List<ActivityVisitTourResponse> {
        return api.getShoreActivityVisitTours(tourId)
    }
}

private fun isValidUuid(value: String?): Boolean {
    if (value.isNullOrBlank()) return false

    return runCatching {
        UUID.fromString(value)
    }.isSuccess
}