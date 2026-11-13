package com.project.tour.controller.internal;

import com.project.tour.dto.internal.RefundCalculationResponse;
import com.project.tour.service.internal.TourRefundService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/internal/tours")
public class InternalTourRefundController {

    private final TourRefundService tourRefundService;

    public InternalTourRefundController(
            TourRefundService tourRefundService) {
        this.tourRefundService = tourRefundService;
    }

    @GetMapping("/{tourId}/refund-calculation")
    public ResponseEntity<RefundCalculationResponse> calculateRefund(
            @PathVariable UUID tourId,
            @RequestParam BigDecimal originalAmount) {
        RefundCalculationResponse response = tourRefundService.calculateRefund(
                tourId,
                originalAmount);

        return ResponseEntity.ok(response);
    }
}