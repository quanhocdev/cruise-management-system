package com.project.cruise.android.ui.screens.passenger

import org.junit.Assert.*
import org.junit.Test
import com.google.gson.Gson
import com.project.cruise.android.data.dto.passenger.BookingTripDetails

class CatalogFormattingTest {
    @Test fun acceptsOnlyRemoteImages() {
        assertEquals("https://example.com/photo.png", catalogImageUrl(" https://example.com/photo.png "))
        assertNull(catalogImageUrl("file:///data/local/secret"))
        assertNull(catalogImageUrl("content://photos/1"))
        assertNull(catalogImageUrl("https://user:pass@example.com/photo"))
        assertNull(catalogImageUrl("/uploads/photo.png"))
        assertNull(catalogImageUrl("not a url"))
        assertNull(catalogImageUrl(null))
    }
    @Test fun missingDurationIsNotZeroMinutes() {
        assertEquals("Đang cập nhật", catalogDuration(null))
        assertEquals("Đang cập nhật", catalogDuration(0))
        assertEquals("30 phút", catalogDuration(30))
    }
    @Test fun distinguishesOldBackendFromEmptyCatalog() {
        val gson = Gson()
        assertNull(gson.fromJson("""{"voyageId":"v"}""", BookingTripDetails::class.java).catalog)
        assertTrue(gson.fromJson("""{"voyageId":"v","catalog":[]}""", BookingTripDetails::class.java).catalog!!.isEmpty())
        val trip = gson.fromJson("""{"voyageId":"v","catalog":[{"id":"s","type":"SERVICE","name":"Spa","price":250000.00,"durationMinutes":30}]}""", BookingTripDetails::class.java)
        assertEquals("Spa", trip.catalog!![0].name)
        assertEquals(0, trip.catalog[0].price!!.compareTo(java.math.BigDecimal("250000")))
    }
}
