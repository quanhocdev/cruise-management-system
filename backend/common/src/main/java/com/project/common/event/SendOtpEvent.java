package com.project.common.event;

import java.time.Instant;

public record SendOtpEvent(
        Long userId,
        String recipientEmail,
        String otp,
        Instant createdAt) {
}