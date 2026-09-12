package com.project.cruise.android.data.dto.tour

import java.math.BigDecimal

data class PublicTourSummaryResponse(
    val id: String,
    val code: String?,
    val name: String?,
    val description: String?,
    val startDate: String?,
    val endDate: String?,
    val cruiseName: String?,
    val cruiseImageUrl: String?,
    val statusBooking: TourBookingStatus?,
    val statusTrip: TourStatusTrip?,
    val bookingStart: String?,
    val bookingEnd: String?,
    val startingPrice: BigDecimal?
)

data class PublicTourDetailResponse(
    val id: String,
    val code: String?,
    val name: String?,
    val description: String?,
    val startDate: String?,
    val endDate: String?,
    val statusBooking: TourBookingStatus?,
    val bookingStart: String?,
    val bookingEnd: String?,
    val cruise: CruiseDetailRecord?,
    val schedules: List<ScheduleDetailRecord>?,
    val packages: List<TourPackageRecord>?,
    val onboardActivities: List<OnboardActivityRecord>?,
    val products: List<ProductRecord>?,
    val services: List<ServiceRecord>?
) {
    data class CruiseDetailRecord(
        val id: String,
        val name: String?,
        val code: String?,
        val description: String?,
        val maxPassengers: Int?,
        val imageUrl: String?
    )

    data class ScheduleDetailRecord(
        val id: String,
        val name: String?,
        val description: String?,
        val dayNumber: Int?,
        val realDay: String?,
        val stops: List<ScheduleStopRecord>?
    )

    data class ScheduleStopRecord(
        val id: String,
        val stopOrder: Int?,
        val arriveAt: String?,
        val leaveAt: String?,
        val portName: String?,
        val portCity: String?,
        val portCountry: String?,
        val portDescription: String?,
        val visitActivity: VisitActivityRecord?
    )

    data class VisitActivityRecord(
        val id: String,
        val visitName: String?,
        val visitDescription: String?,
        val startTime: String?,
        val endTime: String?,
        val price: BigDecimal?,
        val maxPassengers: Int?
    )

    data class TourPackageRecord(
        val id: String,
        val name: String?,
        val description: String?,
        val price: BigDecimal?,
        val maxPassengers: Int?,
        val benefits: List<PackageBenefitRecord>?
    )

    data class PackageBenefitRecord(
        val id: String,
        val type: String?,
        val referenceId: String?,
        val quantity: Int?,
        val discountPercent: BigDecimal?
    )

    data class OnboardActivityRecord(
        val id: String,
        val activityName: String?,
        val activityDescription: String?,
        val startTime: String?,
        val endTime: String?,
        val maxPassengers: Int?,
        val price: BigDecimal?,
        val imageUrl: String?
    )

    data class ProductRecord(
        val id: String,
        val productName: String?,
        val productDescription: String?,
        val price: BigDecimal?,
        val quantity: Int?,
        val imageUrl: String?
    )

    data class ServiceRecord(
        val id: String,
        val serviceName: String?,
        val serviceDescription: String?,
        val price: BigDecimal?,
        val maxPassengers: Int?,
        val durationMinutes: Int?,
        val imageUrl: String?
    )
}