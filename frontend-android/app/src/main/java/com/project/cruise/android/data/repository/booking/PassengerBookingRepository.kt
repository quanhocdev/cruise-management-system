package com.project.cruise.android.data.repository.booking

import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.data.dto.tour.PublicTourDetailResponse
import com.project.cruise.android.data.network.api.TourApiService
import com.project.cruise.android.viewmodel.passenger.BookingDraft

class PassengerBookingRepository(
    private val tourApi: TourApiService,
    private val bookingRepository: BookingRepository,
) {
    suspend fun getPackage(
        tourId: String,
        packageId: String
    ): PublicTourDetailResponse.TourPackageRecord? =
        tourApi.getPublicTourDetail(tourId).packages.orEmpty().firstOrNull { it.id == packageId }

    suspend fun create(
        tourId: String,
        packageId: String,
        draft: BookingDraft
    ): BookingResponse {
        val request = draft.toCreateRequest(tourId = tourId, tourPackageId = packageId)
        return bookingRepository.createBooking(request)
    }
}