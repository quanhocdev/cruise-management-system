package com.project.cruise.android.passenger

import com.project.cruise.android.ui.screens.passenger.tripDate
import org.junit.Assert.assertEquals
import org.junit.Test

class TripDateTest {
    @Test fun formatsDate() { assertEquals("22/11/2026", tripDate("2026-11-22")) }
    @Test fun missingAndInvalidDateUseFallback() {
        assertEquals("Đang cập nhật", tripDate(null))
        assertEquals("Đang cập nhật", tripDate("invalid"))
    }
}
