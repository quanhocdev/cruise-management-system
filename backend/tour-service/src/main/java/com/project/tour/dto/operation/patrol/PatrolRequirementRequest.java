
package com.project.tour.dto.operation.patrol;

import java.util.List;

public record PatrolRequirementRequest(
        List<PatrolRequirementItemRequest> requirements) {
}
