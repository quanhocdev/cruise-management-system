package com.project.cruise.android.ui.passenger.components.mybookingdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.booking.BookingStatus
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.TripSectionKey

/** 6 tab của trang chi tiết booking đã thanh toán. [sections] là các mục ViewModel cần tải khi mở tab. */
enum class BookingDetailTab(
    val label: String,
    val icon: ImageVector,
    val sections: List<TripSectionKey>,
) {
    BOOKING("Đặt chỗ", Icons.Filled.Person, listOf(TripSectionKey.BOOKING, TripSectionKey.PASSENGERS)),
    JOURNEY("Hành trình", Icons.Filled.Place, listOf(TripSectionKey.TRACKING, TripSectionKey.ITINERARY)),
    CRUISE("Du thuyền", Icons.Filled.Home, listOf(TripSectionKey.CRUISE)),
    SERVICES("Dịch vụ", Icons.Filled.Star, listOf(TripSectionKey.FACILITIES)),
    USAGE("Quyền lợi", Icons.Filled.ShoppingCart, listOf(TripSectionKey.BENEFITS, TripSectionKey.USAGES)),
    BILLS("Hóa đơn", Icons.Filled.Info, listOf(TripSectionKey.BILLS)),
}

/** Hàng chip tab cuộn ngang: tab đang chọn nền vàng, chữ navy. */
@Composable
fun BookingTabRow(
    selected: BookingDetailTab,
    onSelect: (BookingDetailTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(BookingDetailTab.values().toList(), key = { it.name }) { tab ->
            val on = tab == selected
            val bg by animateColorAsState(if (on) OceanGold else Color.White, label = "tabBg")
            Surface(
                onClick = { onSelect(tab) },
                modifier = Modifier.semantics { this.selected = on },
                shape = RoundedCornerShape(50),
                color = bg,
                border = BorderStroke(1.dp, if (on) OceanGold else OceanLine),
                shadowElevation = if (on) 3.dp else 0.dp,
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(tab.icon, contentDescription = null, tint = OceanNavy, modifier = Modifier.size(16.dp))
                    Text(
                        tab.label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                        color = OceanNavy,
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroChip(
    text: String,
    highlight: Boolean = false,
    leading: @Composable (() -> Unit)? = null,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (highlight) OceanGold else Color.White.copy(alpha = .16f),
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            leading?.invoke()
            Text(
                text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (highlight) OceanNavy else Color.White,
            )
        }
    }
}

/** Hero gọn: mã booking vàng, tên tour, tuyến, ngày, trạng thái chuyến. */
@Composable
fun BookingHeroCompact(
    bookingCode: String,
    status: BookingStatus?,
    tourName: String?,
    route: String?,
    dates: String?,
    tripStatus: String?,
    cruiseName: String?,
    live: Boolean,
    modifier: Modifier = Modifier,
) {
    val phase = tripPhase(tripStatus)
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
    ) {
        Canvas(Modifier.matchParentSize()) {
            for (i in 0..2) {
                val path = Path().apply {
                    moveTo(-20f, size.height * .72f + i * 16f)
                    cubicTo(
                        size.width * .3f, size.height * .5f + i * 16f,
                        size.width * .7f, size.height * .95f + i * 16f,
                        size.width + 20f, size.height * .66f + i * 16f,
                    )
                }
                drawPath(path, OceanMint.copy(alpha = .14f), style = Stroke(2f))
            }
        }
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("MÃ BOOKING", style = MaterialTheme.typography.labelSmall, color = OceanMint)
                    Text(
                        bookingCode,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = OceanGold,
                    )
                }
                PassengerPill(bookingLabel(status), bookingTone(status))
            }
            tourName?.takeIf { it.isNotBlank() }?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                )
            }
            route?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = .9f))
            }
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (!tripStatus.isNullOrBlank()) {
                    HeroChip(
                        tripStatusLabel(tripStatus),
                        highlight = phase == TripPhase.ONGOING,
                        leading = if (live) {
                            { LiveDot(color = if (phase == TripPhase.ONGOING) OceanNavy else OceanGold) }
                        } else null,
                    )
                }
                dates?.takeIf { it.isNotBlank() }?.let { HeroChip(it) }
                cruiseName?.takeIf { it.isNotBlank() }?.let { HeroChip(it) }
            }
        }
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(3.dp).background(OceanGold))
    }
}

/** Card thu gọn / mở rộng: phần "núp", bấm mới hiện nội dung. */
@Composable
fun ExpandableSection(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: String? = null,
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "chevron")
    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(if (expanded) 1.5.dp else 1.dp, if (expanded) OceanGold else OceanLine.copy(alpha = .55f)),
        shadowElevation = 1.dp,
    ) {
        Column {
            Row(
                Modifier.clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OceanNavy,
                    )
                    subtitle?.takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = OceanSlate)
                    }
                }
                trailing?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OceanGoldDeep,
                    )
                }
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Thu gọn" else "Mở rộng",
                    tint = if (expanded) OceanGoldDeep else OceanSlate,
                    modifier = Modifier.rotate(rotation),
                )
            }
            AnimatedVisibility(expanded) {
                Column(
                    Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    content = content,
                )
            }
        }
    }
}

/** Công tắc 2-3 lựa chọn dạng viên thuốc: lựa chọn đang chọn nền vàng. */
@Composable
fun SegmentedChips(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        color = OceanNavy,
    ) {
        Row(Modifier.padding(4.dp)) {
            options.forEachIndexed { index, label ->
                val on = index == selected
                Surface(
                    onClick = { onSelect(index) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    color = if (on) OceanGold else Color.Transparent,
                ) {
                    Text(
                        label,
                        modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (on) OceanNavy else Color.White.copy(alpha = .85f),
                    )
                }
            }
        }
    }
}

/** Dòng "nhãn ... giá trị"; tự ẩn khi giá trị trống. */
@Composable
internal fun MetaLine(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = OceanSlate)
        Text(
            value,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = OceanNavy,
        )
    }
}

internal fun tripMoney(value: Double?): String = passengerMoney(value?.toBigDecimal())

/**
 * Phát item loading/lỗi khi mục chưa có dữ liệu.
 * Trả về true nếu đã phát (caller nên bỏ qua phần nội dung). [key] để key các item không đụng nhau giữa các tab.
 */
internal fun LazyListScope.tripLoadGate(
    key: String,
    loading: Boolean,
    error: String?,
    empty: Boolean,
    onRetry: () -> Unit,
): Boolean {
    if (!empty) return false
    if (loading) {
        item(key = "$key-loading") { PassengerLoading() }
        return true
    }
    if (error != null) {
        item(key = "$key-error") { PassengerError(error, onRetry) }
        return true
    }
    return false
}