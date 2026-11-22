package com.project.cruise.android.ui.passenger.components.mybookingdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.project.cruise.android.data.dto.passenger.tripdetail.*
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.PassengerTripDetailUiState
import java.util.Locale
import kotlin.math.roundToInt

/** Slot bản đồ: (overview, legs, vị trí tàu, key chặng đang chọn hoặc null, modifier). */
typealias TripMapContent =
        @Composable (TripRouteOverviewDto?, List<TripRouteLegDto>, VesselLocationDto?, String?, Modifier) -> Unit

/** Mặc định dùng bản đồ Mapbox thật. */
val DefaultTripMap: TripMapContent = { overview, legs, vessel, legKey, modifier ->
    TripRouteMap(overview, legs, vessel, legKey, modifier)
}

fun LazyListScope.journeyTab(
    state: PassengerTripDetailUiState,
    selectedLegKey: String?,
    onSelectLeg: (String?) -> Unit,
    onRetry: () -> Unit,
    mapContent: TripMapContent,
) {
    val routeBlocked = tripLoadGate(
        "j-route", state.routeLoading, state.routeError,
        state.routeOverview == null && state.routeLegs.isEmpty(), onRetry,
    )
    if (!routeBlocked) {
        phaseNotice(state)?.let { msg -> item(key = "j-notice") { TripBanner(msg) } }
        item(key = "j-map") { TripMapCard(state, selectedLegKey, mapContent) }
        item(key = "j-stats") { StatRow(state) }
        state.weather?.let { w -> item(key = "j-weather") { WeatherStrip(w) } }
        if (state.routeLegs.isNotEmpty()) {
            item(key = "j-legs-title") { PassengerSection("Các chặng", "Chọn chặng để xem trên bản đồ.") }
            item(key = "j-legs") { LegRow(state.routeLegs, selectedLegKey, onSelectLeg) }
        }
    }

    item(key = "j-itinerary-title") { PassengerSection("Lịch trình theo ngày") }
    if (tripLoadGate("j-itinerary", state.itineraryLoading, state.itineraryError, state.schedules.isEmpty(), onRetry)) return
    if (state.schedules.isEmpty()) {
        item(key = "j-itinerary-empty") {
            PassengerEmpty("Chưa có lịch trình", "Lịch trình chi tiết sẽ hiển thị khi được cập nhật.")
        }
        return
    }
    itemsIndexed(state.schedules, key = { index, day -> "j-day-${day.id ?: index}" }) { index, day ->
        ExpandableSection(
            title = "Ngày ${day.dayNumber ?: (index + 1)}",
            subtitle = day.name,
            trailing = day.realDay?.takeIf { it.isNotBlank() }?.let { passengerDate(it) },
            initiallyExpanded = index == 0,
        ) {
            day.description?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = OceanSlate)
            }
            val stops = day.stops.orEmpty()
            if (stops.isEmpty()) {
                Text("Chưa có điểm dừng.", style = MaterialTheme.typography.bodySmall, color = OceanSlate)
            } else {
                stops.forEach { StopRow(it) }
            }
        }
    }
}

private fun phaseNotice(state: PassengerTripDetailUiState): String? {
    val phase = tripPhase(state.routeOverview?.statusTrip)
    val hasGps = state.vesselLocation?.latitude != null
    return when {
        phase == TripPhase.UPCOMING ->
            "Chuyến đi chưa khởi hành. Vị trí tàu sẽ hiển thị khi tàu rời cảng, bạn có thể xem trước lộ trình."
        phase == TripPhase.FINISHED -> "Chuyến đi đã kết thúc. Bản đồ hiển thị lộ trình của chuyến."
        phase == TripPhase.CANCELLED -> "Chuyến đi này đã bị hủy."
        phase == TripPhase.ONGOING && !hasGps ->
            "Đang chờ tín hiệu GPS từ tàu. Vị trí sẽ tự cập nhật mỗi 10 giây."
        else -> null
    }
}

