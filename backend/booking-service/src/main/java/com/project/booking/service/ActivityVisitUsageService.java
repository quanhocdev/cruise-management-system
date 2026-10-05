package com.project.booking.service;

import com.project.booking.dto.ActivityVisitUsageRequest;
import com.project.booking.dto.ActivityVisitUsageResponse;

import java.util.List;

public interface ActivityVisitUsageService {

    ActivityVisitUsageResponse create(
            ActivityVisitUsageRequest request);

    List<ActivityVisitUsageResponse> getByBookingPassengerId(
            Long bookingPassengerId);

    List<ActivityVisitUsageResponse> getByUserId(
            Long userId);
}