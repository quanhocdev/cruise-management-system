package com.project.tour.dto.shore;

import java.util.List;
import java.util.UUID;

public record AssignShoreStaffResult(
        List<ShoreStaffAssignmentResponse> assigned,
        List<UUID> alreadyAssigned,
        List<UUID> invalidUserIds) {
}