@Composable
private fun TripMapCard(
    state: PassengerTripDetailUiState,
    selectedLegKey: String?,
    mapContent: TripMapContent,
) {
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    val overview = state.routeOverview
    val vessel = state.vesselLocation
    val lat = vessel?.latitude ?: overview?.currentLatitude
    val lon = vessel?.longitude ?: overview?.currentLongitude
    val hasGps = vessel?.latitude != null

    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OceanLine.copy(alpha = .55f)),
        shadowElevation = 3.dp,
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(230.dp)) {
                // Bản đồ nhỏ chỉ để xem; lớp phủ chặn cử chỉ để trang vẫn cuộn được. Chạm để phóng to.
                if (!fullscreen) {
                    mapContent(overview, state.routeLegs, vessel, selectedLegKey, Modifier.matchParentSize())
                }
                Box(
                    Modifier.matchParentSize()
                        .clickable { fullscreen = true }
                        .semantics { contentDescription = "Mở bản đồ toàn màn hình" }
                )
                if (hasGps) {
                    Surface(
                        Modifier.align(Alignment.TopStart).padding(12.dp),
                        shape = RoundedCornerShape(50),
                        color = OceanGold,
                    ) {
                        Row(
                            Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            LiveDot(color = OceanNavy)
                            Text(
                                "TRỰC TIẾP",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = OceanNavy,
                            )
                        }
                    }
                }
                Surface(
                    Modifier.align(Alignment.TopEnd).padding(12.dp),
                    shape = RoundedCornerShape(50),
                    color = OceanNavy.copy(alpha = .82f),
                ) {
                    Text(
                        "Chạm để phóng to",
                        Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
                if (state.routeLoading) {
                    LinearProgressIndicator(
                        Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                        color = OceanGold,
                    )
                }
            }
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val from = overview?.resolvedDepartureName
                val to = overview?.resolvedDestinationName
                if (from != null || to != null) {
                    Text(
                        "${from ?: "—"} → ${to ?: "—"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OceanNavy,
                    )
                }
                MetaLine("Khởi hành", overview?.startDate?.takeIf { it.isNotBlank() }?.let { passengerDate(it) })
                MetaLine("Kết thúc", overview?.endDate?.takeIf { it.isNotBlank() }?.let { passengerDate(it) })
                HorizontalDivider(color = OceanLine)
                if (lat != null && lon != null) {
                    MetaLine("Tọa độ tàu", "${String.format(Locale.US, "%.5f", lat)}, ${String.format(Locale.US, "%.5f", lon)}")
                    val at = vessel?.updatedAt ?: vessel?.timestamp
                    MetaLine("Cập nhật lúc", at?.takeIf { it.isNotBlank() }?.let { "${passengerDate(it)} ${passengerTime(it)}" })
                } else {
                    Text(
                        state.vesselError ?: "Chưa có tín hiệu GPS của tàu.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OceanSlate,
                    )
                }
            }
        }
    }

    if (fullscreen) {
        Dialog(
            onDismissRequest = { fullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(Modifier.fillMaxSize().background(OceanNavy)) {
                mapContent(overview, state.routeLegs, vessel, selectedLegKey, Modifier.fillMaxSize())
                Surface(
                    onClick = { fullscreen = false },
                    modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(12.dp),
                    shape = RoundedCornerShape(50),
                    color = OceanGold,
                ) {
                    Text(
                        "Đóng bản đồ",
                        Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = OceanNavy,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatRow(state: PassengerTripDetailUiState) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatTile(
            "Tổng quãng đường",
            state.routeOverview?.distanceKm?.let { "${it.roundToInt()} km" } ?: "—",
            Modifier.weight(1.3f),
            highlight = true,
        )
        StatTile("Chặng", state.routeLegs.size.takeIf { it > 0 }?.toString() ?: "—", Modifier.weight(1f))
        StatTile("Ngày", state.schedules.size.takeIf { it > 0 }?.toString() ?: "—", Modifier.weight(1f))
    }
}

@Composable
private fun WeatherStrip(weather: TripWeatherDto) {
    PassengerCard {
        Text("Thời tiết tại cảng khởi hành", style = MaterialTheme.typography.labelMedium, color = OceanSlate)
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                weather.temperature?.let { "${it.roundToInt()}°C" } ?: "—",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = OceanGoldDeep,
            )
            Column(horizontalAlignment = Alignment.End) {
                weather.description?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = OceanNavy)
                }
                weather.windSpeed?.let {
                    Text("Gió $it m/s", style = MaterialTheme.typography.bodySmall, color = OceanSlate)
                }
            }
        }
    }
}

@Composable
private fun LegRow(legs: List<TripRouteLegDto>, selectedKey: String?, onSelect: (String?) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        item(key = "all") {
            LegChip("★", "Cả hành trình", "${legs.size} chặng", selectedKey == null) { onSelect(null) }
        }
        itemsIndexed(legs, key = { index, leg -> "${leg.resolvedKey}-$index" }) { index, leg ->
            LegChip(
                badge = (leg.legOrder ?: (index + 1)).toString(),
                line = "${leg.resolvedFromName} → ${leg.resolvedToName}",
                meta = leg.distanceKm?.let { "${it.roundToInt()} km" },
                selected = selectedKey == leg.resolvedKey,
            ) { onSelect(if (selectedKey == leg.resolvedKey) null else leg.resolvedKey) }
        }
    }
}

@Composable
private fun LegChip(badge: String, line: String, meta: String?, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) OceanNavy else Color.White,
        border = BorderStroke(if (selected) 0.dp else 1.dp, OceanLine),
        shadowElevation = if (selected) 4.dp else 1.dp,
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(if (selected) OceanGold else OceanGoldSoft),
                contentAlignment = Alignment.Center,
            ) {
                Text(badge, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = OceanNavy)
            }
            Text(
                line,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else OceanNavy,
                maxLines = 2,
                minLines = 2,
            )
            Text(
                meta ?: " ",
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) OceanGold else OceanSlate,
            )
        }
    }
}

@Composable
private fun StopRow(stop: TripScheduleStopDto) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.padding(top = 5.dp).size(12.dp).clip(CircleShape).background(OceanGold),
            contentAlignment = Alignment.Center,
        ) { Box(Modifier.size(5.dp).clip(CircleShape).background(OceanNavy)) }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                listOfNotNull(stop.stopOrder?.let { "$it." }, stop.portName).joinToString(" ").ifBlank { "Điểm dừng" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = OceanNavy,
            )
            val place = listOfNotNull(stop.portCity, stop.portCountry).filter { it.isNotBlank() }.joinToString(", ")
            if (place.isNotBlank()) Text(place, style = MaterialTheme.typography.bodySmall, color = OceanSlate)
            val arrive = stop.arriveAt?.takeIf { it.isNotBlank() }?.let { passengerTime(it) }
            val leave = stop.leaveAt?.takeIf { it.isNotBlank() }?.let { passengerTime(it) }
            if (arrive != null || leave != null) {
                Text(
                    "Neo tàu: ${arrive ?: "—"} – ${leave ?: "—"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = OceanGoldDeep,
                )
            }
            val visit = stop.visitActivity
            val visitName = visit?.visitName ?: visit?.name
            if (!visitName.isNullOrBlank()) {
                Text("Tham quan: $visitName", style = MaterialTheme.typography.bodySmall, color = OceanNavy)
            }
        }
    }
}