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
public class ProductTourManagementClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public ProductTourManagementClient(
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
    public List<ProductUsageManagementInfo> getProductUsageInfo(
            List<UUID> productTourIds) {

        if (productTourIds == null || productTourIds.isEmpty()) {
            return Collections.emptyList();
        }

        String accessToken = serviceTokenClient.getAccessToken();

        ProductUsageManagementInfo[] response = restClient
                .post()
                .uri("/internal/product-tours/usage-info/batch")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .body(new ProductTourUsageBatchRequest(productTourIds))
                .retrieve()
                .body(ProductUsageManagementInfo[].class);

        return response != null
                ? Arrays.asList(response)
                : Collections.emptyList();
    }

    private record ProductTourUsageBatchRequest(
            List<UUID> productTourIds) {
    }

    public record ProductUsageManagementInfo(
            UUID productTourId,
            UUID tourId,
            UUID productId,
            String productName,
            BigDecimal unitPrice,
            Integer quantity,
            String productTourStatus,
            boolean productActive) {
    }
}
