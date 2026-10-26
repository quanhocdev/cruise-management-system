
package com.project.tour.dto.onboard;

import java.util.List;
import java.util.UUID;

public record AssignOnboardStaffResult(
        List<OnboardStaffAssignmentResponse> assigned,
        List<UUID> alreadyAssigned,
        List<UUID> invalidUserIds) {
}
