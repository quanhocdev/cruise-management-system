
package com.project.tour.dto.onboard;

import com.project.tour.model.StatusAssignStaff;
import com.project.tour.model.TypeOnboardStaff;

import java.time.LocalDateTime;
import java.util.UUID;

public record OnboardStaffAssignmentResponse(
        UUID id,
        UUID tourId,
        TypeOnboardStaff assignmentType,
        UUID cruiseDeckId,
        UUID activityCruiseTourId,
        UUID userId,
        String username,
        String email,
        String fullName,
        String phone,
        String avatarUrl,
        StatusAssignStaff status,
        String rejectionReason,
        UUID assignedBy,
        LocalDateTime assignedAt,
        LocalDateTime respondedAt) {
}
