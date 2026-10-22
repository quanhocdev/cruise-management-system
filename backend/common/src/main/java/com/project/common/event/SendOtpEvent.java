
package com.project.common.event;

import java.time.Instant;
import java.util.UUID;

public record SendOtpEvent(
                UUID userId,
                String recipientEmail,
                String otp,
                Instant createdAt) {
}
