//package com.project.cruise.android.ui.screens.passenger
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import com.project.cruise.android.ui.theme.*
//import com.project.cruise.android.viewmodel.passenger.PassengerCatalogState
//import com.project.cruise.android.viewmodel.passenger.PassengerCatalogViewModel
//
//@Composable
//fun TourListScreen(viewModel: PassengerCatalogViewModel, onBack: () -> Unit, onTourClick: (String) -> Unit) {
//    val state by viewModel.state.collectAsState()
//    LaunchedEffect(Unit) { viewModel.loadTours() }
//    TourListContent(state, onBack, onTourClick, viewModel::loadTours)
//}
//
//@Composable
//internal fun TourListContent(state: PassengerCatalogState, onBack: () -> Unit, onTourClick: (String) -> Unit, retry: () -> Unit) {
//    TourPage("Quay lại Dashboard", onBack) {
//        item { OceanBanner("KHÁM PHÁ HÀNH TRÌNH", "Tour đang mở bán", "Chọn chuyến đi, bắt đầu kỳ nghỉ trên biển.") }
//        tourLoadingAndError(state.loading, state.error, retry)
//        if (state.tours.isNotEmpty()) item { TourSection("${state.tours.size} hành trình dành cho bạn") }
//        items(state.tours, key = { it.id }) { tour ->
//            TourCard {
//                TourHeroImage(tour.cruiseImageUrl, tour.name)
//                Text(tour.cruiseName, color = OceanTeal, style = MaterialTheme.typography.labelLarge)
//                Text(tour.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
//                Text("${tour.startDate} → ${tour.endDate}", color = MaterialTheme.colorScheme.onSurfaceVariant)
//                tour.description?.takeIf { it.isNotBlank() }?.let { Text(it, maxLines = 3, overflow = TextOverflow.Ellipsis) }
//                OceanPrimaryButton("Khám phá hành trình", { onTourClick(tour.id) })
//            }
//        }
//        if (!state.loading && state.error == null && state.tours.isEmpty()) item {
//            OceanStatePanel("Chưa có tour đang mở bán", "Hãy quay lại sau để khám phá hành trình mới.", "⚓")
//        }
//    }
//}
