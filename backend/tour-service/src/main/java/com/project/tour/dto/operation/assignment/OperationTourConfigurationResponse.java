// src/main/java/com/project/tour/dto/tour/operation/OperationTourConfigurationResponse.java

package com.project.tour.dto.operation.assignment;

import java.util.List;
import java.util.UUID;

import com.project.tour.dto.operation.assignment.activitycruise.ActivityCruiseTourAssignmentResponse;
import com.project.tour.dto.operation.assignment.product.ProductTourAssignmentResponse;
import com.project.tour.dto.operation.assignment.service.ServiceTourAssignmentResponse;

public record OperationTourConfigurationResponse(

        UUID tourId,
        String tourCode,
        String tourName,

        List<ActivityCruiseTourAssignmentResponse> activities,

        List<ProductTourAssignmentResponse> products,

        List<ServiceTourAssignmentResponse> services,

        boolean configurationComplete

) {
}