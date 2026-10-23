package com.project.tour.client.auth;

import java.util.UUID;

public record ShoreStaffResponse(
        UUID userId,
        String username,
        String email,
        String role,
        String fullName,
        String phone,
        String avatarUrl) {
}