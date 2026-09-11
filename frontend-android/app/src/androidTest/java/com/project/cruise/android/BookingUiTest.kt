package com.project.cruise.android

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.project.cruise.android.data.dto.passenger.*
import com.project.cruise.android.ui.screens.passenger.*
import com.project.cruise.android.viewmodel.passenger.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class BookingUiTest {
    @get:Rule val compose = createComposeRule()
    private fun booking(status: String = "PENDING_PAYMENT") = PassengerBookingResponse(
        id = 7, voyageId = "v1", bookingCode = "CR007", primaryContactName = "Khách kiểm thử",
        primaryContactPhone = "0900000000", totalAmount = BigDecimal("3500000"), status = status, createdAt = "2026-09-08")

    @Test fun historyKeepsSelectionAndRefresh() {
        var selected = 0L
        var refreshed = false
        compose.setContent { MyBookingsScreen(BookingHistoryState(bookings = listOf(booking())), { refreshed = true }, { selected = it }, {}) }
        compose.onNodeWithText("CR007").performScrollTo().performClick()
        compose.onNodeWithText("Tải lại").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(7L, selected); assertEquals(true, refreshed) }
    }

    @Test fun paymentButtonKeepsCallbackAndBusyLock() {
        var started = 0
        var state by mutableStateOf(BookingHistoryState(detail = booking()))
        compose.setContent { BookingDetailScreen(state, {}, { started++ }, {}, {}, {}) }
        compose.onNodeWithText("Thanh toán VNPay Sandbox").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, started); state = state.copy(creatingPayment = true) }
        compose.onNodeWithText("Thanh toán VNPay Sandbox").assertIsNotEnabled()
        compose.runOnIdle { state = state.copy(detail = booking("CONFIRMED"), creatingPayment = false) }
        compose.onNodeWithText("Thanh toán VNPay Sandbox").assertDoesNotExist()
        compose.onNodeWithText("QR booking").performScrollTo().assertIsDisplayed()
    }

    @Test fun unknownBookingOutcomeStillPreventsResubmission() {
        val room = AvailableRoom("r1", "A1", "d1", 1, "rt1", "Deluxe", null, 3500000.0, 2, 0, 2, true)
        compose.setContent { CreateBookingScreen(PassengerBookingState(loading = false, room = room, outcomeUnknown = true), {}, {}, {}, {}, {}) }
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Xác nhận giữ chỗ"))
        compose.onNodeWithText("Xác nhận giữ chỗ").assertIsNotEnabled()
    }

    @Test fun returnScreenKeepsBookingNavigation() {
        var opened = false
        compose.setContent { PaymentReturnScreen("SUCCESS", 11) { opened = true } }
        compose.onNodeWithText("Thanh toán thành công").assertIsDisplayed()
        compose.onNodeWithText("Xem booking của tôi").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(true, opened) }
    }

    @Test fun createdBookingKeepsDashboardCallback() {
        var done = false
        compose.setContent { BookingCreatedScreen(booking()) { done = true } }
        compose.onNodeWithText("Về Dashboard").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(true, done) }
    }
}
