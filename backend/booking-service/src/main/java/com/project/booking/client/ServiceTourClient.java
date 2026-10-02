package com.project.booking.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class ServiceTourClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public ServiceTourClient(
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
    public ServiceUsageInfo getServiceUsageInfo(
            UUID serviceTourId,
            UUID tourPackageId) {

        String accessToken = serviceTokenClient.getAccessToken();

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(
                                "/internal/service-tours/{serviceTourId}/usage-info")
                        .queryParam("tourPackageId", tourPackageId)
                        .build(serviceTourId))
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(ServiceUsageInfo.class);
    }

    public record ServiceUsageInfo(
            UUID serviceTourId,
            UUID tourId,
            UUID serviceId,
            String serviceName,
            BigDecimal unitPrice,
            String serviceTourStatus,
            boolean serviceActive,
            Integer maxPassengers,
            Integer durationMinutes,
            UUID packageBenefitId,
            Integer benefitQuantity,
            BigDecimal discountPercent) {
    }
}