package com.project.auth.dto;

import java.util.UUID;

public record ManagerInfoResponse(
        UUID userId,
        String username,
        String email,
        String fullName,
        String phone,
        String avatarUrl) {
}