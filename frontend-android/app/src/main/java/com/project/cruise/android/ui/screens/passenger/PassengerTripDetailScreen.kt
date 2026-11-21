package com.project.cruise.android.ui.screens.passenger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.cruise.android.ui.theme.OceanTheme
import com.project.cruise.android.viewmodel.passenger.PassengerTripDetailViewModel
import com.project.cruise.android.viewmodel.passenger.TripSectionKey

@Composable
fun PassengerTripDetailScreen(
    tourId: String,
    bookingId: Long,
    viewModel: PassengerTripDetailViewModel,
    initialSection: TripSectionKey = TripSectionKey.TRACKING,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tourId, bookingId) {
        if (tourId.isNotBlank() && bookingId > 0L) {
            viewModel.selectSection(
                section = initialSection,
                tourId = tourId,
                bookingId = bookingId
            )
        }
    }

    val onReload: () -> Unit = {
        viewModel.reloadSection(
            section = uiState.activeSection,
            tourId = tourId,
            bookingId = bookingId
        )
    }

    OceanTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Color(0xFFF1F5F9)
        ) {
            if (tourId.isBlank() || bookingId <= 0L) {
                Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                    Text(
                        text = "Không tìm thấy thông tin chuyến đi hoặc đơn đặt chỗ.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                return@Surface
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TripDetailHeroCard(
                        booking = uiState.booking,
                        routeOverview = uiState.routeOverview,
                        cruise = uiState.cruise,
                        vesselLocation = uiState.vesselLocation,
                        onReload = onReload
                    )
                }

                item {
                    TripSectionTabBar(
                        activeSection = uiState.activeSection,
                        onSelectSection = { section ->
                            viewModel.selectSection(
                                section = section,
                                tourId = tourId,
                                bookingId = bookingId
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (uiState.activeSection) {
                            TripSectionKey.TRACKING -> TripRouteSection(
                                overview = uiState.routeOverview,
                                legs = uiState.routeLegs,
                                vesselLocation = uiState.vesselLocation,
                                weather = uiState.weather,
                                loading = uiState.routeLoading || uiState.vesselLoading,
                                error = uiState.routeError ?: uiState.vesselError,
                                onReload = onReload
                            )

                            TripSectionKey.BOOKING -> TripBookingSection(
                                booking = uiState.booking,
                                packageName = uiState.matchedPackage?.name,
                                loading = uiState.bookingLoading,
                                error = uiState.bookingError,
                                onReload = onReload
                            )

                            TripSectionKey.PASSENGERS -> TripPassengersSection(
                                passengers = uiState.passengers,
                                loading = uiState.passengersLoading,
                                error = uiState.passengersError,
                                onReload = onReload
                            )

                            TripSectionKey.BENEFITS -> TripBenefitsSection(
                                packageId = uiState.matchedPackage?.id,
                                packageName = uiState.matchedPackage?.name,
                                packageDescription = uiState.matchedPackage?.description,
                                benefits = uiState.resolvedBenefits,
                                loading = uiState.bookingLoading || uiState.facilitiesLoading,
                                error = uiState.bookingError ?: uiState.facilitiesError,
                                onReload = onReload
                            )

                            TripSectionKey.ITINERARY -> SectionHeader(
                                eyebrow = "LỊCH TRÌNH",
                                title = "Lịch trình chuyến đi (${uiState.schedules.size} ngày)",
                                subtitle = "Các chặng dừng và hoạt động theo từng ngày.",
                                loading = uiState.itineraryLoading,
                                onReload = onReload
                            )

                            TripSectionKey.CRUISE -> SectionHeader(
                                eyebrow = "DU THUYỀN",
                                title = uiState.cruise?.name ?: "Thông tin du thuyền",
                                subtitle = "Số tầng: ${uiState.decks.size}",
                                loading = uiState.cruiseLoading,
                                onReload = onReload
                            )

                            TripSectionKey.FACILITIES -> SectionHeader(
                                eyebrow = "LOẠI HÌNH PHỤC VỤ",
                                title = "Tiện ích & gói tour",
                                subtitle = "Sản phẩm: ${uiState.products.size} • Dịch vụ: ${uiState.services.size} • Gói: ${uiState.packages.size}",
                                loading = uiState.facilitiesLoading,
                                onReload = onReload
                            )

                            TripSectionKey.USAGES -> SectionHeader(
                                eyebrow = "LƯỢT SỬ DỤNG",
                                title = "Lịch sử sử dụng quyền lợi & dịch vụ",
                                subtitle = "Sản phẩm: ${uiState.productUsages.size} • Dịch vụ: ${uiState.serviceUsages.size} • Hoạt động: ${uiState.activityCruiseUsages.size} • Tham quan: ${uiState.visitUsages.size}",
                                loading = uiState.usagesLoading,
                                onReload = onReload
                            )

                            TripSectionKey.BILLS -> SectionHeader(
                                eyebrow = "HÓA ĐƠN",
                                title = "Hóa đơn thanh toán (${uiState.bills.size})",
                                subtitle = "Danh sách hóa đơn thuộc đơn đặt chỗ.",
                                loading = uiState.billsLoading,
                                onReload = onReload
                            )
                        }
                    }
                }
            }
        }
    }
}