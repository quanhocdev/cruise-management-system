package com.project.cruise.android.data.time

import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId

/** Schedule values are Vietnamese local time, independent of the device zone. */
object CruiseTime {
    val zone: ZoneId = ZoneId.of("Asia/Ho_Chi_Minh")
    const val label = "Giờ Việt Nam (UTC+7)"

    fun now(clock: Clock = Clock.systemUTC()): LocalDateTime =
        LocalDateTime.now(clock.withZone(zone))
}
