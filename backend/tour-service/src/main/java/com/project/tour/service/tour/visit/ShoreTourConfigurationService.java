package com.project.tour.service.tour.visit;

import com.project.tour.dto.visit.ShoreTourConfigurationResponse;

import java.util.UUID;

public interface ShoreTourConfigurationService {

    ShoreTourConfigurationResponse getConfiguration(
            UUID tourId);
}