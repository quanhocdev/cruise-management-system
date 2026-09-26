package com.project.tour.controller.activityvisit;

import com.project.tour.dto.activityvisit.VisitTourResponse;
import com.project.tour.service.activityvisit.ActivityVisitTourConfigurationService;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shore/visit-tour-configurations")
public class ActivityVisitTourConfigurationController {

    private final ActivityVisitTourConfigurationService configurationService;

    public ActivityVisitTourConfigurationController(
            ActivityVisitTourConfigurationService configurationService) {

        this.configurationService = configurationService;
    }

    @PostMapping("/{tourId}/complete")
    public ResponseEntity<Void> complete(
            @PathVariable UUID tourId) {

        configurationService.complete(tourId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/configuration-history/{tourId}")
    public ResponseEntity<List<VisitTourResponse>> getConfigurationHistoryDetail(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                configurationService.getConfigurationHistoryDetail(tourId));
    }
}
