package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.viewmodel.pos.ConveniencePosState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ConvenienceCustomerScreen(state: ConveniencePosState, onRetry: () -> Unit, onBack: () -> Unit) {
    val role = PosRole.CONVENIENCE
    var services by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    PosTheme {
        Box(Modifier.fillMaxSize().background(PosBackground).safeDrawingPadding().imePadding(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(Modifier.widthIn(max = 640.dp).fillMaxWidth(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item { PosTopBar("Tiện ích cho khách", role, onBack) }
                state.nfcUid?.let { uid -> item {
                    PosPanel {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PosGlyph(PosSymbol.NFC, role.accent(), Modifier.size(32.dp))
                            Column(Modifier.weight(1f)) { Text("Vòng tay đã đọc", fontWeight = FontWeight.Bold); Text(posDisplayCode(uid), color = PosMuted, style = MaterialTheme.typography.bodySmall) }
                            PosBadge("Đã lưu", role.accent())
                        }
                        PosNotice("Chưa xác minh được hành khách. Danh mục dưới đây dùng để tham khảo, chưa ghi nhận sử dụng.", warning = true)
                    }
                } }
                when {
                    state.loading -> item { PosPanel { CircularProgressIndicator(color = role.accent()); Text("Đang tải tiện ích…", color = PosMuted) } }
                    state.error != null -> item { PosPanel { Text("Không tải được tiện ích", style = MaterialTheme.typography.titleMedium); Text(state.error, color = PosMuted); PosPrimaryButton("Thử lại", role, onRetry) } }
                    else -> {
                        item {
                            Text("Danh mục tiện ích", style = MaterialTheme.typography.titleLarge)
                            Text("Giá và thông tin được cung cấp bởi hệ thống", color = PosMuted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 5.dp))
                        }
                        item {
                            OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp),
                                placeholder = { Text("Tìm sản phẩm, dịch vụ") }, leadingIcon = { PosGlyph(PosSymbol.SEARCH, PosMuted) })
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                FilterChip(!services, { services = false }, label = { Text("Sản phẩm · ${state.products.size}") })
                                FilterChip(services, { services = true }, label = { Text("Dịch vụ · ${state.services.size}") })
                            }
                        }
                        if(services) {
                            val filtered = state.services.filter { it.name.contains(query, ignoreCase = true) }
                            if(filtered.isEmpty()) item { PosEmptyState("Chưa có dịch vụ phù hợp", "Thử tìm với tên khác.", PosSymbol.BAG) }
                            items(filtered, key = { it.id }) { item ->
                                CatalogCard(item.name, item.description, formatVnd(item.price), listOfNotNull(item.durationMinutes?.let { "$it phút" }, item.maxPassengers?.let { "Tối đa $it khách" }).joinToString(" · "))
                            }
                        } else {
                            val filtered = state.products.filter { it.name.contains(query, ignoreCase = true) }
                            if(filtered.isEmpty()) item { PosEmptyState("Chưa có sản phẩm phù hợp", "Thử tìm với tên khác.", PosSymbol.BAG) }
                            items(filtered, key = { it.id }) { item -> CatalogCard(item.name, item.description, formatVnd(item.price), item.stockQuantity?.let { "Số lượng danh mục: $it" }.orEmpty()) }
                        }
                        item { PosNotice("Chỉ xem danh mục. Chưa xác nhận quyền lợi hoặc phát sinh chi phí cho khách.") }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogCard(name: String, description: String?, price: String, metadata: String) {
    PosPanel {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(48.dp).background(PosRole.CONVENIENCE.accent().copy(alpha=.08f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { PosGlyph(PosSymbol.BAG, PosRole.CONVENIENCE.accent()) }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(price, color = PosRole.CONVENIENCE.accent(), fontWeight = FontWeight.Bold)
                description?.takeIf { it.isNotBlank() }?.let { Text(it, color = PosMuted, style = MaterialTheme.typography.bodySmall) }
                if(metadata.isNotBlank()) Text(metadata, color = PosMuted, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
private fun formatVnd(value: Double?): String = value?.let { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(it) } ?: "Chưa có giá"
