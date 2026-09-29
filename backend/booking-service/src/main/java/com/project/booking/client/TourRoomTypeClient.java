package com.project.booking.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class TourRoomTypeClient {

    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-API-Key";

    private final RestClient restClient;
    private final String internalApiKey;

    public TourRoomTypeClient(
            RestClient.Builder restClientBuilder,
            @Value("${tour-service.url}") String tourServiceUrl,
            @Value("${internal.api-key}") String internalApiKey) {

        this.restClient = restClientBuilder
                .baseUrl(tourServiceUrl)
                .build();

        this.internalApiKey = internalApiKey;
    }

    @Retry(name = "tourService")
    @CircuitBreaker(name = "tourService")
    public Integer getPackageRoomCapacity(UUID tourPackageId) {

        return restClient
                .get()
                .uri(
                        "/internal/tour-packages/{tourPackageId}/room-capacity",
                        tourPackageId)
                .header(
                        INTERNAL_API_KEY_HEADER,
                        internalApiKey)
                .retrieve()
                .body(Integer.class);
    }
}