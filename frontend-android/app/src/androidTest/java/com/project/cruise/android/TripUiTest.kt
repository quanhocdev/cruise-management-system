package com.project.cruise.android

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.project.cruise.android.data.dto.passenger.*
import com.project.cruise.android.ui.screens.passenger.*
import com.project.cruise.android.ui.theme.OceanPage
import com.project.cruise.android.viewmodel.passenger.BookingHistoryState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class TripUiTest {
    @get:Rule val compose = createComposeRule()
    private fun trip() = BookingTripDetails("v1", "Hạ Long", "Du thuyền", null, null)

    @Test fun itineraryShowsDayAndPreservesRetryAndBack() {
        var retried = false
        var closed = false
        val day = ItineraryDay("d1", 1, "2026-11-01", "Khởi hành", "Đón khách tại cảng")
        compose.setContent { BookingItineraryDialog(BookingHistoryState(trip = trip().copy(itinerary = listOf(day))), { retried = true }, { closed = true }) }
        compose.onNodeWithText("Khởi hành").assertIsDisplayed()
        compose.onNodeWithText("Tải lại lịch trình").performClick()
        compose.onNodeWithText("← Chi tiết booking").performClick()
        compose.runOnIdle { assertEquals(true, retried); assertEquals(true, closed) }
    }

    @Test fun activityGroupsAndDetailBackArePreserved() {
        val onboard = TripActivity("a1", "ONBOARD", "Yoga", null, null, null, "Boong tàu", BigDecimal.ZERO, 10, "CONFIGURED")
        val shore = onboard.copy(id = "a2", type = "SHORE", name = "Tham quan đảo")
        compose.setContent { BookingActivitiesDialog(BookingHistoryState(trip = trip().copy(activities = listOf(onboard, shore))), {}, {}) }
        compose.onNodeWithText("Yoga").assertIsDisplayed()
        compose.onNodeWithText("Tham quan").performClick()
        compose.onNodeWithText("Tham quan đảo").performClick()
        compose.onNodeWithText("Chi tiết hoạt động").assertIsDisplayed()
        compose.onNodeWithText("← Danh sách hoạt động").performClick()
        compose.onNodeWithText("Tham quan đảo").assertIsDisplayed()
    }

    @Test fun catalogGroupsKeepDetailNavigation() {
        val service = TripCatalogItem("s1", "SERVICE", "Spa", null, null, BigDecimal.TEN, "Tầng 1", 30, 2, "CONFIGURED")
        val product = service.copy(id = "p1", type = "PRODUCT", name = "Nước uống")
        compose.setContent { BookingCatalogDialog(BookingHistoryState(trip = trip().copy(catalog = listOf(service, product))), {}, {}) }
        compose.onNodeWithText("Sản phẩm").performClick()
        compose.onNodeWithText("Nước uống").performClick()
        compose.onNodeWithText("Chi tiết").assertIsDisplayed()
        compose.onNodeWithText("← Danh sách").performClick()
        compose.onNodeWithText("Nước uống").assertIsDisplayed()
    }

    @Test fun draftPreviewNeverEnablesSendingAndCanBeCleared() {
        val item = TripCatalogItem("p1", "PRODUCT", "Nước", null, null, BigDecimal.TEN, null, null, null, "CONFIGURED")
        val passenger = PassengerVoyageBookingResponse(9, 1, fullName = "Khách thử", dateOfBirth = "2000-01-01", gender = "MALE", passengerStatus = "REGISTERED", embarkationStatus = "NOT_CHECKED_IN")
        val booking = PassengerBookingResponse(1, "v1", "CR1", primaryContactName = "Khách thử", primaryContactPhone = "0900000000", totalAmount = BigDecimal.TEN, status = "CONFIRMED", createdAt = "", passengers = listOf(passenger))
        compose.setContent { OceanPage { CatalogOrderDraftForm(item, booking) } }
        compose.onNodeWithText("Khách thử · #9").performScrollTo().performClick()
        compose.onNodeWithText("Xem lại bản nháp").performScrollTo().performClick()
        compose.onNodeWithText("Gửi đơn — chưa có API").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Sửa bản nháp").performScrollTo().performClick()
        compose.onNodeWithText("Số lượng (1–99)").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Xóa nội dung nháp").performScrollTo().performClick()
        compose.onNodeWithText("Xem lại bản nháp").performScrollTo().performClick()
        compose.onNodeWithText("Chọn hành khách trong booking này.").performScrollTo().assertIsDisplayed()
    }
}
