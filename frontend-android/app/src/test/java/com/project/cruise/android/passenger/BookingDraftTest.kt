package com.project.cruise.android.passenger

import com.google.gson.Gson
import com.project.cruise.android.data.dto.passenger.PassengerBookingResponse
import com.project.cruise.android.viewmodel.passenger.BookingDraft
import com.project.cruise.android.viewmodel.passenger.PassengerDraft
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class BookingDraftTest {
    private val today = LocalDate.of(2026, 9, 3)
    private val passenger = PassengerDraft(" Nguyen Van A ", "2000-02-29", "MALE")
    private val draft = BookingDraft(" Nguyen Van A ", " 0901234567 ", listOf(passenger))
    private val voyageId = "11111111-1111-1111-1111-111111111111"
    private val roomId = "22222222-2222-2222-2222-222222222222"

    @Test fun acceptsValidDraftWithOptionalFieldsEmpty() {
        assertNull(draft.validate(2, today))
    }

    @Test fun rejectsMissingContactAndOversizedContact() {
        assertNotNull(draft.copy(contactName = " ").validate(2, today))
        assertNotNull(draft.copy(contactPhone = "").validate(2, today))
        assertNotNull(draft.copy(contactName = "a".repeat(151)).validate(2, today))
    }

    @Test fun enforcesCapacityAndTwentyPassengerLimit() {
        assertNotNull(draft.validate(0, today))
        assertNotNull(draft.copy(passengers = listOf(passenger, passenger)).validate(1, today))
        assertNotNull(draft.copy(passengers = List(21) { passenger }).validate(100, today))
        assertNotNull(draft.copy(passengers = emptyList()).validate(2, today))
    }

    @Test fun rejectsInvalidFutureAndTodayBirthDates() {
        listOf("", "2025-02-29", "2026-09-03", "2030-01-01", "03/09/2000").forEach {
            assertNotNull(draft.copy(passengers = listOf(passenger.copy(dateOfBirth = it))).validate(2, today))
        }
    }

    @Test fun rejectsMissingGenderInvalidEmailAndLongPhone() {
        listOf(passenger.copy(gender = ""), passenger.copy(email = "a@@b"), passenger.copy(phone = "1".repeat(31))).forEach {
            assertNotNull(draft.copy(passengers = listOf(it)).validate(2, today))
        }
    }

    @Test fun requestMatchesBackendAndNeverContainsClientPriceOrUserId() {
        val request = draft.toRequest(voyageId, roomId)
        assertEquals("Nguyen Van A", request.primaryContactName)
        assertEquals("0901234567", request.primaryContactPhone)
        assertEquals(roomId, request.passengers.single().cabinId)
        assertNull(request.passengers.single().email)
        val json = Gson().toJson(request)
        assertFalse(json.contains("totalAmount"))
        assertFalse(json.contains("userId"))
        assertTrue(json.contains("\"dateOfBirth\":\"2000-02-29\""))
    }

    @Test fun readsServerBookingAmountWithoutFloatingPointLoss() {
        val response = Gson().fromJson("""{"id":5,"voyageId":"$voyageId","bookingCode":"CR00000005","primaryContactName":"A","primaryContactPhone":"0901234567","totalAmount":1234567890.12,"status":"PENDING_PAYMENT","createdAt":"2026-09-03T00:00:00Z","passengers":[]}""", PassengerBookingResponse::class.java)
        assertEquals(BigDecimal("1234567890.12"), response.totalAmount)
        assertEquals("PENDING_PAYMENT", response.status)
    }
}
