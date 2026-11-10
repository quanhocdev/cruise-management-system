package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record ActivityVisitCompletedEvent(
        Long bookingId,
        Long bookingPassengerId,
        UUID visitTourId,
        Instant completedAt) {
}