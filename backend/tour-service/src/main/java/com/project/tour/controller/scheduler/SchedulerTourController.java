package com.project.tour.controller.scheduler;

import com.project.tour.dto.scheduler.SchedulerTourDtos.TourDetailResponse;
import com.project.tour.dto.scheduler.SchedulerTourDtos.TourSummaryResponse;
import com.project.tour.service.scheduler.SchedulerTourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scheduler/tours")
public class SchedulerTourController {

    private final SchedulerTourService schedulerTourService;

    public SchedulerTourController(SchedulerTourService schedulerTourService) {
        this.schedulerTourService = schedulerTourService;
    }

    // 1. Lấy danh sách tour theo trạng thái
    @GetMapping
    public ResponseEntity<List<TourSummaryResponse>> getActiveTours(
            @RequestParam(required = false) List<String> statuses) {
        return ResponseEntity.ok(schedulerTourService.getToursByStatuses(statuses));
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<TourDetailResponse> getTourDetails(@PathVariable UUID tourId) {
        return ResponseEntity.ok(schedulerTourService.getTourDetailsWithTracking(tourId));
    }
}