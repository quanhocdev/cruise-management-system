
package com.project.booking.service.finance.refund;

import com.project.booking.exception.AppException;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingRefund;
import com.project.booking.model.enums.BookingRefundStatus;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingRefundRepository;
import com.project.booking.repository.BookingRepository;
import com.project.common.event.RefundResultEvent;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
public class FinanceRefundResultServiceImpl
        implements FinanceRefundResultService {

    private final BookingRefundRepository bookingRefundRepository;
    private final BookingRepository bookingRepository;

    public FinanceRefundResultServiceImpl(
            BookingRefundRepository bookingRefundRepository,
            BookingRepository bookingRepository) {

        this.bookingRefundRepository = bookingRefundRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void processRefundResult(RefundResultEvent event) {

        if (event == null || event.paymentClientRequestId() == null
                || event.status() == null) {
            throw new IllegalArgumentException(
                    "Invalid refund result event");
        }

        BookingRefund refund = bookingRefundRepository
                .findByPaymentClientRequestId(
                        event.paymentClientRequestId())
                .orElseThrow(() -> new AppException(
                        "Booking refund not found for payment request: "
                                + event.paymentClientRequestId(),
                        HttpStatus.NOT_FOUND));

        // Event trùng hoặc event đến trễ không được làm lùi trạng thái cuối.
        if (refund.getStatus() == BookingRefundStatus.COMPLETED
                || refund.getStatus() == BookingRefundStatus.FAILED) {
            return;
        }

        Booking booking = bookingRepository.findById(refund.getBookingId())
                .orElseThrow(() -> new AppException(
                        "Booking not found: " + refund.getBookingId(),
                        HttpStatus.NOT_FOUND));

        String paymentStatus = event.status()
                .trim()
                .toUpperCase(Locale.ROOT);

        switch (paymentStatus) {

            case "SUCCEEDED" -> {
                refund.setStatus(BookingRefundStatus.COMPLETED);
                booking.setStatus(BookingStatus.REFUNDED);
            }

            case "REJECTED" -> {
                refund.setStatus(BookingRefundStatus.FAILED);
                booking.setStatus(BookingStatus.CONFIRMED);
            }

            case "QUEUED", "SENDING", "PROCESSING", "UNKNOWN" -> {
                refund.setStatus(BookingRefundStatus.PROCESSING);
                booking.setStatus(BookingStatus.REFUND_PROCESSING);
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported payment refund status: "
                            + event.status());
        }

        bookingRefundRepository.save(refund);
        bookingRepository.save(booking);
    }
}
