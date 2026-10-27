
package com.project.tour.dto.convenience;

import java.util.List;
import java.util.UUID;

public record AssignConvenienceStaffResult(
        List<ConvenienceStaffAssignmentResponse> assigned,
        List<UUID> alreadyAssigned,
        List<UUID> invalidUserIds) {
}
