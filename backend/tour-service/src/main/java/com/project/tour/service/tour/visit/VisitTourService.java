// src/main/java/com/project/tour/service/tour/visit/VisitTourService.java

package com.project.tour.service.tour.visit;

import com.project.tour.dto.visit.CreateVisitTourRequest;
import com.project.tour.dto.visit.UpdateVisitTourRequest;
import com.project.tour.dto.visit.VisitTourResponse;

import java.util.List;
import java.util.UUID;

public interface VisitTourService {

    List<VisitTourResponse> getAll();

    VisitTourResponse getById(UUID id);

    List<VisitTourResponse> getByScheduleStop(UUID scheduleStopId);

    List<VisitTourResponse> getByTour(UUID tourId);

    VisitTourResponse create(
            UUID scheduleStopId,
            CreateVisitTourRequest request);

    VisitTourResponse update(
            UUID id,
            UpdateVisitTourRequest request);

    void delete(UUID id);
}