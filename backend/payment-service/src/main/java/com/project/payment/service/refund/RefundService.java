
package com.project.payment.service.refund;

import com.project.payment.model.Payment;
import com.project.payment.model.PaymentRefund;
import com.project.payment.model.enums.PaymentStatus;
import com.project.payment.repository.PaymentRefundRepository;
import com.project.payment.repository.PaymentRepository;

import com.project.payment.service.refund.RefundModels.Accepted;
import com.project.payment.service.refund.RefundModels.Outcome;
import com.project.payment.service.refund.RefundModels.Receipt;
import com.project.payment.service.refund.RefundModels.Refund;
import com.project.payment.service.refund.RefundModels.Request;
import com.project.payment.service.refund.RefundModels.Summary;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

@Service
public class RefundService {

    private static final String STATUS_QUEUED = "QUEUED";
    private static final String STATUS_SENDING = "SENDING";
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final String STATUS_UNKNOWN = "UNKNOWN";
    private static final String STATUS_SUCCEEDED = "SUCCEEDED";
    private static final String STATUS_REJECTED = "REJECTED";

    private final PaymentRefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final RefundValidator refundValidator;
    private final RefundMapper refundMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public RefundService(
            PaymentRefundRepository refundRepository,
            PaymentRepository paymentRepository,
            RefundValidator refundValidator,
            RefundMapper refundMapper) {

        this.refundRepository = refundRepository;
        this.paymentRepository = paymentRepository;
        this.refundValidator = refundValidator;
        this.refundMapper = refundMapper;
    }

    // Tạo yêu cầu hoàn tiền
    @Transactional
    public Accepted create(Long paymentId, UUID actorId, Request request) {

        refundValidator.validateRequest(request);

        String requestHash = hash(paymentId, request);

        // Khóa idempotency theo clientRequestId
        entityManager.createNativeQuery("""
                SELECT pg_advisory_xact_lock(
                    hashtextextended(CAST(:requestId AS text), 0)
                )
                """)
                .setParameter("requestId", request.clientRequestId().toString())
                .getSingleResult();

        var previous = refundRepository.findByClientRequestId(
                request.clientRequestId());

        if (previous.isPresent()) {

            PaymentRefund existing = previous.get();

            if (!actorId.equals(existing.getActorId())) {
                throw new RefundException(
                        403,
                        "REFUND_REQUEST_FORBIDDEN");
            }

            if (!requestHash.equals(existing.getRequestHash())) {
                throw new RefundException(
                        409,
                        "IDEMPOTENCY_CONFLICT");
            }

            return new Accepted(
                    refundMapper.toReceipt(existing),
                    true);
        }

        // Khóa Payment để tránh hoàn tiền đồng thời
        Payment payment = entityManager.find(
                Payment.class,
                paymentId,
                LockModeType.PESSIMISTIC_WRITE);

        refundValidator.validatePayment(payment);

        BigDecimal totalAmount = payment.getAmount();

        BigDecimal refundedAmount = refundRepository.sumSucceededAmount(paymentId);

        BigDecimal pendingAmount = refundRepository.sumPendingAmount(paymentId);

        if (pendingAmount.signum() > 0) {
            throw new RefundException(
                    422,
                    "REFUND_RECONCILIATION_PENDING");
        }

        BigDecimal remainingAmount = totalAmount.subtract(refundedAmount);

        if (request.amount().compareTo(remainingAmount) > 0) {
            throw new RefundException(
                    422,
                    "REFUND_EXCEEDS_REMAINING");
        }

        PaymentRefund refund = new PaymentRefund();

        refund.setId(UUID.randomUUID());
        refund.setPaymentId(paymentId);
        refund.setClientRequestId(request.clientRequestId());
        refund.setRequestHash(requestHash);
        refund.setActorId(actorId);
        refund.setAmount(request.amount());
        refund.setReason(request.reason());
        refund.setStatus(STATUS_QUEUED);

        refund.setTransactionType(
                request.amount().compareTo(totalAmount) == 0
                        ? "02"
                        : "03");

        refund.setOriginalTransactionNo(
                payment.getTransactionCode());

        refund.setOriginalCreatedAt(
                payment.getCreatedAt());

        Instant now = Instant.now();

        refund.setCreatedAt(now);
        refund.setUpdatedAt(now);

        refundRepository.saveAndFlush(refund);

        return new Accepted(
                refundMapper.toReceipt(refund),
                false);
    }

