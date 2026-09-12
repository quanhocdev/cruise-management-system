//package com.project.cruise.android.ui.screens.passenger
//
//import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Dialog
//import androidx.compose.ui.window.DialogProperties
//import coil.compose.SubcomposeAsyncImage
//import com.project.cruise.android.viewmodel.passenger.BookingHistoryState
//import com.project.cruise.android.ui.theme.*
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//
//@Composable
//fun BookingCatalogDialog(state: BookingHistoryState, onRetry: () -> Unit, onClose: () -> Unit) {
//    var group by rememberSaveable { mutableStateOf("SERVICE") }
//    var selected by rememberSaveable { mutableStateOf<String?>(null) }
//    val catalog = state.trip?.catalog
//    val detail = catalog?.firstOrNull { "${it.type}:${it.id}" == selected }
//    val back = { if (selected != null) selected = null else onClose() }
//    Dialog(onDismissRequest = back, properties = DialogProperties(usePlatformDefaultWidth = false)) {
//        BackHandler(enabled = selected != null, onBack = back)
//        TripSurface {
//            Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding().padding(20.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                TripBackButton(if (selected == null) "← Chi tiết booking" else "← Danh sách", back)
//                Text(if (selected == null) "Dịch vụ & sản phẩm" else "Chi tiết", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = OceanNavy)
//                when {
//                    state.loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
//                    state.error != null || state.tripError != null || catalog == null ->
//                        OceanNotice("Chưa tải được danh mục. Vui lòng thử lại.", true)
//                    selected != null && detail == null -> Text("Mục này không còn trong danh mục của chuyến.")
//                    detail != null -> LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                        item { CatalogImage(detail.imageUrl, detail.name) }
//                        item { Text(detail.name, style = MaterialTheme.typography.titleLarge) }
//                        item { Text(detail.description?.takeIf { it.isNotBlank() } ?: "Chưa có mô tả.") }
//                        item { Text("Giá: ${activityPrice(detail.price)}", color = OceanTeal, style = MaterialTheme.typography.titleLarge) }
//                        item { Text("Trạng thái đồng bộ: ${catalogStatus(detail.status)}") }
//                        item { Text("Khu vực: ${detail.location?.takeIf { it.isNotBlank() } ?: "Đang cập nhật"}") }
//                        if (detail.type == "SERVICE") {
//                            item { Text("Thời lượng: ${catalogDuration(detail.durationMinutes)}") }
//                            item { Text("Số khách tối đa: ${detail.maxPassengers?.takeIf { it > 0 } ?: "Đang cập nhật"}") }
//                        }
//                        item { Text("Thông tin đồng bộ theo chuyến; chưa phản ánh tồn kho hoặc chỗ trống hiện tại.") }
//                        state.detail?.let { booking ->
//                            item { CatalogOrderDraftForm(detail, booking) }
//                        }
//                    }
//                    else -> {
//                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                            FilterChip(selected = group == "SERVICE", onClick = { group = "SERVICE" }, label = { Text("Dịch vụ") })
//                            FilterChip(selected = group == "PRODUCT", onClick = { group = "PRODUCT" }, label = { Text("Sản phẩm") })
//                        }
//                        val visible = catalog.filter { it.type == group }
//                        if (visible.isEmpty()) Text("Chưa có ${if (group == "SERVICE") "dịch vụ" else "sản phẩm"} được cấu hình cho chuyến này.")
//                        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                            items(visible, key = { "${it.type}:${it.id}" }) { entry ->
//                                OutlinedCard(onClick = { selected = "${entry.type}:${entry.id}" }, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.outlinedCardColors(containerColor = Color.White)) {
//                                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                                        CatalogImage(entry.imageUrl, entry.name)
//                                        Text(entry.name, style = MaterialTheme.typography.titleMedium)
//                                        Text(activityPrice(entry.price), color = OceanTeal, fontWeight = FontWeight.Bold)
//                                        Text(catalogStatus(entry.status))
//                                        Text("Xem chi tiết", color = OceanTeal)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                Text("Có thể soạn bản nháp; chưa gửi đơn hoặc thanh toán.", style = MaterialTheme.typography.bodySmall)
//                OutlinedButton(onClick = onRetry, enabled = !state.loading, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Tải lại danh mục") }
//            }
//        }
//    }
//}
//
//@Composable
//private fun CatalogImage(url: String?, name: String) {
//    val safeUrl = catalogImageUrl(url)
//    if (safeUrl == null) {
//        Text("Chưa có ảnh", style = MaterialTheme.typography.bodySmall)
//    } else {
//        // Coil has its own unauthenticated client: do not forward the passenger JWT to image hosts.
//        SubcomposeAsyncImage(model = safeUrl, contentDescription = name,
//            modifier = Modifier.fillMaxWidth().height(160.dp), contentScale = ContentScale.Crop,
//            loading = { Box(Modifier.padding(16.dp)) { Text("Đang tải ảnh…") } },
//            error = { Box(Modifier.padding(16.dp)) { Text("Không tải được ảnh") } })
//    }
//}
