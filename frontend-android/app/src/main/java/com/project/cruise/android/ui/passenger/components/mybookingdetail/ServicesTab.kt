package com.project.cruise.android.ui.passenger.components.mybookingdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.PassengerTripDetailUiState

private data class FacilityUi(
    val name: String,
    val description: String?,
    val price: Double?,
    val imageUrl: String?,
    val extra: String?,
    val highlight: Boolean = false,
)

fun LazyListScope.servicesTab(state: PassengerTripDetailUiState, onRetry: () -> Unit) {
    val noData = state.products.isEmpty() && state.services.isEmpty() && state.cruiseActivities.isEmpty() &&
            state.visits.isEmpty() && state.packages.isEmpty()
    if (tripLoadGate("s", state.facilitiesLoading, state.facilitiesError, noData, onRetry)) return
    if (noData) {
        item(key = "s-empty") { PassengerEmpty("Chưa có dịch vụ", "Danh mục dịch vụ của chuyến đi sẽ hiển thị tại đây.") }
        return
    }

    val myPackageId = state.booking?.tourPackageId ?: state.matchedPackage?.id
    facilityGroup(
        "Gói tour của chuyến", "Gói của bạn được đánh dấu vàng.",
        state.packages.map {
            FacilityUi(
                name = it.name ?: "Gói tour",
                description = it.description,
                price = it.price,
                imageUrl = null,
                extra = it.maxPassengers?.let { n -> "Tối đa $n khách" },
                highlight = it.id != null && it.id == myPackageId,
            )
        },
    )
    facilityGroup(
        "Sản phẩm trên tàu", "Mua và thanh toán trong chuyến đi.",
        state.products.map {
            FacilityUi(
                it.productName ?: it.name ?: "Sản phẩm",
                it.productDescription ?: it.description, it.price, it.imageUrl,
                it.quantity?.let { q -> "SL: $q" },
            )
        },
    )
    facilityGroup(
        "Dịch vụ tiện ích", "Spa, ẩm thực và các tiện ích khác.",
        state.services.map {
            FacilityUi(
                it.serviceName ?: it.name ?: "Dịch vụ",
                it.serviceDescription ?: it.description, it.price, it.imageUrl,
                it.durationMinutes?.let { m -> "$m phút" },
            )
        },
    )
    facilityGroup(
        "Hoạt động trên tàu", "Giải trí và sự kiện trên du thuyền.",
        state.cruiseActivities.map {
            FacilityUi(
                it.activityName ?: it.name ?: "Hoạt động",
                it.activityDescription ?: it.description, it.price, it.imageUrl,
                timeRange(it.startTime, it.endTime),
            )
        },
    )
    facilityGroup(
        "Tham quan bờ", "Các điểm đến khi tàu cập cảng.",
        state.visits.map {
            FacilityUi(
                it.visitName ?: it.name ?: "Điểm tham quan",
                it.visitDescription ?: it.description, it.price, null,
                timeRange(it.startTime, it.endTime),
            )
        },
    )
}

private fun timeRange(start: String?, end: String?): String? {
    if (start.isNullOrBlank() && end.isNullOrBlank()) return null
    return "${if (start.isNullOrBlank()) "—" else passengerTime(start)} – ${if (end.isNullOrBlank()) "—" else passengerTime(end)}"
}

private fun LazyListScope.facilityGroup(title: String, subtitle: String, items: List<FacilityUi>) {
    if (items.isEmpty()) return
    item(key = "s-$title-title") { PassengerSection(title, subtitle) }
    item(key = "s-$title-row") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items) { FacilityCard(it) }
        }
    }
}

@Composable
private fun FacilityCard(item: FacilityUi) {
    Surface(
        Modifier.width(220.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(if (item.highlight) 2.dp else 1.dp, if (item.highlight) OceanGold else OceanLine.copy(alpha = .55f)),
        shadowElevation = if (item.highlight) 4.dp else 1.dp,
    ) {
        Column {
            Box(
                Modifier.fillMaxWidth().height(110.dp)
                    .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Star, null, tint = OceanGold.copy(alpha = .7f), modifier = Modifier.size(34.dp))
                if (!item.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                if (item.highlight) {
                    GoldBadge("GÓI CỦA BẠN", Modifier.align(Alignment.TopStart).padding(10.dp))
                }
            }
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                    maxLines = 2,
                    minLines = 2,
                )
                Text(
                    item.description?.takeIf { it.isNotBlank() } ?: " ",
                    style = MaterialTheme.typography.bodySmall,
                    color = OceanSlate,
                    maxLines = 2,
                    minLines = 2,
                )
                Text(item.extra ?: " ", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                Surface(shape = RoundedCornerShape(50), color = OceanGoldSoft) {
                    Text(
                        item.price?.let { tripMoney(it) } ?: "Đã gồm trong gói",
                        Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = OceanGoldDeep,
                    )
                }
            }
        }
    }
}