package com.project.cruise.android.viewmodel.passenger

import java.time.LocalDate

data class PassengerDraft(
    val fullName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val phone: String = "",
    val email: String = "",
    val idCardType: String = "CCCD",
    val identificationNumber: String = "",
    val documentNote: String = ""
)

data class BookingDraft(
    val contactName: String = "",
    val contactPhone: String = "",
    val numberOfRooms: Int = 1,
    val passengers: List<PassengerDraft> = listOf(PassengerDraft())
) {
    fun validate(maxPassengersPerRoom: Int?, today: LocalDate = LocalDate.now()): String? {
        if (contactName.trim().isEmpty() || contactName.trim().length > 150) {
            return "Tên người liên hệ phải có từ 1 đến 150 ký tự"
        }
        if (contactPhone.trim().isEmpty() || contactPhone.trim().length > 30) {
            return "Số điện thoại liên hệ phải có từ 1 đến 30 ký tự"
        }
        if (numberOfRooms !in 1..20) return "Số phòng phải từ 1 đến 20"
        val capacity = (maxPassengersPerRoom ?: 2).coerceAtLeast(1) * numberOfRooms
        if (passengers.isEmpty() || passengers.size > 20 || passengers.size > capacity) {
            return "Số hành khách vượt sức chứa $capacity người của $numberOfRooms phòng"
        }
        passengers.forEachIndexed { index, passenger ->
            val prefix = "Hành khách ${index + 1}: "
            if (passenger.fullName.trim().isEmpty() || passenger.fullName.trim().length > 150) {
                return prefix + "họ tên không hợp lệ"
            }
            val birthDate = runCatching { LocalDate.parse(passenger.dateOfBirth) }.getOrNull()
            if (birthDate == null || !birthDate.isBefore(today)) {
                return prefix + "ngày sinh phải trước hôm nay"
            }
            if (passenger.gender !in listOf("MALE", "FEMALE", "OTHER")) {
                return prefix + "vui lòng chọn giới tính"
            }
            if (passenger.idCardType !in listOf("CCCD", "PASSPORT", "BIRTH_CERTIFICATE", "OTHER")) {
                return prefix + "loại giấy tờ không hợp lệ"
            }
            if (passenger.identificationNumber.trim().isEmpty() || passenger.identificationNumber.trim().length > 50) {
                return prefix + "số giấy tờ không hợp lệ"
            }
            if (passenger.phone.trim().length > 30) return prefix + "số điện thoại quá dài"
            val email = passenger.email.trim()
            if (email.length > 255 || (email.isNotEmpty() && !email.matches(Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")))) {
                return prefix + "email không hợp lệ"
            }
            if (passenger.documentNote.trim().length > 255) return prefix + "ghi chú giấy tờ quá dài"
        }
        return null
    }
}
