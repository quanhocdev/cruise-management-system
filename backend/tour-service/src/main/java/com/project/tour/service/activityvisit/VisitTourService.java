package com.project.tour.service.activityvisit;

import com.project.common.event.TourMasterSyncEvent;
import com.project.tour.dto.activityvisit.CreateVisitTourRequest;
import com.project.tour.dto.activityvisit.TourVisitSyncResponse;
import com.project.tour.dto.activityvisit.UpdateVisitTourRequest;
import com.project.tour.dto.activityvisit.VisitTourResponse;

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

    VisitTourResponse createVisitTourFromEvent(
            UUID tourId,
            UUID scheduleStopId);

    void syncTourMasterData(TourMasterSyncEvent event);

    List<TourVisitSyncResponse> getAllMasterTours();

    TourVisitSyncResponse getMasterTourById(UUID tourId);

    void delete(UUID id);
}
