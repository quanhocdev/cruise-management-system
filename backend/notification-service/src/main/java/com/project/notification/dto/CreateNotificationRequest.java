package com.project.notification.dto;

import com.project.notification.model.NotificationType;
import jakarta.validation.constraints.*;

public record CreateNotificationRequest(
    @NotNull Long recipientUserId,
    @Email String recipientEmail,
    @NotNull NotificationType type,
    @NotBlank @Size(max = 200) String title,
    @NotBlank @Size(max = 5000) String message,
    @Size(max = 40) String referenceType,
    Long referenceId
) {}
