package com.project.booking.client;

import com.project.booking.dto.payment.BillPaymentRequest;
import com.project.booking.dto.payment.BillPaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.payment.url}") String paymentServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(paymentServiceUrl)
                .build();
    }

    public BillPaymentResponse createBillPayment(BillPaymentRequest request) {
        return restClient
                .post()
                .uri("/api/v1/payments/bills")
                .body(request)
                .retrieve()
                .body(BillPaymentResponse.class);
    }
}