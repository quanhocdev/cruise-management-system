package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.network.ShoreVisitTourResponse
import com.project.cruise.android.viewmodel.pos.ShoreVisitState

@Composable
fun ShoreVisitScreen(state: ShoreVisitState, onRetry: () -> Unit, onBack: () -> Unit) {
    val role = PosRole.SHORE
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    val selected = state.visitTours.firstOrNull { it.id == selectedId }
    PosTheme {
        Box(Modifier.fillMaxSize().background(PosBackground).safeDrawingPadding().imePadding(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(Modifier.widthIn(max = 640.dp).fillMaxWidth(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item { PosTopBar("Tham quan bờ", role, onBack) }
                state.qrValue?.let { code -> item {
                    PosPanel {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PosGlyph(PosSymbol.QR, role.accent(), Modifier.size(30.dp))
                            Column(Modifier.weight(1f)) { Text("Mã khách tham quan", fontWeight = FontWeight.Bold); Text(posDisplayCode(code), color = PosMuted, style = MaterialTheme.typography.bodySmall) }
                            PosBadge("Đã lưu", role.accent())
                        }
                        Text("Chưa xác nhận đăng ký hoặc điểm danh", color = PosAmber, style = MaterialTheme.typography.bodySmall)
                    }
                } }
                when {
                    state.loading -> item { PosPanel { CircularProgressIndicator(color = role.accent()); Text("Đang tải chuyến tham quan…", color = PosMuted) } }
                    state.error != null -> item { PosPanel { Text("Không tải được chuyến tham quan", fontWeight = FontWeight.Bold); Text(state.error, color = PosMuted); PosPrimaryButton("Thử lại", role, onRetry) } }
                    else -> {
                        selected?.let { tour -> item {
                            Surface(color = role.accent(), shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("CHUYẾN ĐANG ĐỐI CHIẾU", color = Color.White.copy(alpha=.8f), style = MaterialTheme.typography.labelSmall)
                                    Text(tour.name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                                    Text(visitTime(tour), color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        } }
                        item { Text("Chọn chuyến tham quan", style = MaterialTheme.typography.titleLarge) }
                        item { OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp), placeholder = { Text("Tìm chuyến tham quan") }, leadingIcon = { PosGlyph(PosSymbol.SEARCH, PosMuted) }) }
                        val filtered = state.visitTours.filter { it.name.contains(query, ignoreCase = true) }
                        if(filtered.isEmpty()) item { PosEmptyState("Chưa có chuyến phù hợp", "Thử tìm với tên khác hoặc tải lại danh sách.", PosSymbol.COMPASS) }
                        items(filtered, key = { it.id }) { tour -> ShoreTourCard(tour, selectedId == tour.id) { selectedId = tour.id } }
                        item {
                            PosPanel {
                                Text("Đối chiếu hành khách", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                PosDetailRow("Đăng ký tham quan", "Chưa được xác minh")
                                PosDetailRow("Trạng thái đi / về", "Chưa có kết quả điểm danh")
                                PosNotice("Chỉ lưu mã và xem chuyến tham quan. Chưa thể xác nhận khách rời tàu hoặc trở về.", warning = true)
                                PosPrimaryButton("Xác nhận khách rời tàu", role, {}, enabled = false)
                                OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Xác nhận khách đã trở về") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShoreTourCard(tour: ShoreVisitTourResponse, selected: Boolean, onSelect: () -> Unit) {
    val accent = PosRole.SHORE.accent()
    Surface(Modifier.fillMaxWidth().selectable(selected, role = Role.RadioButton, onClick = onSelect),
        color = if(selected) Color(0xFFEBF5EF) else Color.White, shape = RoundedCornerShape(22.dp), border = BorderStroke(if(selected) 2.dp else 1.dp, if(selected) accent else PosLine)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PosGlyph(PosSymbol.COMPASS, accent)
                Text(tour.name, Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                RadioButton(selected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = accent))
            }
            Text(visitTime(tour), color = accent, style = MaterialTheme.typography.labelLarge)
            tour.description?.takeIf { it.isNotBlank() }?.let { Text(it, color = PosMuted, style = MaterialTheme.typography.bodySmall) }
            tour.maxPassengers?.let { Text("Sức chứa tối đa · $it khách", color = PosMuted, style = MaterialTheme.typography.bodySmall) }
        }
    }
}
private fun visitTime(tour: ShoreVisitTourResponse): String {
    fun parse(value: String?) = value?.let { runCatching { java.time.LocalDateTime.parse(it) }.getOrNull() }
    val start = parse(tour.startTime)
    val end = parse(tour.endTime)
    val full = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm")
    val time = java.time.format.DateTimeFormatter.ofPattern("HH:mm")
    if (start != null && end != null) {
        return "${start.format(full)} – ${end.format(if(start.toLocalDate() == end.toLocalDate()) time else full)}"
    }
    return listOfNotNull(tour.startTime?.replace('T',' ')?.take(16), tour.endTime?.replace('T',' ')?.take(16))
        .joinToString(" → ").ifBlank { "Chưa có thời gian" }
}
