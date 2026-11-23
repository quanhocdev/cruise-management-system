package com.project.cruise.android.data.dto.booking

import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

data class PassengerRequestDto(
    val fullName: String,
    val dateOfBirth: String, // ISO yyyy-MM-dd
    val gender: String, // MALE, FEMALE, OTHER
    val phoneNumber: String? = null,
    val email: String? = null,
    val idCardType: String, // CCCD, PASSPORT, BIRTH_CERTIFICATE, OTHER
    val identificationNumber: String,
    val documentNote: String? = null,
    val idCardImageUri: Uri? = null,
)

data class CreateBookingRequestDto(
    val tourId: String,
    val tourPackageId: String,
    val numberOfRooms: Int,
    val primaryContactName: String,
    val primaryContactPhone: String,
    val passengers: List<PassengerRequestDto>,
) {
    /**
     * Chuyển DTO sang danh sách MultipartBody.Part khớp @ModelAttribute CreateBookingRequest
     * của Spring Boot và FormData của FE Web.
     */
    fun toMultipartParts(
        imageFileResolver: (Uri) -> Pair<File, String>? = { null }
    ): List<MultipartBody.Part> = buildList {
        add(textPart("tourId", tourId))
        add(textPart("tourPackageId", tourPackageId))
        add(textPart("numberOfRooms", numberOfRooms.toString()))
        add(textPart("primaryContactName", primaryContactName.trim()))
        add(textPart("primaryContactPhone", primaryContactPhone.trim()))

        passengers.forEachIndexed { index, p ->
            val prefix = "passengers[$index]"
            add(textPart("$prefix.fullName", p.fullName.trim()))
            add(textPart("$prefix.dateOfBirth", p.dateOfBirth.trim()))
            add(textPart("$prefix.gender", p.gender.trim()))
            add(textPart("$prefix.idCardType", p.idCardType.trim()))
            add(textPart("$prefix.identificationNumber", p.identificationNumber.trim()))

            p.phoneNumber?.trim()?.takeIf { it.isNotEmpty() }?.let {
                add(textPart("$prefix.phoneNumber", it))
            }
            p.email?.trim()?.takeIf { it.isNotEmpty() }?.let {
                add(textPart("$prefix.email", it.lowercase()))
            }
            p.documentNote?.trim()?.takeIf { it.isNotEmpty() }?.let {
                add(textPart("$prefix.documentNote", it))
            }

            p.idCardImageUri?.let { uri ->
                imageFileResolver(uri)?.let { (file, mimeType) ->
                    val mediaType = mimeType.toMediaTypeOrNull() ?: "image/*".toMediaTypeOrNull()
                    val body: RequestBody = file.asRequestBody(mediaType)
                    add(MultipartBody.Part.createFormData("$prefix.idCardImage", file.name, body))
                }
            }
        }
    }

    private fun textPart(name: String, value: String): MultipartBody.Part =
        MultipartBody.Part.createFormData(
            name,
            null,
            value.toRequestBody("text/plain".toMediaTypeOrNull())
        )
}