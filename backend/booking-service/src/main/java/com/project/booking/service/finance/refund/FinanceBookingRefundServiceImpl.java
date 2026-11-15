package com.project.booking.service.finance.refund;

import com.project.booking.dto.refund.BookingRefundResponse;
import com.project.booking.dto.refund.FinanceRefundRejectRequest;
import com.project.booking.exception.AppException;
import com.project.booking.kafka.RefundApprovedEventProducer;
import com.project.booking.mapper.BookingRefundMapper;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingRefund;
import com.project.booking.model.enums.BookingRefundStatus;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingRefundRepository;
import com.project.booking.repository.BookingRepository;
import com.project.common.event.RefundApprovedEvent;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FinanceBookingRefundServiceImpl
        implements FinanceBookingRefundService {

    private final BookingRefundRepository bookingRefundRepository;
    private final BookingRepository bookingRepository;
    private final BookingRefundMapper bookingRefundMapper;
    private final RefundApprovedEventProducer refundApprovedEventProducer;

    public FinanceBookingRefundServiceImpl(
            BookingRefundRepository bookingRefundRepository,
            BookingRepository bookingRepository,
            BookingRefundMapper bookingRefundMapper,
            RefundApprovedEventProducer refundApprovedEventProducer) {

        this.bookingRefundRepository = bookingRefundRepository;
        this.bookingRepository = bookingRepository;
        this.bookingRefundMapper = bookingRefundMapper;
        this.refundApprovedEventProducer = refundApprovedEventProducer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingRefundResponse> getRefunds(
            BookingRefundStatus status) {

        // Mặc định chỉ hiển thị yêu cầu đang chờ Finance xét duyệt.
        BookingRefundStatus filterStatus = status == null
                ? BookingRefundStatus.PENDING
                : status;

        return bookingRefundRepository
                .findByStatusOrderByCreatedAtAsc(filterStatus)
                .stream()
                .map(bookingRefundMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingRefundResponse getRefundDetail(Long refundId) {

        BookingRefund refund = findRefund(refundId);

        return bookingRefundMapper.toResponse(refund);
    }

    @Override
    public BookingRefundResponse approveRefund(
            Long refundId,
            UUID reviewerId) {

        if (reviewerId == null) {
            throw new AppException(
                    "Reviewer information is invalid",
                    HttpStatus.UNAUTHORIZED);
        }

        BookingRefund refund = findRefund(refundId);

        // Chỉ yêu cầu PENDING mới được Finance duyệt.
        if (refund.getStatus() != BookingRefundStatus.PENDING) {
            throw new AppException(
                    "Only PENDING refund requests can be approved",
                    HttpStatus.CONFLICT);
        }

        Booking booking = findBooking(refund.getBookingId());

        if (booking.getStatus() != BookingStatus.PENDING_REFUND) {
            throw new AppException(
                    "Booking is not waiting for refund approval",
                    HttpStatus.CONFLICT);
        }

        if (refund.getPaymentClientRequestId() == null) {
            throw new AppException(
                    "Refund is missing its payment request identifier",
                    HttpStatus.CONFLICT);
        }

        BigDecimal amount = refund.getCalculatedRefundAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(
                    "Refund amount must be greater than zero",
                    HttpStatus.CONFLICT);
        }

        // 1. Ghi nhận Finance đã duyệt.
        refund.setStatus(BookingRefundStatus.APPROVED);
        refund.setReviewedByUserId(reviewerId);
        refund.setReviewedAt(LocalDateTime.now());
        refund.setRejectionReason(null);

        bookingRefundRepository.save(refund);

        // 2. Booking chuyển sang trạng thái đang xử lý hoàn tiền.
        booking.setStatus(BookingStatus.REFUND_PROCESSING);
        bookingRepository.save(booking);

        // 3. Tạo event gửi sang Payment Service sau khi transaction commit.
        RefundApprovedEvent event = new RefundApprovedEvent(
                refund.getId(),
                refund.getBookingId(),
                refund.getPaymentClientRequestId(),
                refund.getCalculatedRefundAmount(),
                refund.getReason(),
                reviewerId);

        refundApprovedEventProducer.sendAfterCommit(event);

        return bookingRefundMapper.toResponse(refund);
    }

    @Override
    public BookingRefundResponse rejectRefund(
            Long refundId,
            UUID reviewerId,
            FinanceRefundRejectRequest request) {

        if (reviewerId == null) {
            throw new AppException(
                    "Reviewer information is invalid",
                    HttpStatus.UNAUTHORIZED);
        }

        if (request == null
                || request.reason() == null
                || request.reason().isBlank()) {

            throw new AppException(
                    "Rejection reason is required",
                    HttpStatus.BAD_REQUEST);
        }

        BookingRefund refund = findRefund(refundId);

        if (refund.getStatus() != BookingRefundStatus.PENDING) {
            throw new AppException(
                    "Only PENDING refund requests can be rejected",
                    HttpStatus.CONFLICT);
        }

        Booking booking = findBooking(refund.getBookingId());

        if (booking.getStatus() != BookingStatus.PENDING_REFUND) {
            throw new AppException(
                    "Booking is not waiting for refund approval",
                    HttpStatus.CONFLICT);
        }

        // 1. Ghi nhận Finance từ chối.
        refund.setStatus(BookingRefundStatus.REJECTED);
        refund.setReviewedByUserId(reviewerId);
        refund.setReviewedAt(LocalDateTime.now());
        refund.setRejectionReason(request.reason().trim());

        bookingRefundRepository.save(refund);

        // 2. Booking quay về trạng thái đã thanh toán.
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Không gửi event sang Payment Service khi bị từ chối.
        return bookingRefundMapper.toResponse(refund);
    }

    private BookingRefund findRefund(Long refundId) {

        return bookingRefundRepository.findById(refundId)
                .orElseThrow(() -> new AppException(
                        "Refund request not found: " + refundId,
                        HttpStatus.NOT_FOUND));
    }

    private Booking findBooking(Long bookingId) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(
                        "Booking not found: " + bookingId,
                        HttpStatus.NOT_FOUND));
    }
}