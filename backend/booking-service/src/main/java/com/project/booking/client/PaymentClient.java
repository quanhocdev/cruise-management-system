package com.project.booking.client;

import com.project.booking.dto.payment.BillPaymentRequest;
import com.project.booking.dto.payment.BillPaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(
            RestClient.Builder restClientBuilder,
            @Value("${payment-service.url}") String paymentServiceUrl) {

        this.restClient = restClientBuilder.clone()
                .baseUrl(paymentServiceUrl)
                .build();
    }

    @Retry(name = "paymentService")
    @CircuitBreaker(name = "paymentService")
    public BillPaymentResponse createBillPayment(BillPaymentRequest request) {

        return restClient
                .post()
                .uri("/internal/payments/bills")
                .body(request)
                .retrieve()
                .body(BillPaymentResponse.class);
    }
}