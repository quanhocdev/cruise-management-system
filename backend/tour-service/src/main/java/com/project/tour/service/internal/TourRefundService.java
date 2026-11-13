package com.project.tour.service.internal;

import com.project.tour.dto.internal.RefundCalculationResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface TourRefundService {

    RefundCalculationResponse calculateRefund(
            UUID tourId,
            BigDecimal originalAmount);
}