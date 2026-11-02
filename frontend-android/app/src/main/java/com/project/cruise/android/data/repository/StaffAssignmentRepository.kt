package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.staff.*
import com.project.cruise.android.data.network.ApiService

class StaffAssignmentRepository(private val api: ApiService, val role: StaffRole) {
    suspend fun assignments(): List<StaffAssignment> = api.getMyStaffAssignments(role.department)

    suspend fun respond(id: String, request: StaffResponseRequest): StaffAssignment {
        require(isUuid(id)) { "Mã phân công không hợp lệ" }
        require(request.status == "ACCEPTED" || request.status == "REJECTED")
        require(request.status != "REJECTED" || !request.rejectionReason.isNullOrBlank())
        return api.respondToStaffAssignment(role.department, id, request)
    }

    suspend fun tour(id: String) = api.getPublicTourDetail(id)
}