    // Xem tổng quan và lịch sử hoàn tiền
    @Transactional(readOnly = true)
    public Summary summary(Long paymentId, int page, int size) {

        if (page < 0 || page > 100000 || size < 1 || size > 100) {
            throw new RefundException(400, "INVALID_PAGE");
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RefundException(404, "PAYMENT_NOT_FOUND"));

        BigDecimal paidAmount = payment.getAmount();

        BigDecimal refundedAmount = refundRepository.sumSucceededAmount(paymentId);

        BigDecimal pendingAmount = refundRepository.sumPendingAmount(paymentId);

        String blockedReason = null;

        try {
            refundValidator.validatePayment(payment);
        } catch (RefundException e) {
            blockedReason = e.getMessage();
        }

        if (blockedReason == null && pendingAmount.signum() > 0) {
            blockedReason = "REFUND_RECONCILIATION_PENDING";
        }

        if (blockedReason == null
                && paidAmount.compareTo(refundedAmount) <= 0) {
            blockedReason = "PAYMENT_FULLY_REFUNDED";
        }

        List<Refund> refunds = refundRepository
                .findByPaymentIdOrderByCreatedAtDesc(
                        paymentId,
                        PageRequest.of(page, size))
                .stream()
                .map(refundMapper::toRefund)
                .toList();

        BigDecimal refundableAmount = paidAmount
                .subtract(refundedAmount)
                .subtract(pendingAmount)
                .max(BigDecimal.ZERO);

        return new Summary(
                paymentId,
                paidAmount,
                refundedAmount,
                pendingAmount,
                refundableAmount,
                blockedReason == null,
                blockedReason,
                refunds,
                page,
                size);
    }

    // Xem chi tiết yêu cầu hoàn tiền
    @Transactional(readOnly = true)
    public Refund read(UUID refundId) {

        PaymentRefund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundException(404, "REFUND_NOT_FOUND"));

        return refundMapper.toRefund(refund);
    }

    // Đánh dấu yêu cầu hoàn tiền đã được Dispatcher nhận xử lý
    @Transactional
    public boolean claim(UUID refundId) {

        int updated = refundRepository.claimRefund(
                refundId,
                Instant.now());

        return updated == 1;
    }

    // Cập nhật kết quả từ nhà cung cấp thanh toán
    @Transactional
    public void complete(UUID refundId, Outcome outcome) {

        if (outcome == null
                || !Set.of(
                        STATUS_SUCCEEDED,
                        STATUS_REJECTED,
                        STATUS_PROCESSING,
                        STATUS_UNKNOWN).contains(outcome.status())) {

            throw new IllegalArgumentException(
                    "Invalid provider outcome");
        }

        PaymentRefund initialRefund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundException(404, "REFUND_NOT_FOUND"));

        // Khóa Payment trước Refund để thống nhất thứ tự khóa
        Payment payment = entityManager.find(
                Payment.class,
                initialRefund.getPaymentId(),
                LockModeType.PESSIMISTIC_WRITE);

        if (payment == null) {
            throw new RefundException(404, "PAYMENT_NOT_FOUND");
        }

        PaymentRefund refund = refundRepository.findByIdForUpdate(refundId)
                .orElseThrow(() -> new RefundException(404, "REFUND_NOT_FOUND"));

        if (Set.of(STATUS_SUCCEEDED, STATUS_REJECTED)
                .contains(refund.getStatus())) {
            return;
        }

        refund.setStatus(outcome.status());
        refund.setProviderCode(outcome.code());

        if (outcome.transactionNo() != null) {
            refund.setProviderTransactionNo(
                    outcome.transactionNo());
        }

        refund.setUpdatedAt(Instant.now());

        refundRepository.saveAndFlush(refund);

        if (STATUS_SUCCEEDED.equals(outcome.status())) {

            BigDecimal refundedAmount = refundRepository.sumSucceededAmount(
                    refund.getPaymentId());

            if (refundedAmount.compareTo(payment.getAmount()) >= 0) {

                payment.setStatus(PaymentStatus.REFUNDED);
                payment.setUpdatedAt(Instant.now());

                paymentRepository.save(payment);
            }
        }
    }

    // Lấy các yêu cầu đang chờ xử lý
    @Transactional
    public List<UUID> queued() {

        Instant now = Instant.now();

        refundRepository.markInterruptedRefunds(
                now.minusSeconds(120),
                now);

        return refundRepository
                .findTop10ByStatusOrderByCreatedAtAsc(STATUS_QUEUED)
                .stream()
                .map(PaymentRefund::getId)
                .toList();
    }

    // Lấy các yêu cầu cần đối soát
    @Transactional(readOnly = true)
    public List<UUID> awaitingReconciliation() {

        Instant cutoff = Instant.now().minusSeconds(60);

        return refundRepository
                .findTop10ByStatusInAndUpdatedAtBeforeOrderByUpdatedAtAsc(
                        List.of(STATUS_UNKNOWN, STATUS_PROCESSING),
                        cutoff)
                .stream()
                .map(PaymentRefund::getId)
                .toList();
    }

    // Tạo hash để kiểm tra idempotency
    private String hash(Long paymentId, Request request) {

        try {
            TreeMap<String, String> payload = new TreeMap<>();

            payload.put(
                    "amount",
                    request.amount()
                            .stripTrailingZeros()
                            .toPlainString());

            payload.put("paymentId", paymentId.toString());
            payload.put("reason", request.reason());

            String raw = payload.toString();

            byte[] digest = MessageDigest
                    .getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(digest);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot hash refund request",
                    e);
        }
    }
}
