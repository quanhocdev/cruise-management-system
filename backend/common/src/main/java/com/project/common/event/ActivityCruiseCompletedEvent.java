package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record ActivityCruiseCompletedEvent(
        Long bookingId,
        Long bookingPassengerId,
        UUID activityCruiseTourId,
        Instant completedAt) {
}