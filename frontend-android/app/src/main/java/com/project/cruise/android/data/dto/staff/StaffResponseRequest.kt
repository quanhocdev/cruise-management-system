package com.project.cruise.android.data.dto.staff

import java.util.UUID

data class StaffResponseRequest(
    val status: String,
    val rejectionReason: String?,
    val clientRequestId: String
) {
    companion object {
        fun create(
            accept: Boolean,
            reason: String
        ): StaffResponseRequest {
            require(accept || reason.isNotBlank()) {
                "Vui lòng nhập lý do từ chối"
            }

            return StaffResponseRequest(
                status = if (accept) "ACCEPTED" else "REJECTED",
                rejectionReason = if (accept) null else reason.trim(),
                clientRequestId = UUID.randomUUID().toString()
            )
        }
    }
}