package com.project.cruise.android.data.dto.booking

data class DepartureGuest(val bookingPassengerId: Long,val name: String,val status: String,val departedAt: String?)
data class DepartureManifest(val bookingId: Long,val settled: Boolean,val passengers: List<DepartureGuest>)
data class DepartureRequest(val roomReturned: Boolean,val wristbandReturned: Boolean,val reason: String,val clientRequestId: String?=null)
data class PassengerDeparture(val bookingPassengerId: Long,val status: String,val departedAt: String)
