package com.project.tour.controller.tour;

import com.project.tour.dto.visit.ShoreTourConfigurationResponse;
import com.project.tour.model.enums.visit.VisitTourStatus;
import com.project.tour.service.tour.visit.ShoreTourConfigurationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/shore/tours")
public class ShoreTourConfigurationController {

    private final ShoreTourConfigurationService shoreTourConfigurationService;

    public ShoreTourConfigurationController(
            ShoreTourConfigurationService shoreTourConfigurationService) {

        this.shoreTourConfigurationService = shoreTourConfigurationService;
    }

    /**
     * Lấy toàn bộ cấu hình Shore của Tour.
     *
     * Mặc định:
     * WAITING_CONFIG
     * AVAILABLE
     * IN_PROGRESS
     * COMPLETED
     *
     * Có thể lọc:
     *
     * ?status=WAITING_CONFIG
     * ?status=AVAILABLE
     * ?status=IN_PROGRESS
     * ?status=COMPLETED
     */
    @GetMapping("/{tourId}/configuration")
    public ResponseEntity<ShoreTourConfigurationResponse> getConfiguration(
            @PathVariable UUID tourId,
            @RequestParam(required = false) VisitTourStatus status) {

        return ResponseEntity.ok(
                shoreTourConfigurationService
                        .getConfiguration(tourId, status));
    }
}
