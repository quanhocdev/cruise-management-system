package com.project.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceCompletedEvent(
                Long bookingId,
                Long bookingPassengerId,
                UUID bookingOwnerUserId,
                UUID serviceTourId,
                Long sourceUsageId,
                String serviceName,
                LocalDateTime checkedInAt,
                LocalDateTime completedAt) {
}