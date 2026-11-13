package com.project.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityCruiseCompletedEvent(
                Long bookingId,
                Long bookingPassengerId,
                UUID bookingOwnerUserId,
                UUID activityCruiseTourId,
                Long sourceUsageId,
                String activityName,
                LocalDateTime checkedInAt,
                LocalDateTime completedAt) {
}