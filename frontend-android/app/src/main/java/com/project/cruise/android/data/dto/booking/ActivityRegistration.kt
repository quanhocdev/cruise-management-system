package com.project.cruise.android.data.dto.booking

import java.math.BigDecimal

data class RegistrationGuest(val id: Long,val name: String,val status: String)
data class RegistrationTarget(val type: String,val id: String,val tourId: String,val name: String,
    val startTime: String?,val endTime: String?,val capacity: Int?,val price: BigDecimal?,val status: String?)
data class RegistrationChoice(val target: RegistrationTarget,val remainingSeats: Long)
data class RegistrationCatalog(val bookingId: Long,val bookingCode: String,val tourId: String,
    val passengers: List<RegistrationGuest>,val activities: List<RegistrationChoice>)
data class ActivityRegistration(val id: Long,val bookingPassengerId: Long,val passengerName: String,val bookingCode: String,
    val tourId: String,val targetType: String,val targetId: String,val name: String,val startTime: String,val endTime: String,
    val listedPrice: BigDecimal,val status: String,val usageId: Long?,val canCancel: Boolean)
data class RegisterActivityRequest(val bookingPassengerId: Long,val targetType: String,val targetId: String,val clientRequestId: String?=null)
data class RegistrationCommand(val clientRequestId: String?=null)
data class RegistrationAttendance(val reason: String,val clientRequestId: String?=null)
data class ActivityNotice(val id: String,val title: String,val message: String,val createdAt: String,
    val priority: String = "NORMAL",val bookingId: Long? = null,val registrationId: Long? = null,val readAt: String? = null)
data class ActivityUnreadCount(val unreadCount: Long)
data class ActivityReadResult(val id: String,val readAt: String)
