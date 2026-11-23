package com.project.cruise.android.ui.guest.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.tour.*
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.guest.components.tourdetail.*
import com.project.cruise.android.ui.theme.OceanSand
import com.project.cruise.android.viewmodel.tour.*
import java.math.BigDecimal

/** Số item đứng trước tab sticky: header(0), hero(1), thông tin nhanh(2), tab(3). */
private const val TAB_HEADER_INDEX = 3

private enum class TourTab(val label: String) {
    PACKAGES("Gói tour"),
    SCHEDULE("Lịch trình"),
    ONBOARD("Dịch vụ"),
    INFO("Thông tin"),
}

private data class OfferingUi(
    val name: String?,
    val description: String?,
    val price: BigDecimal?,
    val imageUrl: String?,
    val detail: String?,
)

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

private fun availableTabs(tour: PublicTourDetailResponse?): List<TourTab> =
    buildList {
        add(TourTab.PACKAGES)
        if (tour != null) {
            if (!tour.schedules.isNullOrEmpty()) add(TourTab.SCHEDULE)
            if (!tour.onboardActivities.isNullOrEmpty() || !tour.services.isNullOrEmpty() || !tour.products.isNullOrEmpty())
                add(TourTab.ONBOARD)
        }
        add(TourTab.INFO)
    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TourDetailContent(
    state: TourDetailState,
    isLoggedIn: Boolean,
    onBookClick: (String) -> Unit,
    onLoginRequired: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    val listState = rememberLazyListState()
    var tabIndex by rememberSaveable { mutableStateOf(0) }
    val tourForTabs = (state as? TourDetailState.Success)?.tour
    val tabs = remember(tourForTabs) { availableTabs(tourForTabs) }
    val tab = tabs.getOrElse(tabIndex) { TourTab.PACKAGES }

    // Đổi tab thì đưa trang về ngay dưới thanh tab, tránh đứng giữa một tab ngắn hơn.
    LaunchedEffect(tab) {
        if (listState.firstVisibleItemIndex > TAB_HEADER_INDEX) listState.scrollToItem(TAB_HEADER_INDEX)
    }

    PassengerPage("Chi tiết hành trình", onBack = onBack, listState = listState) {
        when (state) {
            TourDetailState.Idle,
            TourDetailState.Loading -> item(key = "loading") { PassengerLoading() }
            is TourDetailState.Error -> item(key = "error") { PassengerError(state.message, onRetry) }
            is TourDetailState.Success -> {
                val tour = state.tour
                val isOpen = tour.statusBooking == TourBookingStatus.OPEN
                val schedules = tour.schedules.orEmpty().sortedBy { it.dayNumber }
                val packages = tour.packages.orEmpty()

                item(key = "hero") {
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
                item(key = "facts") {
                    val capacity = tour.cruise?.maxPassengers
                    TourFactsRow(
                        startDate = passengerDate(tour.startDate),
                        endDate = passengerDate(tour.endDate),
                        lastLabel = if (capacity != null) "Sức chứa" else "Số ngày",
                        lastValue = capacity?.let { "$it khách" } ?: schedules.size.takeIf { it > 0 }?.let { "$it ngày" } ?: "—",
                    )
                }
                stickyHeader(key = "tour-tabs") {
                    Box(Modifier.fillMaxWidth().background(OceanSand)) {
                        TourDetailTabRow(
                            tabs = tabs.map {
                                TourTabUi(
                                    it.label,
                                    when (it) {
                                        TourTab.PACKAGES -> packages.size.takeIf { n -> n > 0 }
                                        TourTab.SCHEDULE -> schedules.size
                                        else -> null
                                    },
                                )
                            },
                            selected = tabs.indexOf(tab),
                            onSelect = { tabIndex = it },
                        )
                    }
                }

                when (tab) {
                    TourTab.PACKAGES -> packagesTab(tour, isOpen, isLoggedIn, onBookClick, onLoginRequired)
                    TourTab.SCHEDULE -> scheduleTab(tour)
                    TourTab.ONBOARD -> onboardTab(tour)
                    TourTab.INFO -> item(key = "info") {
                        TourInfoCard(
                            cruiseName = tour.cruise?.name,
                            cruiseCapacity = tour.cruise?.maxPassengers,
                            departurePort = tour.departurePort,
                            arrivalPort = tour.arrivalPort,
                        )
                    }
                }
            }
        }
    }
}

private fun LazyListScope.packagesTab(
    tour: PublicTourDetailResponse,
    isOpen: Boolean,
    isLoggedIn: Boolean,
    onBookClick: (String) -> Unit,
    onLoginRequired: () -> Unit,
) {
    val packages = tour.packages.orEmpty()
    item(key = "pkg-title") {
        PassengerSection("Chọn gói hành trình", "Vuốt ngang để so sánh giá và quyền lợi.")
    }
    if (packages.isEmpty()) {
        item(key = "pkg-empty") {
            PassengerEmpty("Chưa có gói đặt chỗ", "Gói tour sẽ được hiển thị khi được công bố.")
        }
        return
    }
    item(key = "pkg-row") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(packages, key = { "package-${it.id}" }) { pkg ->
                TourPackageCard(
                    name = pkg.name ?: "Gói hành trình",
                    description = pkg.description,
                    maxPassengers = pkg.maxPassengers,
                    price = pkg.price,
                    benefits = pkg.benefits.orEmpty().map { benefitDescription(it, tour) },
                    isLoggedIn = isLoggedIn,
                    canBook = isOpen,
                    onSelect = { if (isLoggedIn) onBookClick(pkg.id) else onLoginRequired() },
                    modifier = Modifier.fillParentMaxWidth(0.88f),
                )
            }
        }
    }
}

