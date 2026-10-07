package com.project.booking.service.finance;

import com.project.booking.dto.finance.CheckoutPreviewResponse;
import com.project.booking.model.Bill;

public interface CheckoutService {

    CheckoutPreviewResponse getCheckoutPreview(Long bookingId);

    Bill confirmCheckout(Long bookingId);
}