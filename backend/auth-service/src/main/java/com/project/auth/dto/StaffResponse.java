
package com.project.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record StaffResponse(
        UUID id,
        String username,
        String email,
        Long roleId,
        String role,
        String provider,
        String status,
        Boolean enabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
