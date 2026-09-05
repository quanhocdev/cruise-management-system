package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.passenger.CreateBookingRequest
import com.project.cruise.android.data.dto.passenger.CreateVnPayPaymentRequest
import com.project.cruise.android.data.network.ApiService

class PassengerBookingRepository(private val api: ApiService) {
    suspend fun getRoom(voyageId: String, roomId: String) =
        api.getAvailableRooms(voyageId).firstOrNull { it.roomId == roomId }

    suspend fun create(request: CreateBookingRequest) = api.createPassengerBooking(request)
    suspend fun getMine() = api.getMyBookings()
    suspend fun get(id: Long) = api.getPassengerBooking(id)
    suspend fun getQr(id: Long) = api.getBookingQr(id).bytes()
    suspend fun createVnPayPayment(booking: com.project.cruise.android.data.dto.passenger.PassengerBookingResponse) =
        api.createVnPayPayment(CreateVnPayPaymentRequest(booking.id, amount = booking.totalAmount))
}
