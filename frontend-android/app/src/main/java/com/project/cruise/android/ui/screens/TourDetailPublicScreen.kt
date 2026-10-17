package com.project.cruise.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.tour.*
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.tour.*

@Composable
fun TourDetailPublicScreen(
    tourId: String,
    viewModel: TourViewModel,
    isLoggedIn: Boolean,
    onBookClick: (String) -> Unit,
    onLoginRequired: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.tourDetailState.collectAsState()
    LaunchedEffect(tourId) { viewModel.fetchTourDetail(tourId) }
    TourDetailContent(
        state,
        isLoggedIn,
        onBookClick,
        onLoginRequired,
        onBack,
        { viewModel.fetchTourDetail(tourId) },
    )
}

@Composable
fun TourDetailContent(
    state: TourDetailState,
    isLoggedIn: Boolean,
    onBookClick: (String) -> Unit,
    onLoginRequired: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    PassengerPage("Chi tiết hành trình", onBack = onBack) {
        when (state) {
            TourDetailState.Idle,
            TourDetailState.Loading -> item { PassengerLoading() }
            is TourDetailState.Error -> item { PassengerError(state.message, onRetry) }
            is TourDetailState.Success -> {
                val tour = state.tour
                item {
                    CruiseVisual(tour.cruise?.imageUrl)
                    Spacer(Modifier.height(16.dp))
                    PassengerPill(
                        tourSaleLabel(tour.statusBooking),
                        tour.statusBooking == TourBookingStatus.OPEN,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        tour.name ?: "Hành trình ${tour.code.orEmpty()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = OceanNavy,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        tour.description?.takeIf { it.isNotBlank() }
                            ?: "Mô tả hành trình đang được cập nhật.",
                        color = OceanSlate,
                    )
                }
                item {
                    PassengerCard {
                        PassengerInfo("Du thuyền", tour.cruise?.name)
                        HorizontalDivider(color = OceanLine)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(Modifier.weight(1f)) {
                                PassengerInfo("Khởi hành", passengerDate(tour.startDate))
                            }
                            Column(Modifier.weight(1f)) {
                                PassengerInfo("Kết thúc", passengerDate(tour.endDate))
                            }
                        }
                        tour.cruise?.maxPassengers?.let {
                            Text(
                                "Sức chứa du thuyền · $it khách",
                                color = OceanSlate,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
                item {
                    PassengerSection(
                        "Chọn gói hành trình",
                        "Xem giá, sức chứa và quyền lợi của từng gói.",
                    )
                }
                if (tour.packages.isNullOrEmpty())
                    item {
                        PassengerEmpty(
                            "Chưa có gói đặt chỗ",
                            "Gói tour sẽ được hiển thị khi được công bố.",
                        )
                    }
                items(tour.packages.orEmpty(), key = { "package-${it.id}" }) { pkg ->
                    PassengerCard {
                        Text(
                            pkg.name ?: "Gói hành trình",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OceanNavy,
                        )
                        pkg.description
                            ?.takeIf { it.isNotBlank() }
                            ?.let { Text(it, color = OceanSlate) }
                        pkg.maxPassengers?.let { PassengerPill("Tối đa $it khách / phòng") }
                        Text(
                            passengerMoney(pkg.price),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = OceanTeal,
                        )
                        Text(
                            "Giá mỗi phòng",
                            style = MaterialTheme.typography.labelSmall,
                            color = OceanSlate,
                        )
                        if (!pkg.benefits.isNullOrEmpty()) {
                            HorizontalDivider(color = OceanLine)
                            Text(
                                "Quyền lợi đi kèm",
                                fontWeight = FontWeight.SemiBold,
                                color = OceanNavy,
                            )
                            pkg.benefits.forEach { benefit ->
                                Text(
                                    benefitDescription(benefit, tour),
                                    color = OceanSlate,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                        OceanPrimaryButton(
                            if (isLoggedIn) "Chọn gói này" else "Đăng nhập để đặt tour",
                            { if (isLoggedIn) onBookClick(pkg.id) else onLoginRequired() },
                            enabled = tour.statusBooking == TourBookingStatus.OPEN,
                        )
                        if (tour.statusBooking != TourBookingStatus.OPEN)
                            Text(
                                "Hành trình hiện chưa nhận đặt chỗ.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OceanSlate,
                            )
                    }
                }
                if (!tour.schedules.isNullOrEmpty())
                    item {
                        PassengerSection(
                            "Lịch trình",
                            "Các điểm dừng và trải nghiệm trong chuyến đi.",
                        )
                    }
                items(
                    tour.schedules.orEmpty().sortedBy { it.dayNumber },
                    key = { "schedule-${it.id}" },
                ) { schedule ->
                    PassengerCard {
                        PassengerPill(
                            "Ngày ${schedule.dayNumber ?: "—"} · ${passengerDate(schedule.realDay)}"
                        )
                        Text(
                            schedule.name ?: "Lịch trình",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OceanNavy,
                        )
                        schedule.description
                            ?.takeIf { it.isNotBlank() }
                            ?.let { Text(it, color = OceanSlate) }
                        schedule.stops
                            .orEmpty()
                            .sortedBy { it.stopOrder }
                            .forEach { stop ->
                                HorizontalDivider(color = OceanLine)
                                PassengerInfo(
                                    "Điểm dừng ${stop.stopOrder ?: ""}",
                                    listOfNotNull(stop.portName, stop.portCity).joinToString(" · "),
                                )
                                if (stop.arriveAt != null || stop.leaveAt != null)
                                    Text(
                                        "Đến ${passengerTime(stop.arriveAt)} · Rời ${passengerTime(stop.leaveAt)}",
                                        color = OceanSlate,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                stop.visitActivity?.let { visit ->
                                    Text(
                                        visit.visitName ?: "Tham quan",
                                        fontWeight = FontWeight.SemiBold,
                                        color = OceanTeal,
                                    )
                                    visit.visitDescription
                                        ?.takeIf { it.isNotBlank() }
                                        ?.let { Text(it, color = OceanSlate) }
                                    visit.price?.let {
                                        Text(
                                            "Giá tham quan: ${passengerMoney(it)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OceanSlate,
                                        )
                                    }
                                }
                            }
                    }
                }
                if (!tour.onboardActivities.isNullOrEmpty())
                    item { PassengerSection("Trải nghiệm trên tàu") }
                items(tour.onboardActivities.orEmpty(), key = { "activity-${it.id}" }) { activity ->
                    TourOffering(
                        activity.activityName,
                        activity.activityDescription,
                        activity.price,
                        activity.imageUrl,
                        listOfNotNull(activity.startTime, activity.endTime)
                            .takeIf { it.isNotEmpty() }
                            ?.joinToString(" – ") { "${passengerDate(it)} · ${passengerTime(it)}" },
                    )
                }
                if (!tour.services.isNullOrEmpty())
                    item { PassengerSection("Dịch vụ dành cho bạn") }
                items(tour.services.orEmpty(), key = { "service-${it.id}" }) { service ->
                    TourOffering(
                        service.serviceName,
                        service.serviceDescription,
                        service.price,
                        service.imageUrl,
                        service.durationMinutes?.let { "$it phút" },
                    )
                }
                if (!tour.products.isNullOrEmpty()) item { PassengerSection("Sản phẩm trên tàu") }
                items(tour.products.orEmpty(), key = { "product-${it.id}" }) { product ->
                    TourOffering(
                        product.productName,
                        product.productDescription,
                        product.price,
                        product.imageUrl,
                    )
                }
            }
        }
    }
}

private fun benefitDescription(
    benefit: PublicTourDetailResponse.PackageBenefitRecord,
    tour: PublicTourDetailResponse,
): String {
    val name =
        when (benefit.type) {
            "SERVICE" ->
                tour.services?.find { it.id == benefit.referenceId }?.serviceName ?: "Dịch vụ"
            "PRODUCT" ->
                tour.products?.find { it.id == benefit.referenceId }?.productName ?: "Sản phẩm"
            "ACTIVITY_CRUISE" ->
                tour.onboardActivities?.find { it.id == benefit.referenceId }?.activityName
                    ?: "Hoạt động trên tàu"
            "ACTIVITY_VISIT" ->
                tour.schedules
                    .orEmpty()
                    .flatMap { it.stops.orEmpty() }
                    .mapNotNull { it.visitActivity }
                    .find { it.id == benefit.referenceId }
                    ?.visitName ?: "Tham quan trên bờ"
            else -> "Quyền lợi gói tour"
        }
    return listOfNotNull(
            name,
            benefit.quantity?.let { "Số lượng: $it" },
            benefit.discountPercent
                ?.takeIf { it.signum() > 0 }
                ?.let { "Giảm ${it.stripTrailingZeros().toPlainString()}%" },
        )
        .joinToString(" · ")
}

@Composable
private fun TourOffering(
    name: String?,
    description: String?,
    price: java.math.BigDecimal?,
    image: String?,
    detail: String? = null,
) {
    PassengerCard {
        if (!image.isNullOrBlank()) CruiseVisual(image)
        Text(
            name ?: "Đang cập nhật",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        description?.takeIf { it.isNotBlank() }?.let { Text(it, color = OceanSlate) }
        detail?.let { PassengerPill(it) }
        Text(passengerMoney(price), fontWeight = FontWeight.SemiBold, color = OceanTeal)
    }
}
