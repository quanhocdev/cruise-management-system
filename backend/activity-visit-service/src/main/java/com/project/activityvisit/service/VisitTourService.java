// src/main/java/com/project/tour/service/tour/visit/VisitTourService.java

package com.project.activityvisit.service;

import java.util.List;
import java.util.UUID;

import com.project.activityvisit.dto.CreateVisitTourRequest;
import com.project.activityvisit.dto.TourVisitSyncResponse;
import com.project.activityvisit.dto.UpdateVisitTourRequest;
import com.project.activityvisit.dto.VisitTourResponse;
import com.project.common.event.TourMasterSyncEvent;

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