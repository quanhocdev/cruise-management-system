
package com.project.tour.dto.operation.patrol;

import java.util.UUID;

public record PatrolRequirementItemRequest(
        UUID cruiseDeckId,
        Integer requiredCount) {
}
