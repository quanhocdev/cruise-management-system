
package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record ActivityVisitCompletedEvent(
                Long bookingId,
                Long bookingPassengerId,
                UUID bookingOwnerUserId,
                UUID visitTourId,
                UUID sourceUsageId,
                String activityName,
                Instant checkedInAt,
                Instant completedAt) {
}
