package com.project.cruise.android.navigation

import com.project.cruise.android.ui.screens.pos.PosRole

object Routes {

    // =================================================
    // AUTH
    // =================================================
    const val SPLASH = "splash"
    const val GUEST = "guest"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP = "otp/{userId}"

    fun otp(userId: Long) = "otp/$userId"

    // =================================================
    // PASSENGER
    // =================================================
    const val PASSENGER_PROFILE = "passenger_profile"
    const val PASSENGER_DASHBOARD = "passenger_dashboard"
    const val PASSENGER_NOTIFICATIONS = "passenger_notifications"
    const val PASSENGER_TOURS = "passenger_tours"
    const val PASSENGER_TOUR_DETAIL = "passenger_tours/{tourId}"
    const val PASSENGER_ROOMS = "passenger_rooms/{voyageId}"
    const val PASSENGER_CREATE_BOOKING = "passenger_booking/{tourId}/{packageId}"
    const val PASSENGER_BOOKINGS = "passenger_bookings"
    const val PASSENGER_BOOKING_DETAIL = "passenger_bookings/{bookingId}"
    const val PASSENGER_PAYMENT_RESULT =
        "passenger_payment_result?paymentId={paymentId}&status={status}"

    fun passengerTourDetail(tourId: String) = "passenger_tours/$tourId"
    fun passengerCreateBooking(tourId: String, packageId: Any) =
        "passenger_booking/$tourId/$packageId"
    fun passengerBookingDetail(bookingId: Any) = "passenger_bookings/$bookingId"

    // =================================================
    // POS
    // =================================================
    const val POS_LOGIN = "pos_login"
    const val POS_DASHBOARD = "pos_dashboard/{role}"
    const val POS_QR_SCAN = "pos_qr_scan/{role}"
    const val POS_NFC_SCAN = "pos_nfc_scan/{role}"
    const val POS_MANUAL_ENTRY = "pos_manual_entry/{role}"
    const val POS_HISTORY = "pos_history/{role}"
    const val POS_IDENTITY = "pos_identity/{role}/{localId}"

    fun posDashboard(role: PosRole) = "pos_dashboard/${role.apiRole}"
    fun posQrScan(role: PosRole) = "pos_qr_scan/${role.apiRole}"
    fun posNfcScan(role: PosRole) = "pos_nfc_scan/${role.apiRole}"
    fun posManualEntry(role: PosRole) = "pos_manual_entry/${role.apiRole}"
    fun posHistory(role: PosRole) = "pos_history/${role.apiRole}"
    fun posIdentity(role: PosRole, localId: String) = "pos_identity/${role.apiRole}/$localId"

    // ----- POS usage (convenience / onboard / shore dùng chung cấu trúc argument) -----
    private const val CONVENIENCE_PREFIX = "pos_convenience_usage"
    private const val ONBOARD_PREFIX = "pos_onboard_usage"
    private const val SHORE_PREFIX = "pos_shore_usage"

    const val POS_CONVENIENCE_USAGE = "$CONVENIENCE_PREFIX/$USAGE_PATH"
    const val POS_ONBOARD_USAGE = "$ONBOARD_PREFIX/$USAGE_PATH"
    const val POS_SHORE_USAGE = "$SHORE_PREFIX/$USAGE_PATH"

    fun posConvenienceUsage(
        bookingPassengerId: Long,
        passengerName: String?,
        bookingId: Long,
        tourId: String,
        tourPackageId: String,
        nfcCardUid: String
    ) = buildUsageRoute(
        CONVENIENCE_PREFIX, bookingPassengerId, passengerName,
        bookingId, tourId, tourPackageId, nfcCardUid
    )

    fun posOnboardUsage(
        bookingPassengerId: Long,
        passengerName: String?,
        bookingId: Long,
        tourId: String,
        tourPackageId: String,
        nfcCardUid: String
    ) = buildUsageRoute(
        ONBOARD_PREFIX, bookingPassengerId, passengerName,
        bookingId, tourId, tourPackageId, nfcCardUid
    )

    fun posShoreUsage(
        bookingPassengerId: Long,
        passengerName: String?,
        bookingId: Long,
        tourId: String,
        tourPackageId: String,
        nfcCardUid: String
    ) = buildUsageRoute(
        SHORE_PREFIX, bookingPassengerId, passengerName,
        bookingId, tourId, tourPackageId, nfcCardUid
    )
}