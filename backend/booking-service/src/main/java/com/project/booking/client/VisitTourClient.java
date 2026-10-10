package com.project.booking.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class VisitTourClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public VisitTourClient(
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
    public VisitTourUsageInfo getVisitTourUsageInfo(
            UUID visitTourId,
            UUID tourPackageId) {

        String accessToken = serviceTokenClient.getAccessToken();

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(
                                "/internal/visit-tours/{visitTourId}/usage-info")
                        .queryParam("tourPackageId", tourPackageId)
                        .build(visitTourId))
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(VisitTourUsageInfo.class);
    }

    public record VisitTourUsageInfo(
            UUID visitTourId,
            UUID tourId,
            UUID scheduleStopId,
            String name,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer maxPassengers,
            BigDecimal price,
            String visitTourStatus,
            UUID packageBenefitId,
            Integer benefitQuantity,
            BigDecimal discountPercent) {
    }
}