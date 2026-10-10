
package com.project.tour.controller.internal;

import com.project.tour.dto.internal.VisitTourUsageBatchRequest;
import com.project.tour.dto.internal.VisitTourUsageInfo;
import com.project.tour.dto.internal.VisitTourUsageManagementInfo;
import com.project.tour.service.internal.VisitTourInternalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/visit-tours")
public class VisitTourInternalController {

    private final VisitTourInternalService visitTourInternalService;

    public VisitTourInternalController(
            VisitTourInternalService visitTourInternalService) {
        this.visitTourInternalService = visitTourInternalService;
    }

    @GetMapping("/{visitTourId}/usage-info")
    public ResponseEntity<VisitTourUsageInfo> getVisitTourUsageInfo(
            @PathVariable UUID visitTourId,
            @RequestParam UUID tourPackageId) {

        return ResponseEntity.ok(
                visitTourInternalService.getVisitTourUsageInfo(
                        visitTourId,
                        tourPackageId));
    }

    @PostMapping("/usage-info/batch")
    public ResponseEntity<List<VisitTourUsageManagementInfo>> getVisitTourUsageManagementInfo(
            @RequestBody VisitTourUsageBatchRequest request) {

        return ResponseEntity.ok(
                visitTourInternalService.getVisitTourUsageManagementInfo(
                        request.visitTourIds()));
    }
}
