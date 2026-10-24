
package com.project.tour.dto.convenience;

import com.project.tour.model.StatusAssignStaff;
import com.project.tour.model.TypeConvenienceAssign;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConvenienceStaffAssignmentResponse(
        UUID id,
        UUID tourId,
        TypeConvenienceAssign assignmentType,
        UUID targetId,
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
