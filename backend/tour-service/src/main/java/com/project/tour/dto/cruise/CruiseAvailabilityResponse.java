package com.project.tour.dto.cruise;

import com.project.tour.model.enums.cruise.CruiseStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CruiseAvailabilityResponse(
        UUID id,
        String code,
        String name,
        CruiseStatus status,
        boolean isAvailable,
        String reason,
        List<ConflictingTourInfo> conflictingTours) {
    /**
     * Inner Record chứa thông tin ngắn gọn của Tour đang bị trùng lịch
     */
    public record ConflictingTourInfo(
            UUID tourId,
            String tourCode,
            String tourName,
            LocalDate startDate,
            LocalDate endDate) {
    }
}