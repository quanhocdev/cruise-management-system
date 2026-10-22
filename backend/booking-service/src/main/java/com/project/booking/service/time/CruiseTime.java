package com.project.booking.service.time;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

/** Tour schedule timestamps are local Vietnamese time, without an offset. */
@Component
public class CruiseTime {
    public static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private final Clock clock;

    public CruiseTime() {
        this(Clock.systemUTC());
    }

    public CruiseTime(Clock clock) {
        this.clock = clock.withZone(ZONE);
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
