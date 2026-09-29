package com.project.booking.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class TourRoomTypeClient {

    private final RestClient restClient;

    public TourRoomTypeClient(
            RestClient.Builder restClientBuilder,
            @Value("${tour-service.url}") String tourServiceUrl) {
        this.restClient = restClientBuilder
                .baseUrl(tourServiceUrl)
                .build();
    }

    @Retry(name = "tourService")
    @CircuitBreaker(name = "tourService")
    public Integer getRoomTypeCapacity(UUID roomTypeId) {

        return restClient
                .get()
                .uri("/internal/room-types/{roomTypeId}/capacity", roomTypeId)
                .retrieve()
                .body(Integer.class);
    }
}