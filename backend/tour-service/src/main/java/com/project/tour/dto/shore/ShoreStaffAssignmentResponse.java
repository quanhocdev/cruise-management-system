package com.project.tour.dto.shore;

import com.project.tour.model.StatusAssignStaff;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShoreStaffAssignmentResponse(
        UUID id,
        UUID tourId,
        UUID visitTourId,
        UUID userId,
        String username,
        String email,
        String fullName,
        String phone,
        String avatarUrl,
        StatusAssignStaff status,
        UUID assignedBy,
        LocalDateTime assignedAt,
        LocalDateTime respondedAt) {
}