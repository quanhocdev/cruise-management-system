package com.project.cruise.android.ui.passenger.components.mybookingdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.passenger.tripdetail.*
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.PassengerTripDetailUiState

/** Tab "Quyền lợi": [mode] 0 = quyền lợi của gói, 1 = hàng/dịch vụ đã dùng. */
fun LazyListScope.benefitsUsageTab(
    state: PassengerTripDetailUiState,
    mode: Int,
    onMode: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    item(key = "u-mode") { SegmentedChips(listOf("Quyền lợi gói", "Đã sử dụng"), mode, onMode) }
    if (mode == 0) benefitsContent(state, onRetry) else usageContent(state, onRetry)
}

private data class BenefitPalette(
    val accent: Color,
    val cardBg: Color,
    val border: Color,
    val badgeBg: Color,
    val icon: ImageVector,
)

private fun benefitPalette(type: String?): BenefitPalette {
    val t = type.orEmpty()
    return when {
        t.contains("PRODUCT", true) || t.contains("FOOD", true) ->
            BenefitPalette(OceanGoldDeep, OceanGoldSoft, OceanGold, Color(0xFFFFE9A8), Icons.Filled.ShoppingCart)
        t.contains("SERVICE", true) || t.contains("RESTAURANT", true) ->
            BenefitPalette(Color(0xFF0D9488), Color(0xFFF0FDFA), Color(0xFF99F6E4), Color(0xFFCCFBF1), Icons.Filled.Star)
        t.contains("ACTIVITY", true) || t.contains("CRUISE", true) ->
            BenefitPalette(Color(0xFF4F46E5), Color(0xFFEEF2FF), Color(0xFFC7D2FE), Color(0xFFE0E7FF), Icons.Filled.CheckCircle)
        t.contains("VISIT", true) || t.contains("SHORE", true) ->
            BenefitPalette(Color(0xFF16A34A), Color(0xFFF0FDF4), Color(0xFFBBF7D0), Color(0xFFDCFCE7), Icons.Filled.Place)
        else ->
            BenefitPalette(Color(0xFF0284C7), Color(0xFFF0F9FF), Color(0xFFBAE6FD), Color(0xFFE0F2FE), Icons.Filled.Star)
    }
}

private fun LazyListScope.benefitsContent(state: PassengerTripDetailUiState, onRetry: () -> Unit) {
    val pkg = state.matchedPackage
    val loading = state.facilitiesLoading || state.bookingLoading
    if (tripLoadGate("u-ben", loading, state.facilitiesError ?: state.bookingError, pkg == null, onRetry)) return
    if (pkg == null) {
        item(key = "u-ben-empty") { PassengerEmpty("Chưa có thông tin gói", "Quyền lợi của gói bạn đã đặt sẽ hiển thị tại đây.") }
        return
    }
    item(key = "u-pkg") {
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                GoldBadge("GÓI TOUR ĐÃ ĐẶT")
                Text(
                    pkg.name ?: "Gói dịch vụ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                pkg.price?.let {
                    Text(
                        tripMoney(it),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = OceanGold,
                    )
                }
                pkg.description?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = .88f))
                }
            }
        }
    }
    item(key = "u-ben-title") { PassengerSection("Quyền lợi đi kèm", "${state.resolvedBenefits.size} quyền lợi trong gói") }
    if (state.resolvedBenefits.isEmpty()) {
        item(key = "u-ben-none") { PassengerEmpty("Gói chưa có quyền lợi", "Hiện chưa có quyền lợi nào được gắn vào gói này.") }
        return
    }
    itemsIndexed(state.resolvedBenefits, key = { index, b -> "u-ben-${b.id}-$index" }) { _, b ->
        val palette = benefitPalette(b.type)
        Surface(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = palette.cardBg,
            border = BorderStroke(1.dp, palette.border),
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(palette.accent),
                        contentAlignment = Alignment.Center,
                    ) { Icon(palette.icon, null, tint = Color.White, modifier = Modifier.size(22.dp)) }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(b.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OceanNavy)
                        b.type?.takeIf { it.isNotBlank() }?.let {
                            Surface(shape = RoundedCornerShape(50), color = palette.badgeBg) {
                                Text(
                                    it,
                                    Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.accent,
                                )
                            }
                        }
                    }
                }
                if (b.description.isNotBlank()) {
                    Text(b.description, style = MaterialTheme.typography.bodySmall, color = OceanNavy.copy(alpha = .8f))
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Số lượng: ${b.quantity ?: "Không giới hạn"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = OceanNavy,
                    )
                    GoldBadge("Được bao gồm")
                }
            }
        }
    }
}

