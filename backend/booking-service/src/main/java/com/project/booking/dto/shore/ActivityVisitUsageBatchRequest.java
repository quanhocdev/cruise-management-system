
package com.project.booking.dto.shore;

import java.util.List;
import java.util.UUID;

public record ActivityVisitUsageBatchRequest(
        List<UUID> visitTourIds) {
}
