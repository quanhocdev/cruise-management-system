package com.project.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityVisitCompletedEvent(
                Long bookingId,
                Long bookingPassengerId,
                UUID bookingOwnerUserId,
                UUID visitTourId,
                Long sourceUsageId,
                String activityName,
                LocalDateTime checkedInAt,
                LocalDateTime completedAt) {
}