
package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record ActivityCruiseCompletedEvent(
                Long bookingId,
                Long bookingPassengerId,
                UUID bookingOwnerUserId,
                UUID activityCruiseTourId,
                UUID sourceUsageId,
                String activityName,
                Instant checkedInAt,
                Instant completedAt) {
}
