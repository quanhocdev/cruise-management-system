package com.project.cruise.android.data.repository

import com.project.cruise.android.data.dto.passenger.CreateBookingRequest
import com.project.cruise.android.data.network.ApiService

class PassengerBookingRepository(private val api: ApiService) {
    suspend fun getRoom(voyageId: String, roomId: String) =
        api.getAvailableRooms(voyageId).firstOrNull { it.roomId == roomId }

    suspend fun create(request: CreateBookingRequest) = api.createPassengerBooking(request)
}
