package com.project.cruise.android.ui.screens.passenger

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
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.*

@Composable
fun MyBookingsScreen(
    viewModel: BookingViewModel,
    onBookingClick: (Long) -> Unit,
    onBack: () -> Unit,
    onBrowseTours: () -> Unit = onBack,
    onAccount: (() -> Unit)? = null,
) {
    val state by viewModel.bookingListState.collectAsState()
    LaunchedEffect(Unit) { viewModel.fetchMyBookings() }
    MyBookingsContent(
        state,
        onBookingClick,
        onBack,
        { viewModel.fetchMyBookings() },
        onBrowseTours,
        bottomBar = {
            if (onAccount != null)
                OceanBottomBar(true, "passenger_bookings", onBrowseTours, {}, onAccount, {})
        },
    )
}

@Composable
fun MyBookingsContent(
    state: BookingListState,
    onBookingClick: (Long) -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onBrowseTours: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    val bookings = (state as? BookingListState.Success)?.bookings.orEmpty()
    val filtered =
        remember(bookings, query, selected) {
            bookings.filter {
                (selected == null || it.status?.name == selected) &&
                    (query.isBlank() ||
                        listOf(it.bookingCode, it.primaryContactName).any { text ->
                            text?.contains(query.trim(), ignoreCase = true) == true
                        })
            }
        }
    PassengerPage(
        "Chuyến đi của tôi",
        "Theo dõi đặt chỗ và thông tin hành khách",
        onBack,
        bottomBar,
    ) {
        item {
            Surface(
                color = OceanNavy,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        "ĐẶT CHỖ CỦA BẠN",
                        style = MaterialTheme.typography.labelMedium,
                        color = OceanMint,
                    )
                    Text(
                        if (state is BookingListState.Success) "${bookings.size} booking"
                        else "Hành trình của bạn",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White,
                    )
                    if (state is BookingListState.Success)
                        Text(
                            "${bookings.count{it.status==BookingStatus.CONFIRMED}} đã xác nhận · ${bookings.count{it.status==BookingStatus.PENDING_PAYMENT}} chờ thanh toán",
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = .85f),
                            style = MaterialTheme.typography.bodySmall,
                        )
                }
            }
        }
        item {
            OutlinedTextField(
                query,
                { query = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                placeholder = {
                    Text(
                        "Tìm mã booking hoặc tên",
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
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected == null, { selected = null }, label = { Text("Tất cả") })
                BookingStatus.entries.forEach { status ->
                    FilterChip(
                        selected == status.name,
                        { selected = status.name },
                        label = { Text(bookingLabel(status)) },
                    )
                }
            }
            if (state is BookingListState.Success)
                Text(
                    "${filtered.size} booking",
                    color = OceanSlate,
                    style = MaterialTheme.typography.labelLarge,
                )
        }
        when (state) {
            BookingListState.Idle,
            BookingListState.Loading -> item { PassengerLoading() }
            is BookingListState.Error -> item { PassengerError(state.message, onRetry) }
            is BookingListState.Success -> {
                if (filtered.isEmpty())
                    item {
                        PassengerEmpty(
                            "Chưa có booking phù hợp",
                            if (bookings.isEmpty())
                                "Khám phá các hành trình và chọn gói tour để bắt đầu kỳ nghỉ."
                            else "Thử mã khác hoặc đổi bộ lọc trạng thái.",
                            if (bookings.isEmpty()) {
                                if (onBrowseTours != null) "Khám phá tour" else "Quay lại"
                            } else "Xóa bộ lọc",
                            {
                                if (bookings.isEmpty()) (onBrowseTours ?: onBack)()
                                else {
                                    query = ""
                                    selected = null
                                }
                            },
                        )
                    }
                items(filtered, key = { it.id }) { booking ->
                    BookingCard(booking) { onBookingClick(booking.id) }
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: BookingResponse, onClick: () -> Unit) {
    PassengerCard(Modifier.clickable(onClick = onClick)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                color = OceanMintSoft,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            ) {
                PassengerIcon(PassengerGlyph.TICKET, modifier = Modifier.padding(12.dp).size(24.dp))
            }
            Column(Modifier.weight(1f)) {
                Text("MÃ BOOKING", style = MaterialTheme.typography.labelSmall, color = OceanSlate)
                Text(
                    booking.bookingCode ?: "#${booking.id}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                )
            }
        }
        PassengerPill(bookingLabel(booking.status), booking.status == BookingStatus.CONFIRMED)
        PassengerInfo("Người liên hệ", booking.primaryContactName)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f)) {
                PassengerInfo(
                    "Hành khách",
                    booking.numberPassengers?.let { "$it khách" }
                        ?: booking.bookingPassengers?.size?.let { "$it khách" },
                )
            }
            Column(Modifier.weight(1f)) {
                PassengerInfo("Ngày đặt", passengerDate(booking.createdAt))
            }
        }
        HorizontalDivider(color = OceanLine)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Tổng giá trị booking",
                    style = MaterialTheme.typography.labelSmall,
                    color = OceanSlate,
                )
                Text(
                    passengerMoney(booking.totalAmount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OceanTeal,
                )
            }
            PassengerIcon(PassengerGlyph.ARROW)
        }
    }
}
