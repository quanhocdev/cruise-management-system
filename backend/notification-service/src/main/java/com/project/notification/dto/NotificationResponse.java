
package com.project.notification.dto;

import com.project.notification.model.*;
import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
                Long id,
                UUID recipientUserId,
                String recipientEmail,
                NotificationType type,
                String title,
                String message,
                String referenceType,
                Long referenceId,
                EmailStatus emailStatus,
                Instant readAt,
                Instant createdAt) {
}
