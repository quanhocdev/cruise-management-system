package com.project.cruise.android.data.repository

import com.project.cruise.android.data.network.ApiService

class PassengerCatalogRepository(private val api: ApiService) {
    suspend fun getOpenTours() = api.getOpenTours()
    suspend fun getTourDetail(tourId: String) = api.getTourDetail(tourId)
    suspend fun getDepartures(tourId: String) = api.getDepartures(tourId)
    suspend fun getAvailableRooms(voyageId: String) = api.getAvailableRooms(voyageId)
}
