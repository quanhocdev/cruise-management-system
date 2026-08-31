package com.project.activityvisit.controller;

import com.project.activityvisit.dto.HistoryActivityVisitTourResponse;
import com.project.activityvisit.dto.VisitTourResponse;
import com.project.activityvisit.service.ActivityVisitTourConfigurationService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/shore/visit-tour-configurations")
public class ActivityVisitTourConfigurationController {

    private final ActivityVisitTourConfigurationService configurationService;

    public ActivityVisitTourConfigurationController(
            ActivityVisitTourConfigurationService configurationService) {

        this.configurationService = configurationService;
    }

    /**
     * Hoàn thành cấu hình tất cả VisitTour của một Tour.
     */
    @PostMapping("/{tourId}/complete")
    public ResponseEntity<Void> complete(
            @PathVariable UUID tourId) {

        configurationService.complete(tourId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/configuration-history")
    public ResponseEntity<List<HistoryActivityVisitTourResponse>> getConfigurationHistory() {

        return ResponseEntity.ok(
                configurationService.getConfigurationHistory());
    }

    @GetMapping("/configuration-history/{tourId}")
    public ResponseEntity<List<VisitTourResponse>> getConfigurationHistoryDetail(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                configurationService.getConfigurationHistoryDetail(tourId));
    }
}