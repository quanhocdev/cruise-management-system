package com.project.cruise.android.passenger

import com.project.cruise.android.ui.screens.passenger.*
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test

class ActivityFormattingTest {
    @Test fun unknownPriceIsNotFree() {
        assertEquals("Đang cập nhật", activityPrice(null))
        assertEquals("Miễn phí", activityPrice(BigDecimal.ZERO))
        assertNotEquals("Miễn phí", activityPrice(BigDecimal("100000")))
    }
    @Test fun formatsTimeAndFallback() {
        assertEquals("09:30 · 22/11/2026", activityTime("2026-11-22T09:30:00"))
        assertEquals("Đang cập nhật", activityTime(null))
    }
    @Test fun cancelledIsVisibleAsCancelled() { assertEquals("Đã hủy", activityStatus("CANCELLED")) }
}
