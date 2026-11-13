package com.project.booking.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class TourClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public TourClient(
            RestClient.Builder restClientBuilder,
            @Value("${tour-service.url}") String tourServiceUrl,
            ServiceTokenClient serviceTokenClient) {

        this.restClient = restClientBuilder
                .baseUrl(tourServiceUrl)
                .build();

        this.serviceTokenClient = serviceTokenClient;
    }

    /**
     * Lấy thông tin Tour theo ID.
     */
    @Retry(name = "tourService")
    @CircuitBreaker(name = "tourService")
    public TourInfo getTourInfo(UUID tourId) {

        String accessToken = serviceTokenClient.getAccessToken();

        return restClient
                .get()
                .uri("/internal/tours/{tourId}/booking-info", tourId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(TourInfo.class);
    }

    public record TourInfo(
            UUID tourId,
            String tourCode,
            String tourName,
            LocalDate departureDate,
            LocalDate endDate) {
    }
}