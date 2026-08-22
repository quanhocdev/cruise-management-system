package com.project.tour.service.tour.visit;

import com.project.tour.dto.tour.TourResponse;
import com.project.tour.dto.visit.ShoreTourConfigurationResponse;
import com.project.tour.model.enums.visit.VisitTourStatus;
import java.util.List;
import java.util.UUID;

public interface ShoreTourConfigurationService {

    List<TourResponse> getAvailableTours();

    ShoreTourConfigurationResponse getConfiguration(
            UUID tourId,
            VisitTourStatus status);
}