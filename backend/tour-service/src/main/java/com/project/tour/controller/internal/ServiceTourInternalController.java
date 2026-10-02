package com.project.tour.controller.internal;

import com.project.tour.dto.internal.ServiceUsageInfo;
import com.project.tour.service.internal.ServiceTourInternalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/service-tours")
public class ServiceTourInternalController {

    private final ServiceTourInternalService serviceTourInternalService;

    public ServiceTourInternalController(
            ServiceTourInternalService serviceTourInternalService) {

        this.serviceTourInternalService = serviceTourInternalService;
    }

    @GetMapping("/{serviceTourId}/usage-info")
    public ResponseEntity<ServiceUsageInfo> getServiceUsageInfo(
            @PathVariable UUID serviceTourId,
            @RequestParam UUID tourPackageId) {

        return ResponseEntity.ok(
                serviceTourInternalService.getServiceUsageInfo(
                        serviceTourId,
                        tourPackageId));
    }
}