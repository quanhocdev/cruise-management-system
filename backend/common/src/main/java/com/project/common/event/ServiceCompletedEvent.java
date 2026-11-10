
package com.project.common.event;

import java.time.Instant;

public record ServiceCompletedEvent(
        Long bookingId,
        Long bookingPassengerId,
        java.util.UUID serviceTourId,
        Instant completedAt) {
}
