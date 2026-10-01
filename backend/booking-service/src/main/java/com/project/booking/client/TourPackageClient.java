package com.project.booking.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TourPackageClient {

        private final RestClient restClient;
        private final ServiceTokenClient serviceTokenClient;

        public TourPackageClient(
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
        public BookingPackageInfo getBookingPackageInfo(UUID tourPackageId) {

                String accessToken = serviceTokenClient.getAccessToken();

                return restClient
                                .get()
                                .uri(
                                                "/internal/tour-packages/{tourPackageId}/booking-info",
                                                tourPackageId)
                                .headers(headers -> headers.setBearerAuth(accessToken))
                                .retrieve()
                                .body(BookingPackageInfo.class);
        }

        public record BookingPackageInfo(
                        UUID packageId,
                        BigDecimal price,
                        Integer roomCapacity) {
        }
}