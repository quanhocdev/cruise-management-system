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
        private final ServiceTokenClient serviceTokenClient;

        public TourRoomTypeClient(
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
        public Integer getPackageRoomCapacity(UUID tourPackageId) {

                String accessToken = serviceTokenClient.getAccessToken();

                return restClient
                                .get()
                                .uri(
                                                "/internal/tour-packages/{tourPackageId}/room-capacity",
                                                tourPackageId)
                                .headers(headers -> headers.setBearerAuth(accessToken))
                                .retrieve()
                                .body(Integer.class);
        }
}