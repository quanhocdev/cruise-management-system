package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record ProductUsedEvent(
        Long bookingId,
        Long bookingPassengerId,
        UUID productTourId,
        Integer quantity,
        Instant createdAt) {
}