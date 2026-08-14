package com.project.payment.service;

import com.project.payment.dto.*;
import com.project.payment.client.BookingClient;
import com.project.payment.client.BookingPaymentContext;
import com.project.payment.exception.PaymentException;
import com.project.payment.mapper.PaymentMapper;
import com.project.payment.model.Payment;
import com.project.payment.model.enums.*;
import com.project.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final Map<PaymentMethod, PaymentProvider> providers;
    private final long timeoutMinutes;
    private final BookingClient bookingClient;

    public PaymentServiceImpl(PaymentRepository repository, PaymentMapper mapper,
                              List<PaymentProvider> paymentProviders,
                              BookingClient bookingClient,
                              @Value("${vnpay.payment-timeout-minutes:15}") long timeoutMinutes) {
        this.repository = repository; this.mapper = mapper; this.bookingClient = bookingClient;
        this.timeoutMinutes = timeoutMinutes;
        providers = new EnumMap<>(PaymentMethod.class);
        paymentProviders.forEach(provider -> providers.put(provider.getPaymentMethod(), provider));
    }

    @Override @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request, Long payerId, String clientIp) {
        if (request.getMethod() != PaymentMethod.VNPAY)
            throw new PaymentException("Payment method is not available yet: " + request.getMethod());
        if (request.getReferenceType() != PaymentReferenceType.BOOKING)
            throw new PaymentException("Payment reference type is not available yet: " + request.getReferenceType());
        BookingPaymentContext booking = bookingClient.getPaymentContext(request.getReferenceId());
        validateBooking(request, payerId, booking);
        Instant now = Instant.now();
        Payment payment = mapper.toEntity(request);
        payment.setAmount(booking.totalAmount());
        payment.setPayerId(payerId);
        payment.setStatus(PaymentStatus.PENDING); payment.setCreatedAt(now); payment.setUpdatedAt(now);
        payment.setExpiresAt(now.plus(timeoutMinutes, ChronoUnit.MINUTES));
        Payment saved = repository.save(payment);
        saved.setPaymentUrl(provider().createPaymentUrl(saved, clientIp));
        saved.setUpdatedAt(Instant.now());
        return mapper.toResponse(repository.save(saved));
    }

    @Override @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id, Long requesterId, boolean privileged) {
        Payment payment = find(id);
        if (!privileged && !payment.getPayerId().equals(requesterId))
            throw new PaymentException("You cannot access this payment");
        return mapper.toResponse(payment);
    }

    @Override @Transactional(readOnly = true)
    public List<PaymentResponse> getPayments(Long referenceId, PaymentReferenceType referenceType) {
        return repository.findAllByReferenceIdAndReferenceTypeOrderByCreatedAtDesc(referenceId, referenceType)
            .stream().map(mapper::toResponse).toList();
    }

    @Override @Transactional
    public PaymentResponse handleVnPayReturn(Map<String, String> params) {
        if (!provider().verifyCallback(params)) throw new PaymentException("Invalid VNPay signature");
        Payment payment = findFromCallback(params);
        validateAmount(payment, params);
        return mapper.toResponse(applyResult(payment, params));
    }

    @Override @Transactional
    public VnPayIpnResponse handleVnPayIpn(Map<String, String> params) {
        if (!provider().verifyCallback(params)) return new VnPayIpnResponse("97", "Invalid checksum");
        Payment payment;
        try { payment = findFromCallback(params); }
        catch (PaymentException ex) { return new VnPayIpnResponse("01", "Order not found"); }
        try { validateAmount(payment, params); }
        catch (PaymentException ex) { return new VnPayIpnResponse("04", "Invalid amount"); }
        if (payment.getStatus() == PaymentStatus.SUCCESS)
            return new VnPayIpnResponse("02", "Order already confirmed");
        try { applyResult(payment, params); }
        catch (PaymentException ex) { return new VnPayIpnResponse("99", "Booking confirmation failed"); }
        return new VnPayIpnResponse("00", "Confirm success");
    }

    private Payment applyResult(Payment payment, Map<String, String> params) {
        boolean success = "00".equals(params.get("vnp_ResponseCode"))
            && "00".equals(params.getOrDefault("vnp_TransactionStatus", "00"));
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
            if (success) payment.setPaidAt(Instant.now());
            payment.setTransactionCode(provider().getTransactionCode(params));
            payment.setResponseCode(params.get("vnp_ResponseCode"));
            payment.setUpdatedAt(Instant.now());
            Payment saved = repository.save(payment);
            if (success && saved.getReferenceType() == PaymentReferenceType.BOOKING)
                bookingClient.confirmPayment(saved.getReferenceId(), saved.getId());
            return saved;
        }
        return payment;
    }

    private Payment findFromCallback(Map<String, String> params) {
        String txnRef = params.get("vnp_TxnRef");
        if (txnRef == null) throw new PaymentException("Missing VNPay transaction reference");
        try { return find(Long.parseLong(txnRef)); }
        catch (NumberFormatException ex) { throw new PaymentException("Invalid VNPay transaction reference"); }
    }

    private void validateAmount(Payment payment, Map<String, String> params) {
        String rawAmount = params.get("vnp_Amount");
        if (rawAmount == null) throw new PaymentException("Missing VNPay amount");
        try {
            BigDecimal callbackAmount = new BigDecimal(rawAmount).movePointLeft(2);
            if (callbackAmount.compareTo(payment.getAmount()) != 0)
                throw new PaymentException("VNPay amount does not match payment amount");
        } catch (NumberFormatException ex) { throw new PaymentException("Invalid VNPay amount"); }
    }

    private Payment find(Long id) {
        return repository.findById(id).orElseThrow(() -> new PaymentException("Payment not found: " + id));
    }
    private void validateBooking(CreatePaymentRequest request, Long payerId, BookingPaymentContext booking) {
        if (!request.getReferenceId().equals(booking.bookingId()))
            throw new PaymentException("Booking reference does not match");
        if (!payerId.equals(booking.userId()))
            throw new PaymentException("You cannot pay for this booking");
        if (!"PENDING_PAYMENT".equals(booking.status()))
            throw new PaymentException("Booking is not payable");
        if (request.getAmount().compareTo(booking.totalAmount()) != 0)
            throw new PaymentException("Payment amount does not match booking total");
    }
    private PaymentProvider provider() {
        PaymentProvider provider = providers.get(PaymentMethod.VNPAY);
        if (provider == null) throw new PaymentException("VNPay provider is unavailable");
        return provider;
    }
}
