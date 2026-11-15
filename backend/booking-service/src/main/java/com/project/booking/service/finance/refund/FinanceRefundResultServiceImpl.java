package com.project.booking.service.finance.refund;

import com.project.booking.exception.AppException;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingRefund;
import com.project.booking.model.enums.BookingRefundStatus;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingRefundRepository;
import com.project.booking.repository.BookingRepository;
import com.project.booking.service.redis.TourRedisService;
import com.project.common.event.RefundResultEvent;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;

@Service
@Transactional
public class FinanceRefundResultServiceImpl
        implements FinanceRefundResultService {

    private final BookingRefundRepository bookingRefundRepository;
    private final BookingRepository bookingRepository;
    private final TourRedisService tourRedisService;

    public FinanceRefundResultServiceImpl(
            BookingRefundRepository bookingRefundRepository,
            BookingRepository bookingRepository,
            TourRedisService tourRedisService) {

        this.bookingRefundRepository = bookingRefundRepository;
        this.bookingRepository = bookingRepository;
        this.tourRedisService = tourRedisService;
    }

    @Override
    public void processRefundResult(RefundResultEvent event) {

        if (event == null
                || event.paymentClientRequestId() == null
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

        String paymentStatus = event.status()
                .trim()
                .toUpperCase(Locale.ROOT);

        // Lấy booking để có tourPackageId và numberOfRooms.
        Booking booking = bookingRepository.findById(refund.getBookingId())
                .orElseThrow(() -> new AppException(
                        "Booking not found: " + refund.getBookingId(),
                        HttpStatus.NOT_FOUND));

        // Event SUCCEEDED bị gửi lại:
        // vẫn thử hoàn phòng, Redis sẽ chống cộng trùng.
        if (refund.getStatus() == BookingRefundStatus.COMPLETED) {
            if ("SUCCEEDED".equals(paymentStatus)) {
                scheduleReleaseRooms(booking, event.paymentClientRequestId());
            }
            return;
        }

        // Event trùng hoặc đến trễ không được làm lùi trạng thái cuối.
        if (refund.getStatus() == BookingRefundStatus.FAILED) {
            return;
        }

        switch (paymentStatus) {

            case "SUCCEEDED" -> {
                refund.setStatus(BookingRefundStatus.COMPLETED);
                booking.setStatus(BookingStatus.REFUNDED);

                // Chỉ hoàn phòng khi refund thực sự thành công.
                scheduleReleaseRooms(
                        booking,
                        event.paymentClientRequestId());
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

    private void scheduleReleaseRooms(
            Booking booking,
            java.util.UUID refundRequestId) {

        if (booking.getTourPackageId() == null
                || booking.getNumberOfRooms() == null
                || booking.getNumberOfRooms() <= 0) {
            throw new IllegalStateException(
                    "Invalid booking room information: "
                            + booking.getId());
        }

        // Chờ DB commit xong mới cập nhật Redis.
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        tourRedisService.releasePackageRooms(
                                booking.getTourPackageId(),
                                booking.getNumberOfRooms(),
                                refundRequestId);
                    }
                });
    }
}