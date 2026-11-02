package com.project.booking.dto.payment;

import java.math.BigDecimal;

public record BillCollectionResponse(Long billId, BigDecimal collected, BigDecimal returned,
                                     BigDecimal refundPending, long successfulPayments, boolean needsReview) {}
