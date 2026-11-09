package com.project.cruise.android.ui.guest.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.tour.*
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.components.PublicTourChatFloatingWidget
import com.project.cruise.android.ui.guest.components.tourcatalog.*
import com.project.cruise.android.viewmodel.auth.*
import com.project.cruise.android.viewmodel.chat.ChatViewModel
import com.project.cruise.android.viewmodel.tour.*

@Composable
fun TourPublicScreen(
    viewModel: TourViewModel,
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    onTourClick: (String) -> Unit,
    onLoginClick: () -> Unit,
    onUserClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onHomeClick: () -> Unit = {},
) {
    val state by viewModel.tourListState.collectAsState()
    val session by authViewModel.sessionState.collectAsState()
    LaunchedEffect(Unit) { viewModel.fetchPublicTours() }
    val loggedIn = session is SessionState.Authenticated

    Box(Modifier.fillMaxSize()) {
        TourCatalogContent(
            state,
            onTourClick,
            { viewModel.fetchPublicTours() },
            bottomBar = {
                OceanBottomBar(
                    isLoggedIn = loggedIn,
                    currentRoute = "passenger_tours",
                    onHomeClick = onHomeClick,
                    onLoginClick = onLoginClick,
                    onUserClick = onUserClick,
                    onMyBookingsClick = onMyBookingsClick,
                    onMessagesClick = onMessagesClick,
                )
            },
        )
        if (loggedIn) {
            Box(Modifier.fillMaxSize().padding(bottom = 90.dp)) {
                PublicTourChatFloatingWidget(chatViewModel)
            }
        }
    }
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
    val filtered = remember(tours, query, openOnly) { tours.filterTours(query, openOnly) }

    PassengerPage("Khám phá", "OCEANCRUISE · HÀNH TRÌNH TRÊN BIỂN", bottomBar = bottomBar) {
        item { PassengerDiscoveryHero() }
        item { TourSearchField(query = query, onQueryChange = { query = it }) }
        item { TourFilterChips(openOnly = openOnly, onChange = { openOnly = it }) }
        item {
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
                        TourCatalogEmpty(
                            hasTours = tours.isNotEmpty(),
                            onRetry = onRetry,
                            onClearFilters = {
                                query = ""
                                openOnly = false
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

private fun List<PublicTourSummaryResponse>.filterTours(
    query: String,
    openOnly: Boolean,
): List<PublicTourSummaryResponse> = filter { tour ->
    (!openOnly || tour.statusBooking == TourBookingStatus.OPEN) &&
            (query.isBlank() ||
                    listOf(tour.name, tour.code, tour.cruiseName).any {
                        it?.contains(query.trim(), ignoreCase = true) == true
                    })
}