private fun LazyListScope.usageContent(state: PassengerTripDetailUiState, onRetry: () -> Unit) {
    val groups = listOf(
        "Sản phẩm" to state.productUsages,
        "Dịch vụ tiện ích" to state.serviceUsages,
        "Hoạt động trên tàu" to state.activityCruiseUsages,
        "Tham quan bờ" to state.visitUsages,
    )
    val all = groups.flatMap { it.second }
    if (tripLoadGate("u-use", state.usagesLoading, state.usagesError, all.isEmpty(), onRetry)) return
    if (all.isEmpty()) {
        item(key = "u-use-empty") { PassengerEmpty("Chưa có lượt sử dụng", "Các hàng hóa, dịch vụ bạn đã dùng sẽ hiển thị tại đây.") }
        return
    }
    item(key = "u-total") {
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(OceanNavy, OceanTeal)))
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Tổng đã sử dụng", style = MaterialTheme.typography.labelMedium, color = OceanMint)
                Text(
                    tripMoney(all.sumOf { it.totalPrice ?: 0.0 }),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanGold,
                )
                Text("${all.size} lượt sử dụng", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = .85f))
            }
        }
    }
    groups.filter { it.second.isNotEmpty() }.forEach { (title, list) ->
        item(key = "u-group-$title") {
            ExpandableSection(
                title,
                subtitle = tripMoney(list.sumOf { it.totalPrice ?: 0.0 }),
                trailing = "${list.size} lượt",
            ) { list.forEach { UsageRow(it) } }
        }
    }
}

@Composable
private fun UsageRow(u: TripUsageItemDto) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                (u.itemName ?: u.name ?: "Mục sử dụng") + (u.quantity?.let { " × $it" } ?: ""),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = OceanNavy,
            )
            val meta = listOfNotNull(
                u.usedAt?.takeIf { it.isNotBlank() }?.let { "${passengerDate(it)} ${passengerTime(it)}" },
                u.status?.takeIf { it.isNotBlank() },
            ).joinToString(" · ")
            if (meta.isNotBlank()) Text(meta, style = MaterialTheme.typography.labelSmall, color = OceanSlate)
        }
        u.totalPrice?.let {
            Text(tripMoney(it), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = OceanGoldDeep)
        }
    }
}

fun LazyListScope.billsTab(state: PassengerTripDetailUiState, onRetry: () -> Unit) {
    if (tripLoadGate("bill", state.billsLoading, state.billsError, state.bills.isEmpty(), onRetry)) return
    if (state.bills.isEmpty()) {
        item(key = "bill-empty") { PassengerEmpty("Chưa có hóa đơn", "Hóa đơn dịch vụ và biên nhận sẽ hiển thị tại đây.") }
        return
    }
    item(key = "bill-total") {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile("Tổng hóa đơn", tripMoney(state.bills.sumOf { it.totalAmount ?: 0.0 }), Modifier.weight(1.6f), highlight = true)
            StatTile("Số hóa đơn", state.bills.size.toString(), Modifier.weight(1f))
        }
    }
    itemsIndexed(state.bills, key = { index, bill -> "bill-${bill.id ?: index}" }) { index, bill ->
        BillCard(index, bill)
    }
}

@Composable
private fun BillCard(index: Int, bill: TripBillDto) {
    PassengerCard {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                bill.billCode ?: bill.code ?: "Hóa đơn #${index + 1}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
            )
            bill.status?.takeIf { it.isNotBlank() }?.let { PassengerPill(it, billTone(it)) }
        }
        Text(
            tripMoney(bill.totalAmount),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = OceanGoldDeep,
        )
        MetaLine("Ngày tạo", bill.createdAt?.takeIf { it.isNotBlank() }?.let { passengerDate(it) })
        MetaLine("Thanh toán lúc", bill.paidAt?.takeIf { it.isNotBlank() }?.let { "${passengerDate(it)} ${passengerTime(it)}" })
    }
}

private fun billTone(status: String): PillTone =
    when (status.uppercase()) {
        "PAID", "COMPLETED", "SUCCESS" -> PillTone.POSITIVE
        "UNPAID", "PENDING" -> PillTone.WARNING
        "CANCELLED", "FAILED", "REFUNDED" -> PillTone.DANGER
        else -> PillTone.NEUTRAL
    }