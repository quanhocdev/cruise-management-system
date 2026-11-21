package com.project.cruise.android.ui.passenger.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.passenger.tripdetail.BookedTourItemDto
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.OceanMint
import com.project.cruise.android.ui.theme.OceanNavy
import com.project.cruise.android.viewmodel.passenger.PassengerBookedToursViewModel

@Composable
fun PassengerBookedToursScreen(
    viewModel: PassengerBookedToursViewModel,
    onOpenTour: (tourId: String, bookingId: Long) -> Unit,
    onHomeClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    unreadCount: Long = 0L
) {
    val state by viewModel.uiState.collectAsState()

    PassengerPage(
        title = "Hành trình của tôi",
        subtitle = "Theo dõi vị trí tàu, lịch trình và quyền lợi của các chuyến đi đã đặt",
        bottomBar = {
            OceanBottomBar(
                isLoggedIn = true,
                currentRoute = "vessel_tracking",
                unreadCount = unreadCount,
                onHomeClick = onHomeClick,
                onLoginClick = {},
                onUserClick = onUserClick,
                onMyBookingsClick = onMyBookingsClick,
                onMessagesClick = onMessagesClick,
                onNotificationClick = onNotificationClick,
                onVesselTrackingClick = {}
            )
        }
    ) {
        when {
            state.loading -> {
                item { PassengerLoading() }
            }

            state.error != null -> {
                item {
                    PassengerError(
                        message = state.error ?: "Không thể tải danh sách hành trình.",
                        onRetry = { viewModel.loadBookedTours() }
                    )
                }
            }

            state.tours.isEmpty() -> {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Chưa có hành trình nào",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Bạn chưa có tour nào đã xác nhận. Hành trình sẽ hiện ở đây sau khi đặt tour thành công.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            else -> {
                items(
                    items = state.tours,
                    key = { "${it.tourId}_${it.bookingId}" }
                ) { tour ->
                    BookedTourCard(
                        tour = tour,
                        onOpen = {
                            val tId = tour.tourId
                            val bId = tour.bookingId
                            if (!tId.isNullOrBlank() && bId != null && bId > 0L) {
                                onOpenTour(tId, bId)
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun tripStatusDisplay(status: String?): Triple<String, Color, Color> =
    when (status?.uppercase()) {
        "IN_PROGRESS", "ONGOING", "RUNNING" ->
            Triple("Đang khởi hành", Color(0xFFDCFCE7), Color(0xFF15803D))
        "SCHEDULED", "UPCOMING", "CONFIRMED" ->
            Triple("Sắp khởi hành", Color(0xFFE0F2FE), Color(0xFF0284C7))
        "COMPLETED", "FINISHED" ->
            Triple("Đã hoàn thành", Color(0xFFF1F5F9), Color(0xFF475569))
        "CANCELLED" ->
            Triple("Đã hủy", Color(0xFFFEE2E2), Color(0xFFB91C1C))
        else ->
            Triple(status ?: "Đã đặt", Color(0xFFE0F2FE), Color(0xFF0284C7))
    }

@Composable
private fun BookedTourCard(
    tour: BookedTourItemDto,
    onOpen: () -> Unit
) {
    val (statusLabel, badgeBg, badgeText) = tripStatusDisplay(tour.statusTrip)
    val canOpen = !tour.tourId.isNullOrBlank() && (tour.bookingId ?: 0L) > 0L

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tour.tourCode?.takeIf { it.isNotBlank() }?.let { code ->
                    Text(
                        text = code,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7)
                    )
                } ?: Spacer(modifier = Modifier.width(1.dp))

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = statusLabel,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeText
                    )
                }
            }

            Text(
                text = tour.tourName ?: "Chuyến du thuyền",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            val dep = tour.departurePortName
            val arr = tour.arrivalPortName
            if (!dep.isNullOrBlank() || !arr.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${dep ?: "Cảng đi"} → ${arr ?: "Cảng đến"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF334155)
                    )
                }
            }

            val start = tour.resolvedStartDate
            val end = tour.endDate
            if (!start.isNullOrBlank() || !end.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = listOfNotNull(start, end).joinToString(" - "),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )
                }
            }

            Button(
                onClick = onOpen,
                enabled = canOpen,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OceanNavy,
                    contentColor = OceanMint
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Theo dõi hành trình",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}