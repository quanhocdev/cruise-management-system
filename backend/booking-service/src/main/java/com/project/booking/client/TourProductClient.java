package com.project.booking.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TourProductClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public TourProductClient(
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
    public ProductUsageInfo getProductUsageInfo(
            UUID productTourId,
            UUID tourPackageId) {

        String accessToken = serviceTokenClient.getAccessToken();

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(
                                "/internal/product-tours/{productTourId}/usage-info")
                        .queryParam("tourPackageId", tourPackageId)
                        .build(productTourId))
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(ProductUsageInfo.class);
    }

    public record ProductUsageInfo(
            UUID productTourId,
            UUID tourId,
            UUID productId,
            String productName,
            BigDecimal unitPrice,
            String productTourStatus,
            boolean productActive,
            Integer benefitQuantity,
            BigDecimal discountPercent) {
    }
}