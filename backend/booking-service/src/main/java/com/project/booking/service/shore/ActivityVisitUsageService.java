package com.project.booking.service.shore;

import com.project.booking.dto.onboard.ActivityVisitUsageRequest;
import com.project.booking.dto.shore.ActivityVisitUsageResponse;

import java.util.List;

public interface ActivityVisitUsageService {

        ActivityVisitUsageResponse create(
                        ActivityVisitUsageRequest request);

        List<ActivityVisitUsageResponse> getByBookingPassengerId(
                        Long bookingPassengerId);

        List<ActivityVisitUsageResponse> getByUserId(
                        Long userId);
}