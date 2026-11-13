package com.project.booking.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class TourRefundClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public TourRefundClient(
            RestClient.Builder restClientBuilder,
            @Value("${tour-service.url}") String tourServiceUrl,
            ServiceTokenClient serviceTokenClient) {

        this.restClient = restClientBuilder
                .baseUrl(tourServiceUrl)
                .build();

        this.serviceTokenClient = serviceTokenClient;
    }

    @Retry(name = "tourService")
    @CircuitBreaker(name = "tourService")
    public RefundCalculationInfo calculateRefund(
            UUID tourId,
            BigDecimal originalAmount) {

        String accessToken = serviceTokenClient.getAccessToken();

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/tours/{tourId}/refund-calculation")
                        .queryParam("originalAmount", originalAmount)
                        .build(tourId))
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(RefundCalculationInfo.class);
    }

    public record RefundCalculationInfo(
            UUID policyRefundRuleId,
            LocalDate departureDate,
            Integer daysBeforeDeparture,
            BigDecimal refundPercentage,
            BigDecimal calculatedRefundAmount) {
    }
}