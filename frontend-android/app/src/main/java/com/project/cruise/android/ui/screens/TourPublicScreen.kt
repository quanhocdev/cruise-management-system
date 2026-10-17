package com.project.cruise.android.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.tour.*
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.auth.*
import com.project.cruise.android.viewmodel.tour.*

@Composable
fun TourPublicScreen(
    viewModel: TourViewModel,
    authViewModel: AuthViewModel,
    onTourClick: (String) -> Unit,
    onLoginClick: () -> Unit,
    onUserClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onHomeClick: () -> Unit = {},
) {
    val state by viewModel.tourListState.collectAsState()
    val session by authViewModel.sessionState.collectAsState()
    LaunchedEffect(Unit) { viewModel.fetchPublicTours() }
    val loggedIn = session is SessionState.Authenticated
    TourCatalogContent(
        state,
        onTourClick,
        { viewModel.fetchPublicTours() },
        bottomBar = {
            OceanBottomBar(
                loggedIn,
                "passenger_tours",
                onHomeClick,
                onLoginClick,
                onUserClick,
                onMyBookingsClick = onMyBookingsClick,
            )
        },
    )
}

@Composable
fun TourCatalogContent(
    state: TourListState,
    onTourClick: (String) -> Unit,
    onRetry: () -> Unit,
    bottomBar: @Composable () -> Unit = {},
) {
    var query by rememberSaveable { mutableStateOf("") }
    var openOnly by rememberSaveable { mutableStateOf(false) }
    val tours = (state as? TourListState.Success)?.tours.orEmpty()
    val filtered =
        remember(tours, query, openOnly) {
            tours.filter { tour ->
                (!openOnly || tour.statusBooking == TourBookingStatus.OPEN) &&
                    (query.isBlank() ||
                        listOf(tour.name, tour.code, tour.cruiseName).any {
                            it?.contains(query.trim(), ignoreCase = true) == true
                        })
            }
        }
    PassengerPage("Khám phá", "OCEANCRUISE · HÀNH TRÌNH TRÊN BIỂN", bottomBar = bottomBar) {
        item { PassengerDiscoveryHero() }
        item {
            OutlinedTextField(
                query,
                { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                placeholder = {
                    Text(
                        "Tìm tour hoặc du thuyền",
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                },
                leadingIcon = { PassengerIcon(PassengerGlyph.SEARCH) },
                trailingIcon = {
                    if (query.isNotEmpty()) TextButton(onClick = { query = "" }) { Text("Xóa") }
                },
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(!openOnly, { openOnly = false }, label = { Text("Tất cả") })
                FilterChip(openOnly, { openOnly = true }, label = { Text("Đang mở bán") })
            }
            PassengerSection(
                "Hành trình dành cho bạn",
                if (state is TourListState.Success) "${filtered.size} hành trình phù hợp"
                else "Khám phá những chuyến đi mới",
            )
        }
        when (state) {
            TourListState.Idle,
            TourListState.Loading -> item { PassengerLoading() }
            is TourListState.Error -> item { PassengerError(state.message, onRetry) }
            is TourListState.Success -> {
                if (filtered.isEmpty())
                    item {
                        PassengerEmpty(
                            "Chưa có hành trình phù hợp",
                            if (tours.isEmpty()) "Các chuyến đi sẽ xuất hiện khi được công bố."
                            else "Thử tên khác hoặc xem tất cả hành trình.",
                            if (tours.isEmpty()) "Tải lại" else "Xóa bộ lọc",
                            {
                                if (tours.isEmpty()) onRetry()
                                else {
                                    query = ""
                                    openOnly = false
                                }
                            },
                        )
                    }
                items(filtered, key = { it.id }) { tour ->
                    TourSummaryCard(tour) { onTourClick(tour.id) }
                }
            }
        }
    }
}

@Composable
fun TourSummaryCard(tour: PublicTourSummaryResponse, onClick: () -> Unit) {
    PassengerCard(Modifier.clickable(onClick = onClick)) {
        CruiseVisual(tour.cruiseImageUrl)
        PassengerPill(
            tourSaleLabel(tour.statusBooking),
            tour.statusBooking == TourBookingStatus.OPEN,
        )
        Text(
            tour.name ?: "Hành trình ${tour.code.orEmpty()}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        Text(
            tour.cruiseName ?: "Du thuyền chưa cập nhật",
            color = OceanSlate,
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            PassengerIcon(PassengerGlyph.CALENDAR, modifier = Modifier.size(20.dp))
            Text(
                "${passengerDate(tour.startDate)} – ${passengerDate(tour.endDate)}",
                color = OceanSlate,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        HorizontalDivider(color = OceanLine)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Giá từ", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                Text(
                    passengerMoney(tour.startingPrice),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OceanTeal,
                )
            }
            PassengerIcon(PassengerGlyph.ARROW)
        }
    }
}
