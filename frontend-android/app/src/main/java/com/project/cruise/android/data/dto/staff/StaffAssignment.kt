
package com.project.cruise.android.data.dto.staff

import java.util.Locale
import java.util.UUID

enum class StaffRole(
    val department: String,
    val title: String
) {
    ONBOARD_STAFF("onboard-staff", "Nhân viên trên tàu"),
    CONVENIENCE_STAFF("convenience-staff", "Nhân viên tiện ích"),
    SHORE_STAFF("shore-staff", "Nhân viên chuyến bờ");

    companion object {
        fun fromApiRole(value: String?): StaffRole? {
            val role = value
                ?.trim()
                ?.uppercase(Locale.ROOT)
                ?.removePrefix("ROLE_")

            return entries.firstOrNull { it.name == role }
        }
    }
}

data class StaffAssignment(
    val id: String? = null,
    val tourId: String? = null,

    val assignmentType: String? = null,

    val cruiseDeckId: String? = null,
    val activityCruiseTourId: String? = null,
    val targetId: String? = null,
    val visitTourId: String? = null,

    val userId: String? = null,
    val username: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,

    val status: String? = null,
    val rejectionReason: String? = null,

    val assignedBy: String? = null,
    val assignedAt: String? = null,
    val respondedAt: String? = null
) {
    val canRespond: Boolean
        get() = status == "PENDING" && isUuid(id)

    val targetReference: String?
        get() = cruiseDeckId
            ?: activityCruiseTourId
            ?: targetId
            ?: visitTourId

    fun title(role: StaffRole): String = when {
        role == StaffRole.SHORE_STAFF ->
            "Phụ trách chuyến bờ"

        assignmentType == "PATROL" ->
            "Tuần tra tầng tàu"

        assignmentType == "ACTIVITY_CRUISE" ->
            "Phụ trách hoạt động"

        assignmentType == "PRODUCT" ->
            "Phụ trách sản phẩm"

        assignmentType == "SERVICE" ->
            "Phụ trách dịch vụ"

        else ->
            "Phân công công việc"
    }
}

fun isUuid(value: String?): Boolean {
    return value != null && runCatching {
        UUID.fromString(value)
            .toString()
            .equals(value, ignoreCase = true)
    }.getOrDefault(false)
}

fun assignmentStatusLabel(value: String?): String {
    return when (value) {
        "PENDING" -> "Chờ phản hồi"
        "ACCEPTED" -> "Đã nhận"
        "REJECTED" -> "Đã từ chối"
        "COMPLETED" -> "Hoàn thành"
        "CANCELLED" -> "Đã hủy"
        else -> "Chưa rõ trạng thái"
    }
}
