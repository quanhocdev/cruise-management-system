package com.project.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationApprovalTourResponse(
        UUID id,
        UUID tourId,
        UUID targetId,
        String assignmentType,
        String title,
        String message,
        String actionLink,
        boolean isRead,
        Instant createdAt) {
}