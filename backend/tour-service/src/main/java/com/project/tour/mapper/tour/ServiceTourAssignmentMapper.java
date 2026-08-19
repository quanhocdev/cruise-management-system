package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.operation.ServiceTourAssignmentResponse;
import com.project.tour.model.ServiceTour;
import org.springframework.stereotype.Component;

@Component
public class ServiceTourAssignmentMapper {

    public ServiceTourAssignmentResponse toResponse(ServiceTour entity) {

        return new ServiceTourAssignmentResponse(
                entity.getId(),

                entity.getTour() != null
                        ? entity.getTour().getId()
                        : null,

                entity.getCruiseArea() != null
                        ? entity.getCruiseArea().getId()
                        : null,

                entity.getService() != null
                        ? entity.getService().getId()
                        : null,

                entity.getMaxPassengers(),

                entity.getDurationMinutes(),

                entity.getStatus(),

                entity.getCreatedAt(),

                entity.getUpdatedAt());
    }
}