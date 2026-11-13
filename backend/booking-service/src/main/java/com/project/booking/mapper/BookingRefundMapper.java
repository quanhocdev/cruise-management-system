package com.project.booking.mapper;

import com.project.booking.dto.refund.BookingRefundResponse;
import com.project.booking.model.BookingRefund;
import org.springframework.stereotype.Component;

@Component
public class BookingRefundMapper {

    public BookingRefundResponse toResponse(BookingRefund refund) {

        if (refund == null) {
            return null;
        }

        return new BookingRefundResponse(
                refund.getId(),
                refund.getBookingId(),
                refund.getRequestedByUserId(),
                refund.getReason(),

                refund.getPolicyRefundRuleId(),
                refund.getDepartureDate(),
                refund.getDaysBeforeDeparture(),

                refund.getOriginalAmount(),
                refund.getRefundPercentage(),
                refund.getCalculatedRefundAmount(),

                refund.getStatus(),

                refund.getReviewedByUserId(),
                refund.getReviewedAt(),
                refund.getRejectionReason(),

                refund.getCreatedAt(),
                refund.getUpdatedAt());
    }
}