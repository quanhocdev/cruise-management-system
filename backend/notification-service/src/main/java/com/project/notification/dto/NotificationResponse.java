package com.project.notification.dto;

import com.project.notification.model.*;
import java.time.Instant;

public record NotificationResponse(Long id, Long recipientUserId, String recipientEmail,
    NotificationType type, String title, String message, String referenceType, Long referenceId,
    EmailStatus emailStatus, Instant readAt, Instant createdAt) {}
