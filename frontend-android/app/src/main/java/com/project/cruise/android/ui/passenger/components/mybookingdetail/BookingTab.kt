package com.project.cruise.android.ui.passenger.components.mybookingdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.data.dto.booking.BookingStatus
import com.project.cruise.android.data.dto.passenger.tripdetail.BookingPassengerItemDto
import com.project.cruise.android.data.dto.passenger.tripdetail.PassengerBookingDetailDto
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.screens.passenger.BookingTicketPanel
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.BookingAccessState
import com.project.cruise.android.viewmodel.passenger.PassengerTripDetailUiState

/**
 * Tab "Đặt chỗ": dữ liệu lấy từ API giám sát (trip-detail, passengers); chỉ dùng [booking] cũ làm dự phòng
 * và cho QR vé. Liên hệ và từng hành khách được thu gọn.
 */
fun LazyListScope.bookingTab(
    booking: BookingResponse,
    access: BookingAccessState,
    trip: PassengerTripDetailUiState,
    onRefreshBooking: () -> Unit,
    onReloadTab: () -> Unit,
    onActivities: (() -> Unit)?,
    onSpending: (() -> Unit)?,
    onBill: (() -> Unit)?,
    onFeedback: (() -> Unit)?,
) {
    item(key = "b-summary") { TripBookingSummary(trip.booking, booking, trip.matchedPackage?.name) }
    item(key = "b-qr") { QrToggle(booking.status, access, onRefreshBooking) }
    item(key = "b-actions") {
        BookingActionButtons(
            onActivities = onActivities,
            onSpending = onSpending,
            onBill = onBill,
            onFeedback = onFeedback,
        )
    }
    item(key = "b-contact") {
        val b = trip.booking
        val name = b?.primaryContactName ?: booking.primaryContactName
        ExpandableSection("Người liên hệ", subtitle = name) {
            DetailRow(Icons.Filled.Person, "Họ và tên", name)
            DetailRow(Icons.Filled.Phone, "Số điện thoại", b?.primaryContactPhone ?: booking.primaryContactPhone)
            DetailRow(Icons.Filled.Email, "Email", b?.primaryContactEmail ?: booking.primaryContactEmail)
        }
    }

    val count = trip.passengers.size.takeIf { it > 0 } ?: (trip.booking?.numberPassengers ?: booking.numberPassengers)
    item(key = "b-pax-title") {
        PassengerSection(
            "Hành khách",
            count?.let { "$it khách · bấm vào từng người để xem chi tiết" } ?: "Thông tin và trạng thái check-in.",
        )
    }
    if (tripLoadGate("b-pax", trip.passengersLoading, trip.passengersError, trip.passengers.isEmpty(), onReloadTab)) return
    if (trip.passengers.isEmpty()) {
        item(key = "b-pax-empty") {
            PassengerEmpty("Chưa có thông tin hành khách", "Danh sách sẽ hiển thị khi được hệ thống cập nhật.")
        }
        return
    }
    itemsIndexed(trip.passengers, key = { index, p -> "b-pax-${p.id ?: index}" }) { index, p ->
        PassengerExpandCard(index, p)
    }
}

@Composable
private fun TripBookingSummary(trip: PassengerBookingDetailDto?, fallback: BookingResponse, packageName: String?) {
    val total = trip?.totalAmount ?: fallback.totalAmount?.toDouble()
    val created = trip?.createdAt ?: fallback.createdAt
    val pax = trip?.numberPassengers ?: fallback.numberPassengers
    val pkg = trip?.packageName ?: packageName
    val payment = trip?.paymentStatus

    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Tổng giá trị booking", style = MaterialTheme.typography.labelMedium, color = OceanMint)
                    Text(
                        tripMoney(total),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = OceanGold,
                    )
                }
                payment?.takeIf { it.isNotBlank() }?.let { PassengerPill(paymentLabel(it), paymentTone(it)) }
            }
            HorizontalDivider(color = Color.White.copy(alpha = .2f))
            val tiles = listOfNotNull(
                "Ngày đặt" to passengerDate(created),
                pax?.let { "Hành khách" to "$it khách" },
                trip?.numberOfRooms?.let { "Số phòng" to "$it phòng" },
                pkg?.takeIf { it.isNotBlank() }?.let { "Gói tour" to it },
            )
            tiles.chunked(2).forEach { rowItems ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowItems.forEach { (label, value) -> SummaryTile(label, value, Modifier.weight(1f)) }
                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SummaryTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(16.dp), color = Color.White.copy(alpha = .13f)) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = OceanMint)
            Text(
                value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 2,
            )
        }
    }
}

