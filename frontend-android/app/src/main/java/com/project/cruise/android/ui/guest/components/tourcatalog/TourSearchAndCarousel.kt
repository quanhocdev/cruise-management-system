package com.project.cruise.android.ui.guest.components.tourcatalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.tour.PublicTourSummaryResponse
import com.project.cruise.android.data.dto.tour.TourStatusTrip
import com.project.cruise.android.ui.common.CruiseVisual
import com.project.cruise.android.ui.theme.*
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

data class TourSearchCriteria(
    val departure: String = "",
    val destination: String = "",
    val date: String = "",
)

fun selectTop3UpcomingTours(tours: List<PublicTourSummaryResponse>): List<PublicTourSummaryResponse> =
    tours
        .filter { it.statusTrip == TourStatusTrip.READY }
        .sortedWith(
            compareByDescending<PublicTourSummaryResponse> { it.startDate.orEmpty() }
                .thenByDescending { it.bookingStart.orEmpty() }
                .thenByDescending { it.id }
        )
        .take(3)

fun matchesTourSearch(
    tour: PublicTourSummaryResponse,
    criteria: TourSearchCriteria,
): Boolean {
    val depQuery = criteria.departure.trim()
    val destQuery = criteria.destination.trim()
    val dateQuery = criteria.date.trim()

    val matchDeparture = depQuery.isBlank() || listOf(
        tour.departurePort?.name,
        tour.departurePort?.city,
        tour.departurePort?.code,
    ).any { it?.contains(depQuery, ignoreCase = true) == true }

    val matchDestination = destQuery.isBlank() || listOf(
        tour.arrivalPort?.name,
        tour.arrivalPort?.city,
        tour.arrivalPort?.code,
    ).any { it?.contains(destQuery, ignoreCase = true) == true }

    val matchDate = dateQuery.isBlank() || listOf(
        tour.startDate,
        tour.endDate,
    ).any { it?.contains(dateQuery, ignoreCase = true) == true }

    return matchDeparture && matchDestination && matchDate
}

@Composable
fun TourSearchCard(
    criteria: TourSearchCriteria,
    onCriteriaChange: (TourSearchCriteria) -> Unit,
    onSearchClick: () -> Unit,
    onBrowseAllClick: (() -> Unit)? = null,
    searchButtonLabel: String = "Tìm kiếm",
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Tìm kiếm hành trình",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OceanNavy,
            )

            OutlinedTextField(
                value = criteria.departure,
                onValueChange = { onCriteriaChange(criteria.copy(departure = it)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                label = { Text("Điểm đi (cảng / thành phố)") },
                leadingIcon = {
                    Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = OceanTeal)
                },
            )

            OutlinedTextField(
                value = criteria.destination,
                onValueChange = { onCriteriaChange(criteria.copy(destination = it)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                label = { Text("Điểm đến (cảng / thành phố)") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = OceanTeal)
                },
            )

            OutlinedTextField(
                value = criteria.date,
                onValueChange = { onCriteriaChange(criteria.copy(date = it)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                label = { Text("Ngày diễn ra (VD: 2026-12-01)") },
                leadingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = OceanTeal)
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = onSearchClick,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OceanNavy,
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(searchButtonLabel, fontWeight = FontWeight.SemiBold)
                }

                if (onBrowseAllClick != null) {
                    OutlinedButton(
                        onClick = onBrowseAllClick,
                        modifier = Modifier.height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Xem các tour hiện tại", maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingFeaturedCarousel(
    tours: List<PublicTourSummaryResponse>,
    onTourClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val featured = remember(tours) { selectTop3UpcomingTours(tours) }
    if (featured.isEmpty()) return

    var currentIndex by remember(featured) { mutableIntStateOf(0) }
    var tick by remember { mutableIntStateOf(0) }

    LaunchedEffect(featured.size, currentIndex, tick) {
        if (featured.size > 1) {
            delay(5000L)
            currentIndex = (currentIndex + 1) % featured.size
        }
    }

    val safeIndex = currentIndex.coerceIn(0, featured.lastIndex)
    val currentTour = featured[safeIndex]

    val goPrev: () -> Unit = {
        currentIndex = if (safeIndex - 1 < 0) featured.lastIndex else safeIndex - 1
        tick++
    }
    val goNext: () -> Unit = {
        currentIndex = (safeIndex + 1) % featured.size
        tick++
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Tour mới nhất",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                )
            }

            if (featured.size > 1) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilledTonalIconButton(onClick = goPrev, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Trước")
                    }
                    FilledTonalIconButton(onClick = goNext, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Sau")
                    }
                }
            }
        }

        Surface(
            onClick = { onTourClick(currentTour.id) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CruiseVisual(imageUrl = currentTour.cruiseImageUrl)

                Text(
                    text = currentTour.name ?: currentTour.code ?: "Hành trình",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OceanNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                val routeText = listOfNotNull(
                    currentTour.departurePort?.name ?: currentTour.departurePort?.city,
                    currentTour.arrivalPort?.name ?: currentTour.arrivalPort?.city,
                ).joinToString(" → ")

                if (routeText.isNotBlank()) {
                    Text(
                        text = routeText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = OceanTeal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = currentTour.startDate?.let { "Khởi hành: $it" } ?: "Sắp khởi hành",
                        style = MaterialTheme.typography.bodySmall,
                        color = OceanSlate,
                    )
                    currentTour.startingPrice?.let { price ->
                        val formatted = NumberFormat.getInstance(Locale("vi", "VN")).format(price)
                        Text(
                            text = "${formatted}đ",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = OceanNavy,
                        )
                    }
                }
            }
        }

        if (featured.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                featured.forEachIndexed { idx, _ ->
                    val selected = idx == safeIndex
                    Surface(
                        onClick = {
                            currentIndex = idx
                            tick++
                        },
                        modifier = Modifier.padding(horizontal = 4.dp).size(if (selected) 10.dp else 8.dp),
                        shape = RoundedCornerShape(50),
                        color = if (selected) OceanNavy else OceanSlate.copy(alpha = 0.25f),
                    ) {}
                }
            }
        }
    }
}