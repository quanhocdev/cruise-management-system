
package com.project.payment.service.refund;

import com.project.common.event.RefundApprovedEvent;
import com.project.payment.dto.refund.RefundModels;
import com.project.payment.model.Payment;
import com.project.payment.model.enums.PaymentMethod;
import com.project.payment.model.enums.PaymentReferenceType;
import com.project.payment.model.enums.PaymentStatus;
import com.project.payment.repository.PaymentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundApprovedEventConsumerService {

    private static final Logger log = LoggerFactory.getLogger(RefundApprovedEventConsumerService.class);

    private final PaymentRepository paymentRepository;
    private final RefundService refundService;

    public RefundApprovedEventConsumerService(
            PaymentRepository paymentRepository,
            RefundService refundService) {
        this.paymentRepository = paymentRepository;
        this.refundService = refundService;
    }

    @Transactional
    public void processRefundApproved(RefundApprovedEvent event) {

        if (event == null
                || event.bookingRefundId() == null
                || event.bookingId() == null
                || event.paymentClientRequestId() == null
                || event.amount() == null
                || event.actorId() == null) {

            throw new IllegalArgumentException(
                    "Invalid refund approved event");
        }

        log.info(
                "Processing approved refund. bookingRefundId={}, bookingId={}, amount={}",
                event.bookingRefundId(),
                event.bookingId(),
                event.amount());

        // 1. Tìm khoản thanh toán gốc của Booking.
        Payment payment = paymentRepository
                .findByReferenceIdAndReferenceType(
                        event.bookingId(),
                        PaymentReferenceType.BOOKING)
                .orElseThrow(() -> new IllegalStateException(
                        "Payment not found for booking: "
                                + event.bookingId()));

        // 2. Chỉ hỗ trợ hoàn tiền qua VNPay.
        if (payment.getMethod() != PaymentMethod.VNPAY) {
            throw new IllegalStateException(
                    "Only VNPay payments support this refund flow");
        }

        // 3. Khoản thanh toán phải thành công.
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException(
                    "Original payment is not in SUCCESS status");
        }

        // 4. Kiểm tra số tiền yêu cầu.
        if (event.amount().signum() <= 0
                || event.amount().compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Invalid refund amount");
        }

        // 5. Tạo yêu cầu hoàn tiền bằng RefundService hiện có.
        // clientRequestId được tạo từ BookingRefund và giữ cố định
        // để RefundService xử lý idempotency khi Kafka gửi trùng.
        RefundModels.Request request = new RefundModels.Request(
                event.paymentClientRequestId(),
                event.amount(),
                event.reason());

        RefundModels.Accepted accepted = refundService.create(
                payment.getId(),
                event.actorId(),
                request);

        log.info(
                "Refund request accepted. bookingRefundId={}, paymentId={}, paymentRefundId={}, status={}, replay={}",
                event.bookingRefundId(),
                payment.getId(),
                accepted.receipt().refundId(),
                accepted.receipt().status(),
                accepted.replay());
    }
}
