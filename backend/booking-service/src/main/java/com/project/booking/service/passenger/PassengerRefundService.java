package com.project.booking.service.passenger;

import com.project.booking.dto.refund.BookingRefundPreviewResponse;
import com.project.booking.dto.refund.BookingRefundRequest;
import com.project.booking.dto.refund.BookingRefundResponse;

import java.util.List;
import java.util.UUID;

public interface PassengerRefundService {

        BookingRefundPreviewResponse preview(
                        Long bookingId,
                        UUID userId);

        BookingRefundResponse create(
                        Long bookingId,
                        UUID userId,
                        BookingRefundRequest request);

        BookingRefundResponse getLatest(
                        Long bookingId,
                        UUID userId);

        List<BookingRefundResponse> getMine(UUID userId);
}