package com.project.payment.service;

import com.project.payment.dto.CreatePaymentRequest;
import com.project.payment.dto.PaymentResponse;
import com.project.payment.dto.VnPayIpnResponse;
import com.project.payment.model.enums.PaymentReferenceType;
import java.util.List;
import java.util.Map;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request, String clientIp);

    PaymentResponse getPayment(Long id);

    List<PaymentResponse> getPayments(Long referenceId, PaymentReferenceType referenceType);

    PaymentResponse handleVnPayReturn(Map<String, String> params);

    VnPayIpnResponse handleVnPayIpn(Map<String, String> params);
}
