package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.data.network.ApiService

class BookingRepository(
    private val apiService: ApiService
) {
    suspend fun getPayment(id: Long) = apiService.getBookingPayment(id)

    suspend fun getQr(id: Long): ByteArray = apiService.getBookingQr(id).use { it.bytes() }

    suspend fun getMyBookings(): List<BookingResponse> {
        return apiService.getMyBookings()
    }

    suspend fun getBookingById(id: Long, privileged: Boolean = false): BookingResponse {
        return apiService.getBookingById(id, privileged)
    }
}