private fun LazyListScope.scheduleTab(tour: PublicTourDetailResponse) {
    val schedules = tour.schedules.orEmpty().sortedBy { it.dayNumber }
    item(key = "sch-title") { PassengerSection("Lịch trình", "Bấm vào từng ngày để xem các điểm dừng.") }
    itemsIndexed(schedules, key = { _, s -> "schedule-${s.id}" }) { index, schedule ->
        ScheduleDayCard(
            ScheduleDayUi(
                badge = "Ngày ${schedule.dayNumber ?: "—"} · ${passengerDate(schedule.realDay)}",
                name = schedule.name ?: "Lịch trình",
                description = schedule.description,
                stops =
                    schedule.stops.orEmpty().sortedBy { it.stopOrder }.map { stop ->
                        ScheduleStopUi(
                            order = stop.stopOrder?.toString().orEmpty(),
                            location = listOfNotNull(stop.portName, stop.portCity).joinToString(" · "),
                            timeText =
                                if (stop.arriveAt != null || stop.leaveAt != null)
                                    "Đến ${passengerTime(stop.arriveAt)} · Rời ${passengerTime(stop.leaveAt)}"
                                else null,
                            visitName = stop.visitActivity?.let { it.visitName ?: "Tham quan" },
                            visitDescription = stop.visitActivity?.visitDescription,
                            visitPrice = stop.visitActivity?.price?.let { "Giá tham quan: ${passengerMoney(it)}" },
                        )
                    },
            ),
            initiallyExpanded = index == 0,
        )
    }
}

private fun LazyListScope.onboardTab(tour: PublicTourDetailResponse) {
    offeringGroup(
        "ob-act", "Trải nghiệm trên tàu", "Giải trí và sự kiện trên du thuyền.",
        tour.onboardActivities.orEmpty().map {
            OfferingUi(
                it.activityName, it.activityDescription, it.price, it.imageUrl,
                listOfNotNull(it.startTime, it.endTime).takeIf { l -> l.isNotEmpty() }
                    ?.joinToString(" – ") { t -> "${passengerDate(t)} · ${passengerTime(t)}" },
            )
        },
    )
    offeringGroup(
        "ob-svc", "Dịch vụ dành cho bạn", "Tiện ích và dịch vụ trong chuyến đi.",
        tour.services.orEmpty().map {
            OfferingUi(it.serviceName, it.serviceDescription, it.price, it.imageUrl, it.durationMinutes?.let { m -> "$m phút" })
        },
    )
    offeringGroup(
        "ob-prd", "Sản phẩm trên tàu", "Mua sắm và ẩm thực trên du thuyền.",
        tour.products.orEmpty().map { OfferingUi(it.productName, it.productDescription, it.price, it.imageUrl, null) },
    )
}

private fun LazyListScope.offeringGroup(key: String, title: String, subtitle: String, list: List<OfferingUi>) {
    if (list.isEmpty()) return
    item(key = "$key-title") { PassengerSection(title, subtitle) }
    item(key = "$key-row") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(list) {
                TourOfferingCard(
                    name = it.name,
                    description = it.description,
                    price = it.price,
                    imageUrl = it.imageUrl,
                    detail = it.detail,
                    modifier = Modifier.width(220.dp),
                )
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