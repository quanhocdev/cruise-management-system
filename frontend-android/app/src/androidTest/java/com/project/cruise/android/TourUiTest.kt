package com.project.cruise.android

import androidx.compose.ui.test.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import com.project.cruise.android.data.dto.passenger.*
import com.project.cruise.android.ui.screens.passenger.*
import com.project.cruise.android.viewmodel.passenger.PassengerCatalogState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TourUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun tourSelectionPreservesIdAndBack() {
        var chosen = ""
        var back = false
        val tour = TourSummary("tour-7", "T7", "Hạ Long", "Kỳ nghỉ trên biển", "2026-11-01", "2026-11-03", "c1", "Du thuyền", null)
        compose.setContent { TourListContent(PassengerCatalogState(tours = listOf(tour)), { back = true }, { chosen = it }, {}) }
        compose.onNodeWithText("Khám phá hành trình →").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("tour-7", chosen) }
        compose.onNodeWithText("← Quay lại Dashboard").performClick()
        compose.runOnIdle { assertEquals(true, back) }
    }

    @Test fun departureSelectionPreservesVoyageId() {
        var chosen = ""
        val tour = TourDetail("t1", "T1", "Hạ Long", null, "2026-11-01", "2026-11-03", "", "", "c1", "Du thuyền", null, null, 20, emptyList())
        val departure = Departure("voyage-9", "t1", "T1", "2026-11-01", "2026-11-03", "c1", "Du thuyền", 20, "OPEN")
        compose.setContent { TourDetailContent("t1", PassengerCatalogState(detail = tour, departures = listOf(departure)), {}, { chosen = it }, {}) }
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Chọn chuyến · Xem phòng →"))
        compose.onNodeWithText("Chọn chuyến · Xem phòng →").performClick()
        compose.runOnIdle { assertEquals("voyage-9", chosen) }
    }

    @Test fun roomSelectionPreservesIdAndSoldOutIsDisabled() {
        var chosen = ""
        val room = AvailableRoom("room-5", "A01", "d1", 1, "rt1", "Deluxe", null, 3500000.0, 2, 0, 2, true)
        var state by androidx.compose.runtime.mutableStateOf(PassengerCatalogState(rooms = listOf(room)))
        compose.setContent { AvailableRoomsContent(state, {}, { chosen = it }, {}) }
        compose.onNodeWithText("Chọn phòng →").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("room-5", chosen); state = state.copy(rooms = listOf(room.copy(remainingCapacity = 0))) }
        compose.onNodeWithText("Chọn phòng →").performScrollTo().assertIsNotEnabled()
    }

    @Test fun roomErrorIsNotReportedAsSoldOutAndRetryWorks() {
        var retried = false
        compose.setContent { AvailableRoomsContent(PassengerCatalogState(error = "Lỗi kiểm thử"), {}, {}, { retried = true }) }
        compose.onNodeWithText("Chuyến này hiện không còn phòng trống.").assertDoesNotExist()
        compose.onNodeWithText("Thử lại").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(true, retried) }
    }
}
