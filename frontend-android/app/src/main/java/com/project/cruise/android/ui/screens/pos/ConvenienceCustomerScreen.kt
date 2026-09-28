package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.network.ConvenienceProductResponse
import com.project.cruise.android.data.network.ConvenienceServiceResponse
import com.project.cruise.android.viewmodel.pos.ConveniencePosState
import java.text.NumberFormat
import java.util.Locale

private enum class ConvenienceCatalogTab { PRODUCTS, SERVICES }

@Composable
fun ConvenienceCustomerScreen(
    state: ConveniencePosState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(ConvenienceCatalogTab.PRODUCTS) }
    PosTheme {
        Column(
            Modifier.fillMaxSize().background(PosBackground).safeDrawingPadding().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onBack) { Text("← Về POS tiện ích") }
            PosBadge(PosRole.CONVENIENCE.title, PosRole.CONVENIENCE.accent())
            Text("Nhận diện và danh mục", style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = PosInk)

            when {
                state.loading -> {
                    NfcIdentityPanel(state.nfcUid)
                    ConvenienceLoading()
                }
                state.error != null -> {
                    NfcIdentityPanel(state.nfcUid)
                    ConvenienceError(state.error, onRetry)
                }
                else -> {
                    NfcIdentityPanel(state.nfcUid)

                    TabRow(selectedTabIndex = selectedTab.ordinal, containerColor = Color.Transparent) {
                        Tab(selected = selectedTab == ConvenienceCatalogTab.PRODUCTS,
                            onClick = { selectedTab = ConvenienceCatalogTab.PRODUCTS },
                            text = { Text("Sản phẩm (${state.products.size})") })
                        Tab(selected = selectedTab == ConvenienceCatalogTab.SERVICES,
                            onClick = { selectedTab = ConvenienceCatalogTab.SERVICES },
                            text = { Text("Dịch vụ (${state.services.size})") })
                    }

                    val empty = selectedTab == ConvenienceCatalogTab.PRODUCTS && state.products.isEmpty() ||
                        selectedTab == ConvenienceCatalogTab.SERVICES && state.services.isEmpty()
                    if (empty) {
                        PosPanel { Text("Chưa có dữ liệu đang hoạt động.", color = PosMuted) }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 12.dp)
                        ) {
                            if (selectedTab == ConvenienceCatalogTab.PRODUCTS) {
                                items(state.products, key = { it.id }) { ProductCard(it) }
                            } else {
                                items(state.services, key = { it.id }) { ServiceCard(it) }
                            }
                        }
                    }
                    Surface(color = Color(0xFFFFEED0), shape = MaterialTheme.shapes.medium) {
                        Text(
                            "Chỉ xem danh mục. Chưa thể ghi nhận sử dụng hoặc phát sinh phí cho tới khi backend xác minh khách và cung cấp API giao dịch.",
                            Modifier.padding(14.dp), color = Color(0xFF745014),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NfcIdentityPanel(uid: String?) {
    if (uid == null) return
    PosPanel {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Vòng NFC", fontWeight = FontWeight.Bold, color = PosInk)
            PosBadge("Chưa xác minh", Color(0xFF995417))
        }
        Text(maskUid(uid), color = PosInk, style = MaterialTheme.typography.titleMedium)
        Text(
            "Backend hiện chưa có API tra hành khách theo UID NFC. POS chưa thể hiển thị tên khách, phòng, chuyến hoặc quyền lợi.",
            color = PosMuted,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ConvenienceLoading() {
    PosPanel {
        CircularProgressIndicator(color = PosRole.CONVENIENCE.accent())
        Text("Đang tải danh mục tiện ích…", color = PosMuted)
    }
}

@Composable
private fun ConvenienceError(message: String, onRetry: () -> Unit) {
    PosPanel {
        Text("Không tải được dữ liệu", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
        Text(message, color = PosMuted)
        Button(onClick = onRetry) { Text("Thử lại") }
    }
}

@Composable
private fun ProductCard(item: ConvenienceProductResponse) {
    CatalogCard(item.name, item.description, formatVnd(item.price)) {
        item.stockQuantity?.let { Text("Số lượng trong danh mục: $it", color = PosMuted,
            style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
private fun ServiceCard(item: ConvenienceServiceResponse) {
    CatalogCard(item.name, item.description, formatVnd(item.price)) {
        val metadata = listOfNotNull(
            item.durationMinutes?.let { "$it phút" },
            item.maxPassengers?.let { "Tối đa $it khách" }
        ).joinToString(" · ")
        if (metadata.isNotBlank()) Text(metadata, color = PosMuted,
            style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun CatalogCard(
    name: String,
    description: String?,
    price: String,
    metadata: @Composable ColumnScope.() -> Unit
) {
    Surface(Modifier.fillMaxWidth(), color = Color.White, shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name, Modifier.weight(1f), fontWeight = FontWeight.Bold, color = PosInk)
                Text(price, color = PosRole.CONVENIENCE.accent(), fontWeight = FontWeight.Bold)
            }
            description?.takeIf { it.isNotBlank() }?.let {
                Text(it, color = PosMuted, style = MaterialTheme.typography.bodySmall)
            }
            metadata()
        }
    }
}

private fun formatVnd(value: Double?): String = value?.let {
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN")).format(it)
} ?: "Chưa có giá"

private fun maskUid(uid: String): String = when {
    uid.length <= 8 -> uid
    else -> "${uid.take(4)}••••${uid.takeLast(4)}"
}
