package com.project.booking.service.finance;

import com.project.booking.dto.finance.CheckoutPreviewResponse;

public interface CheckoutService {

    CheckoutPreviewResponse getCheckoutPreview(Long bookingId);
}