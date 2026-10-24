package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.network.ApiService
import com.project.cruise.android.viewmodel.passenger.BookingDraft
import okhttp3.MultipartBody

class PassengerBookingRepository(private val api: ApiService) {
    suspend fun getPackage(tourId: String, packageId: String): PublicTourDetailResponse.TourPackageRecord? =
        api.getPublicTourDetail(tourId).packages.orEmpty().firstOrNull { it.id == packageId }

    suspend fun create(tourId: String, packageId: String, draft: BookingDraft): BookingResponse {
        val parts = buildList {
            add(textPart("tourId", tourId))
            add(textPart("tourPackageId", packageId))
            add(textPart("numberOfRooms", draft.numberOfRooms.toString()))
            add(textPart("primaryContactName", draft.contactName.trim()))
            add(textPart("primaryContactPhone", draft.contactPhone.trim()))
            draft.passengers.forEachIndexed { index, passenger ->
                val prefix = "passengers[$index]"
                add(textPart("$prefix.fullName", passenger.fullName.trim()))
                add(textPart("$prefix.dateOfBirth", passenger.dateOfBirth))
                add(textPart("$prefix.gender", passenger.gender))
                add(textPart("$prefix.idCardType", passenger.idCardType))
                add(textPart("$prefix.identificationNumber", passenger.identificationNumber.trim()))
                passenger.phone.trim().takeIf { it.isNotEmpty() }?.let { add(textPart("$prefix.phoneNumber", it)) }
                passenger.email.trim().takeIf { it.isNotEmpty() }?.let { add(textPart("$prefix.email", it)) }
                passenger.documentNote.trim().takeIf { it.isNotEmpty() }?.let { add(textPart("$prefix.documentNote", it)) }
            }
        }
        return api.createPassengerBooking(parts)
    }

    private fun textPart(name: String, value: String): MultipartBody.Part =
        MultipartBody.Part.createFormData(name, value)
}
