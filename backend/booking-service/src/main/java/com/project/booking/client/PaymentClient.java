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
            @Value("${payment-service.url}") String paymentServiceUrl,
            @Value("${internal.api-key:${INTERNAL_API_KEY:}}") String key) {

        this.restClient = restClientBuilder.clone()
                .baseUrl(paymentServiceUrl)
                .defaultHeader("X-Internal-Api-Key",key)
                .build();
    }

    public java.util.List<BillPaymentResponse> getBillPayments(Long billId) {
        return restClient.get().uri("/internal/payments/bills/{id}",billId).retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<java.util.List<BillPaymentResponse>>() {});
    }

    public java.util.List<com.project.booking.dto.payment.BillCollectionResponse> getBillCollections(java.util.List<Long> ids) {
        return restClient.get().uri(uri->uri.path("/internal/payments/bill-collections")
                .queryParam("billIds",ids.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","))).build())
            .retrieve().body(new org.springframework.core.ParameterizedTypeReference<java.util.List<com.project.booking.dto.payment.BillCollectionResponse>>() {});
    }

    public org.springframework.http.ResponseEntity<BillPaymentResponse> createCommand(BillPaymentRequest request) {
        try {
            return restClient.post().uri("/internal/payments/bills").body(request).retrieve().toEntity(BillPaymentResponse.class);
        } catch(org.springframework.web.client.HttpClientErrorException e) {
            String code="BILL_PAYMENT_REJECTED";
            try {
                String message=new com.fasterxml.jackson.databind.ObjectMapper().readTree(e.getResponseBodyAsString()).path("message").asText();
                if(java.util.Set.of("IDEMPOTENCY_CONFLICT","BILL_PAYMENT_MISMATCH","INVALID_CLIENT_REQUEST_ID").contains(message))code=message;
            } catch(com.fasterxml.jackson.core.JsonProcessingException ignored) { }
            if(e.getStatusCode().value()==409)
                throw new com.project.booking.service.command.CommandException(code,"Yêu cầu thanh toán bị xung đột. Vui lòng kiểm tra bill.",org.springframework.http.HttpStatus.CONFLICT);
            throw new org.springframework.web.server.ResponseStatusException(e.getStatusCode(),"BILL_PAYMENT_REJECTED");
        }
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
