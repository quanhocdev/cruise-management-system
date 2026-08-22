package com.project.tour.controller.tour;

import com.project.tour.dto.tour.TourResponse;
import com.project.tour.dto.visit.ShoreTourConfigurationResponse;
import com.project.tour.model.enums.visit.VisitTourStatus;
import com.project.tour.service.tour.visit.ShoreTourConfigurationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shore/tours")
public class ShoreTourConfigurationController {

    private final ShoreTourConfigurationService shoreTourConfigurationService;

    public ShoreTourConfigurationController(
            ShoreTourConfigurationService shoreTourConfigurationService) {

        this.shoreTourConfigurationService = shoreTourConfigurationService;
    }

    // =====================================================
    // GET TOURS FOR SHORE
    // =====================================================

    /**
     * Lấy danh sách các Tour mà Shore được phép quản lý.
     *
     * Chỉ lấy:
     * APPROVED
     * READY
     * IN_PROGRESS
     * COMPLETED
     *
     * Không lấy:
     * DRAFT
     * APPROVAL_PENDING
     * CANCELLED
     */
    @GetMapping
    public ResponseEntity<List<TourResponse>> getAvailableTours() {

        return ResponseEntity.ok(
                shoreTourConfigurationService.getAvailableTours());
    }

    // =====================================================
    // GET TOUR CONFIGURATION
    // =====================================================

    /**
     * Lấy toàn bộ cấu hình Shore của một Tour.
     *
     * Có thể lọc VisitTour theo status:
     *
     * ?status=NOT_STARTED
     * ?status=IN_PROGRESS
     * ?status=COMPLETED
     * ?status=DELAYED
     * ?status=CANCELLED
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