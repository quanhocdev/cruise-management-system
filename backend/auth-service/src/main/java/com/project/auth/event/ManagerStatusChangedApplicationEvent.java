
package com.project.auth.event;

import java.time.Instant;
import java.util.UUID;

import com.project.auth.model.enums.UserStatus;

public record ManagerStatusChangedApplicationEvent(
        UUID userId,
        String roleName,
        UserStatus previousStatus,
        UserStatus newStatus,
        Instant changedAt) {
}
