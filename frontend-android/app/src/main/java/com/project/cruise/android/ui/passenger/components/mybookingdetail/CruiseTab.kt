package com.project.cruise.android.ui.passenger.components.mybookingdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.passenger.tripdetail.*
import com.project.cruise.android.ui.common.*
import com.project.cruise.android.ui.theme.*
import com.project.cruise.android.viewmodel.passenger.PassengerTripDetailUiState

fun LazyListScope.cruiseTab(
    state: PassengerTripDetailUiState,
    selectedDeckId: String?,
    onSelectDeck: (String) -> Unit,
    onRetry: () -> Unit,
) {
    if (tripLoadGate("c", state.cruiseLoading, state.cruiseError, state.cruise == null && state.decks.isEmpty(), onRetry)) return

    state.cruise?.let { cruise -> item(key = "c-info") { CruiseInfoCard(cruise) } }

    val decks = state.decks.sortedBy { it.deckNumber ?: Int.MAX_VALUE }
    if (decks.isNotEmpty()) {
        item(key = "c-decks-title") { PassengerSection("Sơ đồ trên tàu", "Chọn tầng để xem các khu vực.") }
        item(key = "c-deck-row") { DeckRow(decks, selectedDeckId, onSelectDeck) }
        val deck = decks.firstOrNull { it.id == selectedDeckId } ?: decks.first()
        item(key = "c-areas") { DeckAreasCard(deck, state) }
    }
}

@Composable
private fun CruiseInfoCard(cruise: TripCruiseDto) {
    PassengerCard {
        CruiseVisual(cruise.imageUrl)
        Text(
            cruise.name ?: "Du thuyền",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            cruise.code?.takeIf { it.isNotBlank() }?.let { PassengerPill("Mã $it", PillTone.NEUTRAL) }
            cruise.maxPassengers?.let { GoldBadge("Sức chứa $it khách") }
        }
        cruise.description?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = OceanSlate, maxLines = 5)
        }
    }
}

@Composable
private fun DeckRow(decks: List<TripCruiseDeckDto>, selectedId: String?, onSelect: (String) -> Unit) {
    val current = selectedId ?: decks.first().id
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(decks, key = { it.id ?: "deck-${it.deckNumber}" }) { deck ->
            val on = deck.id == current
            Surface(
                onClick = { deck.id?.let(onSelect) },
                shape = RoundedCornerShape(18.dp),
                color = if (on) OceanGold else Color.White,
                border = BorderStroke(1.dp, if (on) OceanGold else OceanLine),
                shadowElevation = if (on) 3.dp else 0.dp,
            ) {
                Column(
                    Modifier.widthIn(min = 92.dp).padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "Tầng ${deck.deckNumber ?: ""}".trim(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = OceanNavy,
                    )
                    deck.name?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (on) OceanNavy.copy(alpha = .75f) else OceanSlate,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckAreasCard(deck: TripCruiseDeckDto, state: PassengerTripDetailUiState) {
    val areas = deck.id?.let { state.deckAreas[it] }
    val loading = deck.id?.let { it in state.loadingDeckIds } == true
    PassengerCard {
        Text(
            deck.name ?: "Tầng ${deck.deckNumber ?: ""}".trim(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        deck.description?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = OceanSlate)
        }
        HorizontalDivider(color = OceanLine)
        if (areas == null) {
            when {
                loading -> LinearProgressIndicator(Modifier.fillMaxWidth(), color = OceanGold)
                state.areasError != null ->
                    Text(state.areasError, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                else -> Text("Đang chuẩn bị dữ liệu khu vực…", style = MaterialTheme.typography.bodySmall, color = OceanSlate)
            }
        } else if (areas.isEmpty()) {
            Text("Tầng này chưa có thông tin khu vực.", style = MaterialTheme.typography.bodySmall, color = OceanSlate)
        } else {
            areas.forEach { area ->
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            area.name ?: "Khu vực",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = OceanNavy,
                        )
                        area.description?.takeIf { it.isNotBlank() }?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = OceanSlate)
                        }
                    }
                    area.areaType?.takeIf { it.isNotBlank() }?.let { PassengerPill(it, PillTone.NEUTRAL) }
                }
            }
        }
    }
}