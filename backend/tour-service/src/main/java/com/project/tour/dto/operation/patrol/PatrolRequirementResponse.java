
package com.project.tour.dto.operation.patrol;

import java.util.UUID;

public record PatrolRequirementResponse(
        UUID cruiseDeckId,
        Integer deckNumber,
        Integer requiredCount) {
}
