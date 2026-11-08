
package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record ManagerStatusChangedEvent(
        UUID userId,
        String roleName,
        String previousStatus,
        String newStatus,
        Instant changedAt) {
}
