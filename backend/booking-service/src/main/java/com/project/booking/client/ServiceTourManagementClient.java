package com.project.booking.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class ServiceTourManagementClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public ServiceTourManagementClient(
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
    public List<ServiceUsageManagementInfo> getServiceUsageInfo(
            List<UUID> serviceTourIds) {

        if (serviceTourIds == null || serviceTourIds.isEmpty()) {
            return Collections.emptyList();
        }

        String accessToken = serviceTokenClient.getAccessToken();

        ServiceUsageManagementInfo[] response = restClient
                .post()
                .uri("/internal/service-tours/usage-info/batch")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .body(new ServiceTourUsageBatchRequest(serviceTourIds))
                .retrieve()
                .body(ServiceUsageManagementInfo[].class);

        return response != null
                ? Arrays.asList(response)
                : Collections.emptyList();
    }

    private record ServiceTourUsageBatchRequest(
            List<UUID> serviceTourIds) {
    }

    public record ServiceUsageManagementInfo(
            UUID serviceTourId,
            UUID tourId,
            UUID serviceId,
            String serviceName,
            BigDecimal unitPrice,
            String serviceTourStatus,
            boolean serviceActive,
            Integer maxPassengers,
            Integer durationMinutes) {
    }
}
