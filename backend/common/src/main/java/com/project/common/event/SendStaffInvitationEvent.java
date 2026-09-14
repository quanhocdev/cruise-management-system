package com.project.common.event;

import java.time.Instant;

public record SendStaffInvitationEvent(
        Long userId,
        String recipientEmail,
        String username,
        String activationLink,
        Instant createdAt) {
}