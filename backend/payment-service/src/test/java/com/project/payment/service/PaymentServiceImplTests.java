package com.project.payment.service;

import com.project.payment.dto.*;
import com.project.payment.client.BookingClient;
import com.project.payment.client.BookingPaymentContext;
import com.project.payment.exception.PaymentException;
import com.project.payment.mapper.PaymentMapper;
import com.project.payment.model.Payment;
import com.project.payment.model.enums.*;
import com.project.payment.repository.PaymentRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTests {
    @Mock PaymentRepository repository;
    @Mock PaymentProvider provider;
    @Mock BookingClient bookingClient;
    PaymentServiceImpl service;

    @BeforeEach void setUp() {
        when(provider.getPaymentMethod()).thenReturn(PaymentMethod.VNPAY);
        service = new PaymentServiceImpl(repository, new PaymentMapper(), List.of(provider), bookingClient, 15);
    }

    @Test void createPaymentUsesAuthenticatedPayerAndReturnsSandboxUrl() {
        when(bookingClient.getPaymentContext(100L)).thenReturn(
            new BookingPaymentContext(100L, 7L, new BigDecimal("1000000"), "PENDING_PAYMENT"));
        when(repository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0); if (payment.getId() == null) payment.setId(10L); return payment;
        });
        when(provider.createPaymentUrl(any(Payment.class), eq("127.0.0.1"))).thenReturn("https://sandbox.test/pay");
        PaymentResponse response = service.createPayment(request(), 7L, "127.0.0.1");
        assertEquals(7L, response.getPayerId());
        assertEquals(PaymentStatus.PENDING, response.getStatus());
        assertEquals("https://sandbox.test/pay", response.getPaymentUrl());
        assertNotNull(response.getExpiresAt());
    }

    @Test void createPaymentRejectsTamperedBookingAmount() {
        when(bookingClient.getPaymentContext(100L)).thenReturn(
            new BookingPaymentContext(100L, 7L, new BigDecimal("2000000"), "PENDING_PAYMENT"));
        assertThrows(PaymentException.class, () -> service.createPayment(request(), 7L, "127.0.0.1"));
    }

    @Test void returnRejectsAmountTampering() {
        Payment payment = payment(PaymentStatus.PENDING);
        when(provider.verifyCallback(anyMap())).thenReturn(true);
        when(repository.findById(10L)).thenReturn(Optional.of(payment));
        Map<String, String> params = callback(); params.put("vnp_Amount", "99900");
        assertThrows(PaymentException.class, () -> service.handleVnPayReturn(params));
    }

    @Test void ipnIsIdempotentForSuccessfulPayment() {
        when(provider.verifyCallback(anyMap())).thenReturn(true);
        when(repository.findById(10L)).thenReturn(Optional.of(payment(PaymentStatus.SUCCESS)));
        VnPayIpnResponse response = service.handleVnPayIpn(callback());
        assertEquals("02", response.RspCode());
    }

    private CreatePaymentRequest request() {
        CreatePaymentRequest request = new CreatePaymentRequest(); request.setReferenceId(100L);
        request.setReferenceType(PaymentReferenceType.BOOKING); request.setAmount(new BigDecimal("1000000"));
        request.setMethod(PaymentMethod.VNPAY); return request;
    }
    private Payment payment(PaymentStatus status) {
        Payment payment = new Payment(); payment.setId(10L); payment.setPayerId(7L);
        payment.setAmount(new BigDecimal("1000000")); payment.setStatus(status);
        payment.setCreatedAt(Instant.now()); payment.setUpdatedAt(Instant.now()); return payment;
    }
    private Map<String, String> callback() {
        Map<String, String> params = new HashMap<>(); params.put("vnp_TxnRef", "10");
        params.put("vnp_Amount", "100000000"); params.put("vnp_ResponseCode", "00");
        params.put("vnp_TransactionStatus", "00"); params.put("vnp_TransactionNo", "123456"); return params;
    }
}
