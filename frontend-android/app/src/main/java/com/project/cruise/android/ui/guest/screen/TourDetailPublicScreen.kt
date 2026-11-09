package com.project.cruise.android.ui.guest.screen

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.project.cruise.android.data.dto.tour.*
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.guest.components.tourdetail.*
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
                val isOpen = tour.statusBooking == TourBookingStatus.OPEN

                item {
                    TourDetailHero(
                        imageUrl = tour.cruise?.imageUrl,
                        statusLabel = tourSaleLabel(tour.statusBooking),
                        isOpen = isOpen,
                        title = tour.name ?: "Hành trình ${tour.code.orEmpty()}",
                        description =
                            tour.description?.takeIf { it.isNotBlank() }
                                ?: "Mô tả hành trình đang được cập nhật.",
                    )
                }
                item {
                    TourInfoCard(
                        cruiseName = tour.cruise?.name,
                        startDate = passengerDate(tour.startDate),
                        endDate = passengerDate(tour.endDate),
                        cruiseCapacity = tour.cruise?.maxPassengers,
                    )
                }

                // --- Gói hành trình ---
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
                    TourPackageCard(
                        name = pkg.name ?: "Gói hành trình",
                        description = pkg.description,
                        maxPassengers = pkg.maxPassengers,
                        price = pkg.price,
                        benefits = pkg.benefits.orEmpty().map { benefitDescription(it, tour) },
                        isLoggedIn = isLoggedIn,
                        canBook = isOpen,
                        onSelect = { if (isLoggedIn) onBookClick(pkg.id) else onLoginRequired() },
                    )
                }

                // --- Lịch trình ---
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
                    ScheduleDayCard(
                        ScheduleDayUi(
                            badge = "Ngày ${schedule.dayNumber ?: "—"} · ${passengerDate(schedule.realDay)}",
                            name = schedule.name ?: "Lịch trình",
                            description = schedule.description,
                            stops =
                                schedule.stops.orEmpty().sortedBy { it.stopOrder }.map { stop ->
                                    ScheduleStopUi(
                                        order = stop.stopOrder?.toString().orEmpty(),
                                        location =
                                            listOfNotNull(stop.portName, stop.portCity)
                                                .joinToString(" · "),
                                        timeText =
                                            if (stop.arriveAt != null || stop.leaveAt != null)
                                                "Đến ${passengerTime(stop.arriveAt)} · Rời ${passengerTime(stop.leaveAt)}"
                                            else null,
                                        visitName =
                                            stop.visitActivity?.let { it.visitName ?: "Tham quan" },
                                        visitDescription = stop.visitActivity?.visitDescription,
                                        visitPrice =
                                            stop.visitActivity?.price?.let {
                                                "Giá tham quan: ${passengerMoney(it)}"
                                            },
                                    )
                                },
                        )
                    )
                }

                // --- Trải nghiệm trên tàu ---
                if (!tour.onboardActivities.isNullOrEmpty())
                    item { PassengerSection("Trải nghiệm trên tàu") }
                items(tour.onboardActivities.orEmpty(), key = { "activity-${it.id}" }) { activity ->
                    TourOfferingCard(
                        name = activity.activityName,
                        description = activity.activityDescription,
                        price = activity.price,
                        imageUrl = activity.imageUrl,
                        detail =
                            listOfNotNull(activity.startTime, activity.endTime)
                                .takeIf { it.isNotEmpty() }
                                ?.joinToString(" – ") { "${passengerDate(it)} · ${passengerTime(it)}" },
                    )
                }

                // --- Dịch vụ ---
                if (!tour.services.isNullOrEmpty())
                    item { PassengerSection("Dịch vụ dành cho bạn") }
                items(tour.services.orEmpty(), key = { "service-${it.id}" }) { service ->
                    TourOfferingCard(
                        name = service.serviceName,
                        description = service.serviceDescription,
                        price = service.price,
                        imageUrl = service.imageUrl,
                        detail = service.durationMinutes?.let { "$it phút" },
                    )
                }

                // --- Sản phẩm ---
                if (!tour.products.isNullOrEmpty()) item { PassengerSection("Sản phẩm trên tàu") }
                items(tour.products.orEmpty(), key = { "product-${it.id}" }) { product ->
                    TourOfferingCard(
                        name = product.productName,
                        description = product.productDescription,
                        price = product.price,
                        imageUrl = product.imageUrl,
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