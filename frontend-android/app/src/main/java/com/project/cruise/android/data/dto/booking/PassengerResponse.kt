package com.project.cruise.android.data.dto.booking

data class PassengerResponse(
    val id: Long?,
    val fullName: String?,
    val dateOfBirth: String?,
    val gender: String?,
    val phoneNumber: String?,
    val email: String?,
    val idCardType: DocumentType?,
    val identificationNumber: String?,
    val documentNote: String?,
    val idCardImageUrl: String?,
    val createdAt: String?,
    val updatedAt: String?
)