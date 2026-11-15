package com.project.booking.service.finance.refund;

import com.project.booking.dto.refund.BookingRefundResponse;
import com.project.booking.dto.refund.FinanceRefundRejectRequest;
import com.project.booking.model.enums.BookingRefundStatus;

import java.util.List;
import java.util.UUID;

public interface FinanceBookingRefundService {

    List<BookingRefundResponse> getRefunds(BookingRefundStatus status);

    BookingRefundResponse getRefundDetail(Long refundId);

    BookingRefundResponse approveRefund(Long refundId, UUID reviewerId);

    BookingRefundResponse rejectRefund(
            Long refundId,
            UUID reviewerId,
            FinanceRefundRejectRequest request);
}