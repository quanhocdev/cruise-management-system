// src/main/java/com/project/tour/controller/tour/operation/OperationTourConfigurationController.java

package com.project.tour.controller.tour.operation;

import com.project.tour.dto.tour.operation.OperationTourConfigurationResponse;
import com.project.tour.service.tour.operation.OperationTourConfigurationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/operation/tours")
public class OperationTourConfigurationController {

    private final OperationTourConfigurationService configurationService;

    public OperationTourConfigurationController(
            OperationTourConfigurationService configurationService) {

        this.configurationService = configurationService;
    }

    /**
     * Lấy toàn bộ cấu hình của một Tour cho Operation.
     *
     * Bao gồm:
     * - Activity
     * - Product
     * - Service
     *
     * Đồng thời trả về configurationComplete
     * để xác định Tour đã đủ dữ liệu để tạo Package hay chưa.
     */
    @GetMapping("/{tourId}/configuration")
    public ResponseEntity<OperationTourConfigurationResponse> getConfiguration(
            @PathVariable UUID tourId) {

        return ResponseEntity.ok(
                configurationService.getConfiguration(tourId));
    }
}