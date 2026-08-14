package com.project.auth.dto;

import java.time.LocalDateTime;

public record StaffResponse(
                Long id,
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