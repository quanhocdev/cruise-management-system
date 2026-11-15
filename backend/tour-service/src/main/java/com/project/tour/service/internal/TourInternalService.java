package com.project.tour.service.internal;

import com.project.tour.dto.internal.TourBookingInfoResponse;
import java.util.List;
import java.util.UUID;
import com.project.tour.dto.scheduler.SchedulerTourDtos.TourDetailResponse;

public interface TourInternalService {

    TourBookingInfoResponse getTourBookingInfo(UUID tourId);

    List<TourBookingInfoResponse> getToursBookingInfo(List<UUID> tourIds);

    TourDetailResponse getTourTrackingDetails(UUID tourId);
}