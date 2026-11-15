package com.project.auth.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponse(
                Long id,
                UUID userId,
                String fullName,
                LocalDate dateOfBirth,
                String phone,
                String address,
                String gender,
                String nationality,
                String identityNumber,
                String avatarUrl,
                String avatarPublicId,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}