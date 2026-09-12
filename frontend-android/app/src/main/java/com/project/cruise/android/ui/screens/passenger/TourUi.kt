//package com.project.cruise.android.ui.screens.passenger
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.background
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyListScope
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.layout.ContentScale
//import coil.compose.AsyncImage
//import com.project.cruise.android.ui.theme.*
//
//@Composable
//internal fun TourPage(backLabel: String, onBack: () -> Unit, content: LazyListScope.() -> Unit) {
//    OceanTheme {
//        Surface(Modifier.fillMaxSize(), color = OceanMist) {
//            Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 20.dp)) {
//                FilledTonalButton(onClick = onBack,
//                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).heightIn(min = 56.dp)) {
//                    Text("← $backLabel", style = MaterialTheme.typography.titleMedium)
//                }
//                LazyColumn(Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(bottom = 24.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
//            }
//        }
//    }
//}
//
//@Composable
//internal fun TourCard(content: @Composable ColumnScope.() -> Unit) {
//    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
//        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
//    }
//}
//
//@Composable
//internal fun TourHeroImage(url: String?, description: String) {
//    Box(
//        Modifier.fillMaxWidth().height(180.dp).background(OceanMintSoft, RoundedCornerShape(20.dp)),
//        contentAlignment = androidx.compose.ui.Alignment.Center
//    ) {
//        if (url.isNullOrBlank()) {
//            ShipEmblem(Modifier.size(76.dp))
//        } else {
//            AsyncImage(
//                model = url,
//                contentDescription = description,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//        }
//    }
//}
//
//@Composable
//internal fun TourSection(title: String) {
//    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OceanNavy)
//}
//
//internal fun LazyListScope.tourLoadingAndError(loading: Boolean, error: String?, retry: () -> Unit) {
//    if (loading) item {
//        LinearProgressIndicator(Modifier.fillMaxWidth())
//        OceanStatePanel("Đang chuẩn bị hành trình", "Dữ liệu mới nhất đang được tải từ máy chủ.", "≈")
//    }
//    if (error != null) item {
//        OceanStatePanel("Không tải được dữ liệu", error, "!", "Thử lại", retry, error = true)
//    }
//}
