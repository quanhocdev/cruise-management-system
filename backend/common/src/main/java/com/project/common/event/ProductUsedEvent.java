package com.project.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductUsedEvent(
                Long bookingId,
                Long bookingPassengerId,
                UUID bookingOwnerUserId,
                UUID productTourId,
                Long sourceUsageId,
                String productName,
                Integer quantity,
                LocalDateTime usedAt) {
}