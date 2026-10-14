package com.project.booking.service.finance.checkout;

import com.project.booking.dto.finance.CheckoutPreviewResponse;
import com.project.booking.dto.finance.CheckoutResponse;

public interface CheckoutService {

    CheckoutPreviewResponse getCheckoutPreview(Long bookingId);

    CheckoutResponse confirmCheckout(Long bookingId);
}