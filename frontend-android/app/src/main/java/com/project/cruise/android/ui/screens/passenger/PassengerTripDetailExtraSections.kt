//package com.project.cruise.android.ui.screens.passenger
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.project.cruise.android.data.dto.passenger.tripdetail.*
//import java.text.NumberFormat
//import java.util.Locale
//
//private fun formatVndAmount(amount: Double?): String {
//    if (amount == null) return "Chưa cập nhật"
//    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
//    return "${formatter.format(amount.toLong())} ₫"
//}
//
//// ==========================================
//// 5. LỊCH TRÌNH (SCHEDULES + STOPS + VISIT ACTIVITY)
//// ==========================================
//@Composable
//fun TripItinerarySection(
//    schedules: List<TripScheduleDto>,
//    loading: Boolean,
//    error: String?,
//    onReload: () -> Unit
//) {
//    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//        SectionHeader(
//            eyebrow = "LỊCH TRÌNH",
//            title = "Lịch trình chuyến đi",
//            subtitle = "Các ngày hành trình, điểm dừng cảng và hoạt động tham quan.",
//            loading = loading,
//            onReload = onReload
//        )
//
//        when {
//            loading && schedules.isEmpty() -> SectionLoadingBox("Đang tải lịch trình...")
//            error != null && schedules.isEmpty() -> SectionErrorBox(error, onRetry = onReload)
//            schedules.isEmpty() -> SectionEmptyBox(
//                title = "Chưa có lịch trình",
//                subtitle = "Lịch trình chi tiết hiện chưa được cập nhật."
//            )
//            else -> {
//                schedules.forEachIndexed { idx, schedule ->
//                    val dayTitle = schedule.dayNumber?.let { "Ngày $it" } ?: "Ngày ${idx + 1}"
//                    val headerText = listOfNotNull(
//                        dayTitle,
//                        schedule.name?.takeIf { it.isNotBlank() },
//                        schedule.realDay?.takeIf { it.isNotBlank() }?.let { "($it)" }
//                    ).joinToString(" • ")
//
//                    ElevatedCard(
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(14.dp),
//                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(14.dp),
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Text(
//                                text = headerText,
//                                style = MaterialTheme.typography.titleSmall,
//                                fontWeight = FontWeight.Bold,
//                                color = Color(0xFF0F172A)
//                            )
//                            if (!schedule.description.isNullOrBlank()) {
//                                Text(
//                                    text = schedule.description,
//                                    style = MaterialTheme.typography.bodySmall,
//                                    color = Color(0xFF475569)
//                                )
//                            }
//
//                            val stops = schedule.stops.orEmpty()
//                            if (stops.isNotEmpty()) {
//                                HorizontalDivider(color = Color(0xFFE2E8F0))
//                                stops.forEachIndexed { sIdx, stop ->
//                                    val stopTitle = listOfNotNull(
//                                        "Điểm ${stop.stopOrder ?: (sIdx + 1)}",
//                                        stop.portName?.takeIf { it.isNotBlank() },
//                                        stop.portCity?.takeIf { it.isNotBlank() }
//                                    ).joinToString(" • ")
//
//                                    Surface(
//                                        modifier = Modifier.fillMaxWidth(),
//                                        shape = RoundedCornerShape(10.dp),
//                                        color = Color(0xFFF8FAFC),
//                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
//                                    ) {
//                                        Column(
//                                            modifier = Modifier.padding(10.dp),
//                                            verticalArrangement = Arrangement.spacedBy(4.dp)
//                                        ) {
//                                            Text(
//                                                text = stopTitle,
//                                                style = MaterialTheme.typography.bodyMedium,
//                                                fontWeight = FontWeight.SemiBold
//                                            )
//                                            val timeRange = listOfNotNull(
//                                                stop.arriveAt?.let { "Đến: $it" },
//                                                stop.leaveAt?.let { "Rời: $it" }
//                                            ).joinToString("  |  ")
//                                            if (timeRange.isNotBlank()) {
//                                                Text(timeRange, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
//                                            }
//                                            stop.visitActivity?.let { visit ->
//                                                val vName = visit.visitName ?: visit.name
//                                                if (!vName.isNullOrBlank()) {
//                                                    Text(
//                                                        text = "Tham quan: $vName",
//                                                        style = MaterialTheme.typography.bodySmall,
//                                                        color = Color(0xFF0284C7),
//                                                        fontWeight = FontWeight.Medium
//                                                    )
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// ==========================================
//// 6. DU THUYỀN (CRUISE + DECKS + AREAS)
//// ==========================================
//@Composable
//fun TripCruiseSection(
//    cruise: TripCruiseDto?,
//    decks: List<TripCruiseDeckDto>,
//    deckAreas: Map<String, List<TripCruiseDeckAreaDto>>,
//    areasLoading: Boolean,
//    onLoadDeckAreas: (String) -> Unit,
//    loading: Boolean,
//    error: String?,
//    onReload: () -> Unit
//) {
//    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//        SectionHeader(
//            eyebrow = "DU THUYỀN",
//            title = cruise?.name ?: "Thông tin du thuyền",
//            subtitle = "Tổng quan tàu, các tầng boong và khu vực tiện ích.",
//            loading = loading,
//            onReload = onReload
//        )
//
//        when {
//            loading && cruise == null && decks.isEmpty() -> SectionLoadingBox("Đang tải thông tin du thuyền...")
//            error != null && cruise == null && decks.isEmpty() -> SectionErrorBox(error, onRetry = onReload)
//            else -> {
//                cruise?.let { c ->
//                    ElevatedCard(
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(14.dp),
//                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(14.dp),
//                            verticalArrangement = Arrangement.spacedBy(6.dp)
//                        ) {
//                            Text(
//                                text = c.name ?: "Du thuyền",
//                                style = MaterialTheme.typography.titleMedium,
//                                fontWeight = FontWeight.Bold
//                            )
//                            val meta = listOfNotNull(
//                                c.code?.let { "Mã tàu: $it" },
//                                c.maxPassengers?.let { "Sức chứa: $it khách" }
//                            ).joinToString(" • ")
//                            if (meta.isNotBlank()) {
//                                Text(meta, style = MaterialTheme.typography.labelMedium, color = Color(0xFF0284C7))
//                            }
//                            if (!c.description.isNullOrBlank()) {
//                                Text(c.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF334155))
//                            }
//                        }
//                    }
//                }
//
//                if (decks.isNotEmpty()) {
//                    Text("Danh sách tầng (${decks.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
//                    decks.forEachIndexed { index, deck ->
//                        val deckKey = deck.id ?: "deck-$index"
//                        val deckTitle = deck.name?.takeIf { it.isNotBlank() }
//                            ?: "Tầng ${deck.deckNumber ?: (index + 1)}"
//                        val areas = deck.id?.let { deckAreas[it] }.orEmpty()
//
//                        ElevatedCard(
//                            modifier = Modifier.fillMaxWidth(),
//                            shape = RoundedCornerShape(12.dp),
//                            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
//                        ) {
//                            Column(
//                                modifier = Modifier.padding(12.dp),
//                                verticalArrangement = Arrangement.spacedBy(6.dp)
//                            ) {
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Text(deckTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
//                                    deck.id?.let { dId ->
//                                        TextButton(
//                                            onClick = { onLoadDeckAreas(dId) },
//                                            enabled = !areasLoading
//                                        ) {
//                                            Text(if (areas.isEmpty()) "Xem khu vực" else "Tải lại khu vực")
//                                        }
//                                    }
//                                }
//                                if (!deck.description.isNullOrBlank()) {
//                                    Text(deck.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
//                                }
//                                if (areas.isNotEmpty()) {
//                                    areas.forEach { area ->
//                                        Text(
//                                            text = "• ${area.name ?: "Khu vực"}${area.areaType?.let { " ($it)" } ?: ""}",
//                                            style = MaterialTheme.typography.bodySmall,
//                                            color = Color(0xFF334155)
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// ==========================================
//// 7. LOẠI HÌNH PHỤC VỤ & GÓI TOUR
//// ==========================================
//@Composable
//fun TripFacilitiesAndPackagesSection(
//    products: List<TripProductDto>,
//    services: List<TripServiceDto>,
//    cruiseActivities: List<TripOnboardActivityDto>,
//    visits: List<TripVisitActivityDto>,
//    packages: List<TripPackageDto>,
//    loading: Boolean,
//    error: String?,
//    onReload: () -> Unit
//) {
//    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//        SectionHeader(
//            eyebrow = "LOẠI HÌNH PHỤC VỤ",
//            title = "Dịch vụ, hoạt động & gói tour",
//            subtitle = "Danh sách tiện ích trên tàu và các gói dịch vụ của chuyến đi.",
//            loading = loading,
//            onReload = onReload
//        )
//
//        when {
//            loading && products.isEmpty() && services.isEmpty() && cruiseActivities.isEmpty() && visits.isEmpty() && packages.isEmpty() ->
//                SectionLoadingBox("Đang tải tiện ích và gói tour...")
//            error != null && products.isEmpty() && services.isEmpty() && cruiseActivities.isEmpty() && visits.isEmpty() && packages.isEmpty() ->
//                SectionErrorBox(error, onRetry = onReload)
//            else -> {
//                FacilityGroupCard(
//                    title = "Gói tour (${packages.size})",
//                    items = packages.map { pkg ->
//                        val priceText = pkg.price?.let { formatVndAmount(it) }
//                        val sub = listOfNotNull(priceText, pkg.description?.takeIf { it.isNotBlank() }).joinToString(" • ")
//                        (pkg.name ?: "Gói tour") to sub
//                    }
//                )
//                FacilityGroupCard(
//                    title = "Dịch vụ trên tàu (${services.size})",
//                    items = services.map { s ->
//                        val name = s.serviceName ?: s.name ?: "Dịch vụ"
//                        val sub = listOfNotNull(s.price?.let { formatVndAmount(it) }, s.durationMinutes?.let { "$it phút" }).joinToString(" • ")
//                        name to sub
//                    }
//                )
//                FacilityGroupCard(
//                    title = "Sản phẩm đi kèm (${products.size})",
//                    items = products.map { p ->
//                        val name = p.productName ?: p.name ?: "Sản phẩm"
//                        val sub = listOfNotNull(p.price?.let { formatVndAmount(it) }, p.quantity?.let { "SL: $it" }).joinToString(" • ")
//                        name to sub
//                    }
//                )
//                FacilityGroupCard(
//                    title = "Hoạt động giải trí (${cruiseActivities.size})",
//                    items = cruiseActivities.map { a ->
//                        val name = a.activityName ?: a.name ?: "Hoạt động"
//                        val sub = a.price?.let { formatVndAmount(it) } ?: a.activityDescription.orEmpty()
//                        name to sub
//                    }
//                )
//                FacilityGroupCard(
//                    title = "Điểm tham quan (${visits.size})",
//                    items = visits.map { v ->
//                        val name = v.visitName ?: v.name ?: "Điểm tham quan"
//                        val sub = v.price?.let { formatVndAmount(it) } ?: v.visitDescription.orEmpty()
//                        name to sub
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun FacilityGroupCard(
//    title: String,
//    items: List<Pair<String, String>>
//) {
//    ElevatedCard(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(14.dp),
//        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
//    ) {
//        Column(
//            modifier = Modifier.padding(14.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
//            if (items.isEmpty()) {
//                Text("Chưa có dữ liệu.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
//            } else {
//                items.forEach { (name, sub) ->
//                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
//                        Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
//                        if (sub.isNotBlank()) {
//                            Text(sub, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// ==========================================
//// 8. LƯỢT SỬ DỤNG
//// ==========================================
//@Composable
//fun TripUsagesSection(
//    productUsages: List<TripUsageItemDto>,
//    serviceUsages: List<TripUsageItemDto>,
//    activityCruiseUsages: List<TripUsageItemDto>,
//    visitUsages: List<TripUsageItemDto>,
//    loading: Boolean,
//    error: String?,
//    onReload: () -> Unit
//) {
//    val allRows = buildList {
//        productUsages.forEach { add(Triple(it.itemName ?: it.name ?: "Sản phẩm", "Sản phẩm", it)) }
//        serviceUsages.forEach { add(Triple(it.itemName ?: it.name ?: "Dịch vụ", "Dịch vụ", it)) }
//        activityCruiseUsages.forEach { add(Triple(it.itemName ?: it.name ?: "Hoạt động", "Hoạt động tàu", it)) }
//        visitUsages.forEach { add(Triple(it.itemName ?: it.name ?: "Tham quan", "Tham quan", it)) }
//    }
//
//    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//        SectionHeader(
//            eyebrow = "LƯỢT SỬ DỤNG",
//            title = "Lịch sử sử dụng quyền lợi & dịch vụ",
//            subtitle = "Các lượt đã sử dụng thuộc đơn đặt chỗ.",
//            loading = loading,
//            onReload = onReload
//        )
//
//        when {
//            loading && allRows.isEmpty() -> SectionLoadingBox("Đang tải lượt sử dụng...")
//            error != null && allRows.isEmpty() -> SectionErrorBox(error, onRetry = onReload)
//            allRows.isEmpty() -> SectionEmptyBox(
//                title = "Chưa có lượt sử dụng nào",
//                subtitle = "Chưa ghi nhận lượt sử dụng sản phẩm, dịch vụ hoặc hoạt động."
//            )
//            else -> {
//                allRows.forEach { (title, category, item) ->
//                    ElevatedCard(
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(12.dp),
//                            verticalArrangement = Arrangement.spacedBy(4.dp)
//                        ) {
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
//                                Text(category, style = MaterialTheme.typography.labelMedium, color = Color(0xFF0284C7))
//                            }
//                            val meta = listOfNotNull(
//                                item.quantity?.let { "SL: $it" },
//                                item.status?.let { "Trạng thái: $it" },
//                                item.usedAt?.let { "Thời gian: $it" },
//                                item.totalPrice?.let { formatVndAmount(it) }
//                            ).joinToString(" • ")
//                            if (meta.isNotBlank()) {
//                                Text(meta, style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569))
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// ==========================================
//// 9. HÓA ĐƠN
//// ==========================================
//@Composable
//fun TripBillsSection(
//    bills: List<TripBillDto>,
//    loading: Boolean,
//    error: String?,
//    onReload: () -> Unit
//) {
//    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//        SectionHeader(
//            eyebrow = "HÓA ĐƠN",
//            title = "Hóa đơn thanh toán",
//            subtitle = "Danh sách hóa đơn thuộc đơn đặt chỗ.",
//            loading = loading,
//            onReload = onReload
//        )
//
//        when {
//            loading && bills.isEmpty() -> SectionLoadingBox("Đang tải danh sách hóa đơn...")
//            error != null && bills.isEmpty() -> SectionErrorBox(error, onRetry = onReload)
//            bills.isEmpty() -> SectionEmptyBox(
//                title = "Chưa có hóa đơn",
//                subtitle = "Đơn đặt chỗ hiện chưa có hóa đơn nào."
//            )
//            else -> {
//                bills.forEachIndexed { idx, bill ->
//                    ElevatedCard(
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(14.dp),
//                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(14.dp),
//                            verticalArrangement = Arrangement.spacedBy(6.dp)
//                        ) {
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Text(
//                                    text = bill.billCode ?: bill.code ?: "Hóa đơn #${idx + 1}",
//                                    style = MaterialTheme.typography.titleSmall,
//                                    fontWeight = FontWeight.Bold
//                                )
//                                Text(
//                                    text = bill.status ?: "Chưa rõ",
//                                    style = MaterialTheme.typography.labelMedium,
//                                    color = Color(0xFF0284C7),
//                                    fontWeight = FontWeight.SemiBold
//                                )
//                            }
//                            Text(
//                                text = "Số tiền: ${formatVndAmount(bill.totalAmount)}",
//                                style = MaterialTheme.typography.bodyMedium,
//                                fontWeight = FontWeight.SemiBold
//                            )
//                            val timeInfo = bill.paidAt ?: bill.createdAt
//                            if (!timeInfo.isNullOrBlank()) {
//                                Text("Thời gian: $timeInfo", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}