package com.project.cruise.android.viewmodel.passenger

import com.project.cruise.android.data.dto.passenger.CreateBookingRequest
import com.project.cruise.android.data.dto.passenger.CreatePassengerRequest
import java.time.LocalDate
import java.util.UUID

data class PassengerDraft(
    val fullName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val phone: String = "",
    val email: String = ""
)

data class BookingDraft(
    val contactName: String = "",
    val contactPhone: String = "",
    val passengers: List<PassengerDraft> = listOf(PassengerDraft())
) {
    fun validate(remainingCapacity: Long, today: LocalDate = LocalDate.now()): String? {
        if (contactName.trim().isEmpty() || contactName.trim().length > 150)
            return "Tên người liên hệ phải có từ 1 đến 150 ký tự"
        if (contactPhone.trim().isEmpty() || contactPhone.trim().length > 30)
            return "Số điện thoại liên hệ phải có từ 1 đến 30 ký tự"
        if (passengers.isEmpty() || passengers.size > 20 || passengers.size > remainingCapacity)
            return "Số hành khách vượt số chỗ còn lại hoặc giới hạn 20 người"
        passengers.forEachIndexed { index, passenger ->
            val prefix = "Hành khách ${index + 1}: "
            if (passenger.fullName.trim().isEmpty() || passenger.fullName.trim().length > 150)
                return prefix + "họ tên phải có từ 1 đến 150 ký tự"
            val birthDate = runCatching { LocalDate.parse(passenger.dateOfBirth) }.getOrNull()
            if (birthDate == null || !birthDate.isBefore(today))
                return prefix + "ngày sinh phải hợp lệ và trước hôm nay"
            if (passenger.gender !in listOf("MALE", "FEMALE", "OTHER"))
                return prefix + "vui lòng chọn giới tính"
            if (passenger.phone.trim().length > 30) return prefix + "số điện thoại quá dài"
            val email = passenger.email.trim()
            if (email.length > 255 || (email.isNotEmpty() && !email.matches(Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))))
                return prefix + "email không hợp lệ"
        }
        return null
    }

    fun toRequest(voyageId: String, roomId: String): CreateBookingRequest {
        UUID.fromString(voyageId)
        UUID.fromString(roomId)
        return CreateBookingRequest(
            voyageId, contactName.trim(), contactPhone.trim(),
            passengers.map {
                CreatePassengerRequest(
                    fullName = it.fullName.trim(), dateOfBirth = it.dateOfBirth,
                    gender = it.gender, cabinId = roomId,
                    phoneNumber = it.phone.trim().ifEmpty { null },
                    email = it.email.trim().ifEmpty { null }
                )
            }
        )
    }
}
