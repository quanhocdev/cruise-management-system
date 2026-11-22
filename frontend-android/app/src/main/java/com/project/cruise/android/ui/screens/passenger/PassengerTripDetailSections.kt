//package com.project.cruise.android.ui.screens.passenger
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.horizontalScroll
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.outlined.*
//import androidx.compose.foundation.clickable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.project.cruise.android.data.dto.passenger.tripdetail.*
//import com.project.cruise.android.ui.theme.OceanMint
//import com.project.cruise.android.ui.theme.OceanNavy
//import com.project.cruise.android.viewmodel.passenger.TripSectionKey
//import java.text.NumberFormat
//import java.util.Locale
//import androidx.compose.material.icons.filled.AccountBox
//import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Place
//import androidx.compose.material.icons.filled.ShoppingCart
//import androidx.compose.material.icons.filled.Star
//
//private fun formatVnd(amount: Double?): String {
//    if (amount == null) return "Chưa cập nhật"
//    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
//    return "${formatter.format(amount.toLong())} ₫"
//}
//
//private fun bookingStatusLabel(status: String?): String = when (status?.uppercase()) {
//    "PENDING" -> "Chờ xử lý"
//    "WAITING" -> "Chờ xác nhận"
//    "CONFIRMED" -> "Đã xác nhận"
//    "PAID" -> "Đã thanh toán"
//    "COMPLETED" -> "Hoàn thành"
//    "CANCELLED" -> "Đã hủy"
//    "REFUNDED" -> "Đã hoàn tiền"
//    "EXPIRED" -> "Hết hạn"
//    else -> status ?: "Chưa cập nhật"
//}
//
//@Composable
//fun TripDetailHeroCard(
//    booking: PassengerBookingDetailDto?,
//    routeOverview: TripRouteOverviewDto?,
//    cruise: TripCruiseDto?,
//    vesselLocation: VesselLocationDto?,
//    onReload: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Surface(
//        modifier = modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
//        color = OceanNavy
//    ) {
//        Column(
//            modifier = Modifier
//                .background(
//                    Brush.verticalGradient(
//                        colors = listOf(OceanNavy, Color(0xFF0F2D4A))
//                    )
//                )
//                .statusBarsPadding()
//                .padding(horizontal = 16.dp, vertical = 18.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(modifier = Modifier.weight(1f)) {
//                    Text(
//                        text = "GIÁM SÁT CHUYẾN ĐI",
//                        style = MaterialTheme.typography.labelSmall,
//                        color = OceanMint,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        text = routeOverview?.tourName
//                            ?: cruise?.name
//                            ?: "Chi tiết chuyến đi",
//                        style = MaterialTheme.typography.titleLarge,
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//
//                IconButton(
//                    onClick = onReload,
//                    colors = IconButtonDefaults.iconButtonColors(
//                        containerColor = Color.White.copy(alpha = 0.12f),
//                        contentColor = Color.White
//                    )
//                ) {
//                    Icon(Icons.Outlined.Refresh, contentDescription = "Tải lại")
//                }
//            }
//
//            val depName = routeOverview?.departurePort?.name
//            val arrName = routeOverview?.arrivalPort?.name
//            if (!depName.isNullOrBlank() || !arrName.isNullOrBlank()) {
//                Text(
//                    text = "${depName ?: "Cảng đi"} → ${arrName ?: "Cảng đến"}",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.White.copy(alpha = 0.85f)
//                )
//            }
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                booking?.bookingCode?.let { code ->
//                    HeroChip(text = "Mã: $code")
//                }
//                booking?.status?.let { st ->
//                    HeroChip(text = bookingStatusLabel(st))
//                }
//                if (vesselLocation?.latitude != null && vesselLocation.longitude != null) {
//                    HeroChip(text = "GPS: ${String.format(Locale.US, "%.4f", vesselLocation.latitude)}, ${String.format(Locale.US, "%.4f", vesselLocation.longitude)}")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun HeroChip(text: String) {
//    Surface(
//        shape = RoundedCornerShape(999.dp),
//        color = OceanMint.copy(alpha = 0.18f),
//        border = BorderStroke(1.dp, OceanMint.copy(alpha = 0.45f))
//    ) {
//        Text(
//            text = text,
//            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
//            style = MaterialTheme.typography.labelSmall,
//            color = Color.White,
//            fontWeight = FontWeight.Medium
//        )
//    }
//}
//
//@Composable
//fun TripSectionTabBar(
//    activeSection: TripSectionKey,
//    onSelectSection: (TripSectionKey) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .horizontalScroll(rememberScrollState()),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        TripSectionKey.entries.forEach { section ->
//            val selected = activeSection == section
//            FilterChip(
//                selected = selected,
//                onClick = { onSelectSection(section) },
//                label = {
//                    Text(
//                        text = section.label,
//                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
//                    )
//                },
//                colors = FilterChipDefaults.filterChipColors(
//                    selectedContainerColor = OceanNavy,
//                    selectedLabelColor = Color.White
//                )
//            )
//        }
//    }
//}
//
//@Composable
//fun SectionHeader(
//    eyebrow: String,
//    title: String,
//    subtitle: String,
//    loading: Boolean,
//    onReload: () -> Unit
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.Top
//    ) {
//        Column(modifier = Modifier.weight(1f)) {
//            Surface(
//                shape = RoundedCornerShape(999.dp),
//                color = Color(0xFFE0F2FE)
//            ) {
//                Text(
//                    text = eyebrow,
//                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
//                    style = MaterialTheme.typography.labelSmall,
//                    color = Color(0xFF0284C7),
//                    fontWeight = FontWeight.Bold
//                )
//            }
//            Spacer(modifier = Modifier.height(6.dp))
//            Text(
//                text = title,
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold,
//                color = Color(0xFF0F172A)
//            )
//            Text(
//                text = subtitle,
//                style = MaterialTheme.typography.bodySmall,
//                color = Color(0xFF64748B)
//            )
//        }
//
//        OutlinedButton(
//            onClick = onReload,
//            enabled = !loading,
//            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
//        ) {
//            Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
//            Spacer(modifier = Modifier.width(4.dp))
//            Text("Tải lại", style = MaterialTheme.typography.labelMedium)
//        }
//    }
//}
//
//@Composable
//fun SectionLoadingBox(text: String) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(14.dp))
//            .background(Color(0xFFF8FAFC))
//            .padding(24.dp),
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
//        Spacer(modifier = Modifier.width(10.dp))
//        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF475569))
//    }
//}
//
//@Composable
//fun SectionErrorBox(message: String, onRetry: () -> Unit) {
//    Surface(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(14.dp),
//        color = Color(0xFFFEF2F2),
//        border = BorderStroke(1.dp, Color(0xFFFECACA))
//    ) {
//        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//            Text(message, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFB91C1C))
//            Button(
//                onClick = onRetry,
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
//            ) {
//                Text("Thử lại")
//            }
//        }
//    }
//}
//
//@Composable
//fun SectionEmptyBox(title: String, subtitle: String) {
//    Surface(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(14.dp),
//        color = Color(0xFFF8FAFC),
//        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(28.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(6.dp)
//        ) {
//            Icon(
//                Icons.Outlined.Info,
//                contentDescription = null,
//                tint = Color(0xFF64748B),
//                modifier = Modifier.size(28.dp)
//            )
//            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
//            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
//        }
//    }
//}
//
//@Composable
//private fun InfoRowItem(icon: ImageVector, label: String, value: String) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(10.dp))
//            .background(Color(0xFFF8FAFC))
//            .padding(12.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .size(34.dp)
//                .clip(RoundedCornerShape(8.dp))
//                .background(Color(0xFFE0F2FE)),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(icon, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
//        }
//        Column(modifier = Modifier.weight(1f)) {
//            Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
//            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
//        }
//    }
//}
//
//// ==========================================
//// 1. THEO DÕI HÀNH TRÌNH (GPS POLLING 10s + LEGS + WEATHER)
//// ==========================================
//@Composable
//fun TripRouteSection(
//    overview: TripRouteOverviewDto?,
//    legs: List<TripRouteLegDto>,
//    vesselLocation: VesselLocationDto?,
//    weather: TripWeatherDto?,
//    loading: Boolean,
//    error: String?,
//    onReload: () -> Unit
//) {
//    var selectedLegKey by remember { mutableStateOf<String?>(null) }
//    val selectedLeg = remember(legs, selectedLegKey) {
//        legs.firstOrNull { it.resolvedKey == selectedLegKey }
//    }
//    val activeGeoJson = selectedLeg?.routeGeoJson ?: overview?.routeGeoJson
//
//    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//        SectionHeader(
//            eyebrow = "HÀNH TRÌNH",
//            title = "Tuyến đường chuyến đi",
//            subtitle = "Theo dõi lộ trình và các chặng di chuyển của tàu (GPS tự cập nhật mỗi 10s).",
//            loading = loading,
//            onReload = onReload
//        )
//
//        if (loading && overview == null && legs.isEmpty() && vesselLocation == null) {
//            SectionLoadingBox("Đang tải dữ liệu hành trình và vị trí tàu...")
//            return
//        }
//
//        if (error != null && overview == null && legs.isEmpty() && vesselLocation == null) {
//            SectionErrorBox(error, onRetry = onReload)
//            return
//        }
//
//        // Card tổng quan cảng khởi hành - tổng khoảng cách - cảng đích
//        ElevatedCard(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(14.dp),
//            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
//        ) {
//            Column(
//                modifier = Modifier.padding(14.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                InfoRowItem(
//                    icon = Icons.Default.Place,
//                    label = "Cảng khởi hành",
//                    value = overview?.resolvedDepartureName ?: "Chưa cập nhật"
//                )
//                InfoRowItem(
//                    icon = Icons.Default.Star,
//                    label = "Tổng khoảng cách",
//                    value = formatDistanceKm(overview?.distanceKm)
//                )
//                InfoRowItem(
//                    icon = Icons.Default.Place,
//                    label = "Cảng đích",
//                    value = overview?.resolvedDestinationName ?: "Chưa cập nhật"
//                )
//            }
//        }
//
//        TripMapboxCard(
//            overview = overview,
//            legs = legs,
//            vesselLocation = vesselLocation,
//            routeGeoJson = activeGeoJson
//        )
//
//        ElevatedCard(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(14.dp),
//            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
//        ) {
//            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                Text("Tọa độ GPS hiện tại (Polling 10s)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
//                val lat = vesselLocation?.latitude ?: overview?.currentLatitude
//                val lon = vesselLocation?.longitude ?: overview?.currentLongitude
//                InfoRowItem(
//                    icon = Icons.Default.Place,
//                    label = "Tọa độ",
//                    value = if (lat != null && lon != null) "${String.format(Locale.US, "%.6f", lat)}, ${String.format(Locale.US, "%.6f", lon)}" else "Chưa có tín hiệu GPS"
//                )
//                val updatedTime = vesselLocation?.updatedAt ?: vesselLocation?.timestamp
//                if (!updatedTime.isNullOrBlank()) {
//                    InfoRowItem(icon = Icons.Default.DateRange, label = "Cập nhật lúc", value = updatedTime)
//                }
//                weather?.let { w ->
//                    val summary = buildList {
//                        w.temperature?.let { add("${it}°C") }
//                        w.description?.takeIf { it.isNotBlank() }?.let { add(it) }
//                        w.windSpeed?.let { add("Gió ${it} m/s") }
//                    }.joinToString(" • ")
//                    if (summary.isNotBlank()) {
//                        InfoRowItem(icon = Icons.Default.Star, label = "Thời tiết khu vực", value = summary)
//                    }
//                }
//            }
//        }
//
//        if (legs.isNotEmpty()) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("Các chặng hành trình", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
//                Text("${legs.size} chặng", style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
//            }
//
//            legs.forEachIndexed { index, leg ->
//                val selected = selectedLegKey == leg.resolvedKey
//                ElevatedCard(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            selectedLegKey = if (selected) null else leg.resolvedKey
//                        },
//                    shape = RoundedCornerShape(12.dp),
//                    colors = CardDefaults.elevatedCardColors(
//                        containerColor = if (selected) Color(0xFFEFF6FF) else Color.White
//                    )
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(12.dp),
//                        horizontalArrangement = Arrangement.spacedBy(10.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .size(30.dp)
//                                .clip(CircleShape)
//                                .background(if (selected) Color(0xFF0284C7) else Color(0xFFE2E8F0)),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = (leg.legOrder ?: (index + 1)).toString(),
//                                style = MaterialTheme.typography.labelMedium,
//                                fontWeight = FontWeight.Bold,
//                                color = if (selected) Color.White else Color(0xFF0F172A)
//                            )
//                        }
//
//                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
//                            Text(
//                                text = "${leg.resolvedFromName} → ${leg.resolvedToName}",
//                                style = MaterialTheme.typography.bodyMedium,
//                                fontWeight = FontWeight.SemiBold,
//                                color = Color(0xFF0F172A)
//                            )
//                            Text(
//                                text = formatDistanceKm(leg.distanceKm),
//                                style = MaterialTheme.typography.bodySmall,
//                                color = Color(0xFF64748B)
//                            )
//                            leg.status?.let {
//                                Text("Trạng thái: $it", style = MaterialTheme.typography.labelSmall, color = Color(0xFF475569))
//                            }
//                        }
//
//                        Text(
//                            text = if (selected) "Bỏ chọn" else "Xem chặng",
//                            style = MaterialTheme.typography.labelMedium,
//                            fontWeight = FontWeight.SemiBold,
//                            color = Color(0xFF0284C7)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//// ==========================================
//// 2. ĐƠN ĐẶT CHỖ
//// ==========================================
//@Composable
//fun TripBookingSection(
//    booking: PassengerBookingDetailDto?,
//    packageName: String?,
//    loading: Boolean,
//    error: String?,
//    onReload: () -> Unit
//) {
//    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//        SectionHeader(
//            eyebrow = "THÔNG TIN ĐẶT CHỖ",
//            title = "Đơn đặt chỗ của bạn",
//            subtitle = "Thông tin đặt tour và trạng thái đơn đặt chỗ.",
//            loading = loading,
//            onReload = onReload
//        )
//
//        if (loading && booking == null) {
//            SectionLoadingBox("Đang tải thông tin đặt chỗ...")
//            return
//        }
//
//        if (error != null && booking == null) {
//            SectionErrorBox(error, onRetry = onReload)
//            return
//        }
//
//
//
//        if (booking == null) return
//
//        ElevatedCard(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(16.dp),
//            colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
//        ) {
//            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column {
//                        Text("Mã đặt chỗ", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
//                        Text(
//                            text = booking.bookingCode ?: booking.id?.toString() ?: "Chưa cập nhật",
//                            style = MaterialTheme.typography.titleMedium,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                    Surface(
//                        shape = RoundedCornerShape(999.dp),
//                        color = Color(0xFFDCFCE7)
//                    ) {
//                        Text(
//                            text = bookingStatusLabel(booking.status),
//                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
//                            style = MaterialTheme.typography.labelMedium,
//                            color = Color(0xFF15803D),
//                            fontWeight = FontWeight.SemiBold
//                        )
//                    }
//                }
//
//                HorizontalDivider(color = Color(0xFFE2E8F0))
//
//                InfoRowItem(
//                    icon = Icons.Default.DateRange,
//                    label = "Ngày đặt",
//                    value = booking.createdAt ?: "Chưa cập nhật"
//                )
//                InfoRowItem(
//                    icon = Icons.Default.Person,
//                    label = "Số lượng hành khách",
//                    value = booking.numberPassengers?.toString() ?: "Chưa cập nhật"
//                )
//                booking.numberOfRooms?.let { rooms ->
//                    InfoRowItem(icon = Icons.Default.Home, label = "Số phòng", value = rooms.toString())
//                }
//                val resolvedPackageName = booking.packageName ?: packageName
//                if (!resolvedPackageName.isNullOrBlank()) {
//                    InfoRowItem(icon = Icons.Default.Star, label = "Gói tour", value = resolvedPackageName)
//                }
//                booking.totalAmount?.let { amount ->
//                    InfoRowItem(icon = Icons.Default.ShoppingCart, label = "Tổng giá trị", value = formatVnd(amount))
//                }
//            }
//        }
//    }
//}
//
//// ==========================================
//// 3. DANH SÁCH HÀNH KHÁCH
//// ==========================================
//@Composable
//fun TripPassengersSection(
//    passengers: List<BookingPassengerItemDto>,
//    loading: Boolean,
//    error: String?,
//    onReload: () -> Unit
//) {
//    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//        SectionHeader(
//            eyebrow = "DANH SÁCH HÀNH KHÁCH",
//            title = "Hành khách trong chuyến đi",
//            subtitle = "Thông tin những hành khách thuộc đơn đặt chỗ này.",
//            loading = loading,
//            onReload = onReload
//        )
//
//        when {
//            loading -> SectionLoadingBox("Đang tải danh sách hành khách...")
//            error != null -> SectionErrorBox(error, onRetry = onReload)
//            passengers.isEmpty() -> SectionEmptyBox(
//                title = "Chưa có thông tin hành khách",
//                subtitle = "Danh sách hành khách hiện chưa có dữ liệu."
//            )
//            else -> {
//                Text(
//                    text = "Tổng cộng ${passengers.size} hành khách",
//                    style = MaterialTheme.typography.labelLarge,
//                    fontWeight = FontWeight.SemiBold,
//                    color = Color(0xFF0F172A)
//                )
//
//                passengers.forEachIndexed { index, item ->
//                    val profile = item.passenger
//                    ElevatedCard(
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(14.dp),
//                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
//                    ) {
//                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(10.dp)
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        .size(40.dp)
//                                        .clip(CircleShape)
//                                        .background(Color(0xFFE0F2FE)),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Icon(Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF0284C7))
//                                }
//                                Column {
//                                    Text("Hành khách ${index + 1}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
//                                    Text(
//                                        text = profile?.fullName ?: "Hành khách",
//                                        style = MaterialTheme.typography.titleSmall,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                }
//                            }
//
//                            val badge = listOfNotNull(profile?.gender, item.status).joinToString(" • ")
//                            if (badge.isNotBlank()) {
//                                Text(badge, style = MaterialTheme.typography.bodySmall, color = Color(0xFF0284C7), fontWeight = FontWeight.Medium)
//                            }
//
//                            profile?.email?.let { InfoRowItem(Icons.Outlined.Email, "Email", it) }
//                            profile?.phoneNumber?.let { InfoRowItem(Icons.Outlined.Phone, "Số điện thoại", it) }
//                            profile?.dateOfBirth?.let { InfoRowItem(Icons.Default.DateRange, "Ngày sinh", it) }
//                            profile?.identificationNumber?.let { idNum ->
//                                val label = profile.idCardType?.let { "Giấy tờ ($it)" } ?: "Giấy tờ tùy thân"
//                                InfoRowItem(Icons.Default.AccountBox, label, idNum)
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
//// 4. QUYỀN LỢI GÓI TOUR (CÓ MÀU PHÂN LOẠI)
//// ==========================================
//private data class BenefitPalette(
//    val accent: Color,
//    val cardBg: Color,
//    val border: Color,
//    val badgeBg: Color,
//    val icon: ImageVector
//)
//
//private fun benefitPaletteFor(type: String?): BenefitPalette = when {
//    type.orEmpty().contains("PRODUCT", true) || type.orEmpty().contains("FOOD", true) ->
//        BenefitPalette(Color(0xFFD97706), Color(0xFFFFFBEB), Color(0xFFFDE68A), Color(0xFFFEF3C7), Icons.Default.ShoppingCart)
//    type.orEmpty().contains("SERVICE", true) || type.orEmpty().contains("RESTAURANT", true) ->
//        BenefitPalette(Color(0xFF0D9488), Color(0xFFF0FDFA), Color(0xFF99F6E4), Color(0xFFCCFBF1), Icons.Default.Star)
//    type.orEmpty().contains("ACTIVITY", true) || type.orEmpty().contains("CRUISE", true) ->
//        BenefitPalette(Color(0xFF4F46E5), Color(0xFFEEF2FF), Color(0xFFC7D2FE), Color(0xFFE0E7FF), Icons.Default.CheckCircle)
//    type.orEmpty().contains("VISIT", true) || type.orEmpty().contains("SHORE", true) ->
//        BenefitPalette(Color(0xFF16A34A), Color(0xFFF0FDF4), Color(0xFFBBF7D0), Color(0xFFDCFCE7), Icons.Default.Place)
//    else ->
//        BenefitPalette(Color(0xFF0284C7), Color(0xFFF0F9FF), Color(0xFFBAE6FD), Color(0xFFE0F2FE), Icons.Default.Star)
//}
//
//@Composable
//fun TripBenefitsSection(
//    packageId: String?,
//    packageName: String?,
//    packageDescription: String?,
//    benefits: List<ResolvedBenefitUiModel>,
//    loading: Boolean,
//    error: String?,
//    onReload: () -> Unit
//) {
//    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//        SectionHeader(
//            eyebrow = "QUYỀN LỢI ĐẶT CHỖ",
//            title = "Quyền lợi của bạn",
//            subtitle = "Các quyền lợi được cấp theo gói của đơn đặt chỗ.",
//            loading = loading,
//            onReload = onReload
//        )
//
//        if (!packageName.isNullOrBlank() || !packageId.isNullOrBlank()) {
//            Surface(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(14.dp),
//                color = Color(0xFFEFF6FF),
//                border = BorderStroke(1.dp, Color(0xFF93C5FD))
//            ) {
//                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
//                    Text(
//                        text = "GÓI TOUR ĐÃ ĐẶT",
//                        style = MaterialTheme.typography.labelSmall,
//                        color = Color(0xFF1D4ED8),
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = packageName ?: "Chưa cập nhật tên gói",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF0F172A)
//                    )
//                    if (!packageDescription.isNullOrBlank()) {
//                        Text(packageDescription, style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569))
//                    }
//                    if (!packageId.isNullOrBlank()) {
//                        Text(
//                            text = "ID gói: $packageId",
//                            style = MaterialTheme.typography.labelSmall,
//                            fontFamily = FontFamily.Monospace,
//                            color = Color(0xFF334155)
//                        )
//                    }
//                }
//            }
//        }
//
//        when {
//            loading && benefits.isEmpty() -> SectionLoadingBox("Đang tải quyền lợi gói tour...")
//            error != null && benefits.isEmpty() -> SectionErrorBox(error, onRetry = onReload)
//            benefits.isEmpty() -> SectionEmptyBox(
//                title = "Chưa có thông tin quyền lợi",
//                subtitle = "Chưa tìm thấy quyền lợi nào được ghi nhận cho gói này."
//            )
//            else -> {
//                Text(
//                    text = "${benefits.size} quyền lợi được bao gồm trong gói",
//                    style = MaterialTheme.typography.labelLarge,
//                    fontWeight = FontWeight.SemiBold
//                )
//
//                benefits.forEach { benefit ->
//                    val palette = benefitPaletteFor(benefit.type)
//                    Surface(
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(14.dp),
//                        color = palette.cardBg,
//                        border = BorderStroke(1.dp, palette.border)
//                    ) {
//                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(12.dp)
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        .size(40.dp)
//                                        .clip(RoundedCornerShape(10.dp))
//                                        .background(palette.accent),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Icon(palette.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
//                                }
//                                Column(modifier = Modifier.weight(1f)) {
//                                    Text(
//                                        text = benefit.name,
//                                        style = MaterialTheme.typography.titleSmall,
//                                        fontWeight = FontWeight.Bold,
//                                        color = Color(0xFF0F172A)
//                                    )
//                                    benefit.type?.let { type ->
//                                        Surface(
//                                            shape = RoundedCornerShape(999.dp),
//                                            color = palette.badgeBg
//                                        ) {
//                                            Text(
//                                                text = type,
//                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
//                                                style = MaterialTheme.typography.labelSmall,
//                                                color = palette.accent,
//                                                fontWeight = FontWeight.Bold
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//
//                            if (benefit.description.isNotBlank()) {
//                                Text(benefit.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF334155))
//                            }
//
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Text(
//                                    text = "Số lượng: ${benefit.quantity ?: "Không giới hạn"}",
//                                    style = MaterialTheme.typography.labelMedium,
//                                    color = Color(0xFF334155)
//                                )
//                                Surface(
//                                    shape = RoundedCornerShape(999.dp),
//                                    color = Color(0xFFDCFCE7)
//                                ) {
//                                    Text(
//                                        text = "Được bao gồm",
//                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
//                                        style = MaterialTheme.typography.labelSmall,
//                                        color = Color(0xFF15803D),
//                                        fontWeight = FontWeight.SemiBold
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//}
//private fun formatDistanceKm(value: Double?): String {
//    if (value == null || !value.isFinite()) return "Chưa cập nhật"
//    val formatted = String.format(Locale("vi", "VN"), "%.2f", value)
//        .trimEnd('0')
//        .trimEnd(',')
//        .trimEnd('.')
//    return "$formatted km"
//}