private fun paymentLabel(status: String): String =
    when (status.uppercase()) {
        "PAID", "SUCCESS", "COMPLETED" -> "Đã thanh toán"
        "PENDING", "UNPAID", "WAITING" -> "Chờ thanh toán"
        "REFUNDED" -> "Đã hoàn tiền"
        "FAILED", "EXPIRED" -> "Thanh toán lỗi"
        else -> status
    }

private fun paymentTone(status: String): PillTone =
    when (status.uppercase()) {
        "PAID", "SUCCESS", "COMPLETED" -> PillTone.POSITIVE
        "PENDING", "UNPAID", "WAITING" -> PillTone.WARNING
        "FAILED", "EXPIRED" -> PillTone.DANGER
        else -> PillTone.NEUTRAL
    }

@Composable
private fun QrToggle(status: BookingStatus?, access: BookingAccessState, onRefresh: () -> Unit) {
    var show by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick = { show = !show },
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.5.dp, OceanGold),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = OceanGoldSoft, contentColor = OceanNavy),
        ) {
            Text(if (show) "Ẩn mã QR check-in" else "Hiện mã QR check-in", fontWeight = FontWeight.Bold)
        }
        AnimatedVisibility(show) { BookingTicketPanel(status, access, onRefresh) }
    }
}

@Composable
private fun PassengerExpandCard(index: Int, p: BookingPassengerItemDto) {
    val profile = p.passenger
    ExpandableSection(
        title = profile?.fullName?.takeIf { it.isNotBlank() } ?: "Hành khách ${index + 1}",
        subtitle = listOfNotNull("Hành khách ${index + 1}", genderText(profile?.gender)).joinToString(" · "),
        trailing = statusText(p.status),
    ) {
        profile?.dateOfBirth?.takeIf { it.isNotBlank() }?.let { DetailRow(Icons.Filled.DateRange, "Ngày sinh", passengerDate(it)) }
        profile?.identificationNumber?.takeIf { it.isNotBlank() }?.let {
            DetailRow(Icons.Filled.AccountBox, profile.idCardType?.takeIf { t -> t.isNotBlank() }?.let { t -> "Giấy tờ ($t)" } ?: "Giấy tờ tùy thân", it)
        }
        profile?.phoneNumber?.takeIf { it.isNotBlank() }?.let { DetailRow(Icons.Filled.Phone, "Số điện thoại", it) }
        profile?.email?.takeIf { it.isNotBlank() }?.let { DetailRow(Icons.Filled.Email, "Email", it) }
        p.nfcCardUid?.takeIf { it.isNotBlank() }?.let { DetailRow(Icons.Filled.CheckCircle, "Thẻ NFC", it) }
        p.checkedInAt?.takeIf { it.isNotBlank() }?.let {
            DetailRow(Icons.Filled.DateRange, "Check-in lúc", "${passengerDate(it)} ${passengerTime(it)}")
        }
        p.checkedOutAt?.takeIf { it.isNotBlank() }?.let {
            DetailRow(Icons.Filled.DateRange, "Check-out lúc", "${passengerDate(it)} ${passengerTime(it)}")
        }
        profile?.documentNote?.takeIf { it.isNotBlank() }?.let { DetailRow(Icons.Filled.Info, "Ghi chú giấy tờ", it) }
    }
}

private fun statusText(status: String?): String =
    when (status?.uppercase()) {
        "CHECKED_IN", "CHECKEDIN" -> "Đã check-in"
        "CHECKED_OUT" -> "Đã check-out"
        "PENDING", "NOT_CHECKED_IN" -> "Chưa check-in"
        null -> "Chưa có trạng thái"
        else -> status
    }

private fun genderText(gender: String?): String? =
    when (gender?.uppercase()) {
        "MALE" -> "Nam"
        "FEMALE" -> "Nữ"
        "OTHER" -> "Khác"
        else -> gender?.takeIf { it.isNotBlank() }
    }