
package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record SendStaffInvitationEvent(
                UUID userId,
                String recipientEmail,
                String username,
                String activationLink,
                Instant createdAt) {
}
