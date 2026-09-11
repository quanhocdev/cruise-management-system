package com.project.cruise.android.ui.screens.passenger

import com.project.cruise.android.data.dto.passenger.TripCatalogItem
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test

class CatalogOrderDraftTest {
    private val item = TripCatalogItem("p", "PRODUCT", "Water", null, null, BigDecimal("12500.50"), null, null, null, "CONFIGURED")

    @Test fun acceptsValidDraftAndComputesExactDecimalTotal() {
        assertNull(orderDraftError(item, 1L, setOf(1L), "2"))
        assertEquals(BigDecimal("25001.00"), orderDraftTotal(item.price, "2"))
    }
    @Test fun passengerMustBelongToBooking() {
        assertNotNull(orderDraftError(item, null, setOf(1L), "1"))
        assertNotNull(orderDraftError(item, 2L, setOf(1L), "1"))
    }
    @Test fun rejectsInvalidQuantity() {
        for (value in listOf("", "0", "-1", "1.5", "100", "999999999999999")) {
            assertNotNull(orderDraftError(item, 1L, setOf(1L), value))
            assertNull(orderDraftTotal(item.price, value))
        }
    }
    @Test fun missingPriceIsNotFree() {
        assertNotNull(orderDraftError(item.copy(price = null), 1L, setOf(1L), "1"))
        assertNull(orderDraftTotal(null, "1"))
        assertNull(orderDraftTotal(BigDecimal("-1"), "1"))
        assertEquals(BigDecimal.ZERO, orderDraftTotal(BigDecimal.ZERO, "1"))
    }
    @Test fun rejectsUnavailableOrUnknownItems() {
        for (status in listOf("OUT_OF_STOCK", "COMPLETED", "WAITING_CONFIG", null))
            assertNotNull(orderDraftError(item.copy(status = status), 1L, setOf(1L), "1"))
        assertNotNull(orderDraftError(item.copy(type = "UNKNOWN"), 1L, setOf(1L), "1"))
    }
    @Test fun serviceDraftIsOneVisitPerPassenger() {
        assertNull(orderDraftError(item.copy(type = "SERVICE"), 1L, setOf(1L), "1"))
        assertNotNull(orderDraftError(item.copy(type = "SERVICE"), 1L, setOf(1L), "2"))
    }
}
