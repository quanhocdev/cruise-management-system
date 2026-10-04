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
public class ActivityCruiseClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public ActivityCruiseClient(
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
    public ActivityCruiseUsageInfo getActivityCruiseUsageInfo(
            UUID activityCruiseTourId,
            UUID tourPackageId) {

        String accessToken = serviceTokenClient.getAccessToken();

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(
                                "/internal/activity-cruise-tours/{activityCruiseTourId}/usage-info")
                        .queryParam("tourPackageId", tourPackageId)
                        .build(activityCruiseTourId))
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(ActivityCruiseUsageInfo.class);
    }

    public record ActivityCruiseUsageInfo(
            UUID activityCruiseTourId,
            UUID tourId,
            UUID activityCruiseId,
            String activityName,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer maxPassengers,
            BigDecimal price,
            String activityCruiseTourStatus,
            boolean activityActive,
            UUID packageBenefitId,
            Integer benefitQuantity,
            BigDecimal discountPercent) {
    }
}