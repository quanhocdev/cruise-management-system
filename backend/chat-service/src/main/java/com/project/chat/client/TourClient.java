
package com.project.chat.client;

import com.project.chat.dto.internal.TourPriceSearchResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

@Component
public class TourClient {

        private final RestClient restClient;
        private final ServiceTokenClient serviceTokenClient;
        private final String tourScope;

        public TourClient(
                        RestClient.Builder restClientBuilder,
                        ServiceTokenClient serviceTokenClient,
                        @Value("${tour-service.url}") String tourServiceUrl,
                        @Value("${service.oauth2.tour-scope}") String tourScope) {

                this.restClient = restClientBuilder
                                .baseUrl(tourServiceUrl)
                                .build();

                this.serviceTokenClient = serviceTokenClient;
                this.tourScope = tourScope;
        }

        public List<TourPriceSearchResponse> searchReadyToursByMinimumPrice(
                        BigDecimal minPrice,
                        int limit) {

                validatePrice(minPrice, "Minimum price");
                validateLimit(limit);

                return executeSearch(
                                "/internal/tours/search-by-min-price",
                                minPrice,
                                null,
                                limit);
        }

        public List<TourPriceSearchResponse> searchReadyToursByMaximumPrice(
                        BigDecimal maxPrice,
                        int limit) {

                validatePrice(maxPrice, "Maximum price");
                validateLimit(limit);

                return executeSearch(
                                "/internal/tours/search-by-max-price",
                                null,
                                maxPrice,
                                limit);
        }

        public List<TourPriceSearchResponse> searchReadyToursByPriceRange(
                        BigDecimal minPrice,
                        BigDecimal maxPrice,
                        int limit) {

                validatePrice(minPrice, "Minimum price");
                validatePrice(maxPrice, "Maximum price");
                validateLimit(limit);

                if (minPrice.compareTo(maxPrice) > 0) {
                        throw new IllegalArgumentException(
                                        "Minimum price must not be greater than maximum price");
                }

                String accessToken = serviceTokenClient.getAccessToken(tourScope);

                List<TourPriceSearchResponse> response = restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/internal/tours/search-by-price-range")
                                                .queryParam("minPrice", minPrice)
                                                .queryParam("maxPrice", maxPrice)
                                                .queryParam("limit", limit)
                                                .build())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .retrieve()
                                .body(new ParameterizedTypeReference<>() {
                                });

                return response != null ? response : List.of();
        }

        /**
         * Tìm tour READY theo nhiều tiêu chí.
         */
        public List<TourPriceSearchResponse> searchReadyToursByCriteria(
                        BigDecimal minPrice,
                        BigDecimal maxPrice,
                        String departurePoint,
                        String destination,
                        Integer month,
                        Integer year,
                        int limit) {

                if (limit < 1 || limit > 10) {
                        throw new IllegalArgumentException(
                                        "Tour search limit must be between 1 and 10");
                }

                String accessToken = serviceTokenClient.getAccessToken(tourScope);

                List<TourPriceSearchResponse> response = restClient.get()
                                .uri(uriBuilder -> {
                                        var builder = uriBuilder.path("/internal/tours/search");

                                        if (minPrice != null) {
                                                builder.queryParam("minPrice", minPrice);
                                        }

                                        if (maxPrice != null) {
                                                builder.queryParam("maxPrice", maxPrice);
                                        }

                                        if (departurePoint != null && !departurePoint.isBlank()) {
                                                builder.queryParam("departurePoint", departurePoint.trim());
                                        }

                                        if (destination != null && !destination.isBlank()) {
                                                builder.queryParam("destination", destination.trim());
                                        }

                                        if (month != null) {
                                                builder.queryParam("month", month);
                                        }

                                        if (year != null) {
                                                builder.queryParam("year", year);
                                        }

                                        builder.queryParam("limit", limit);

                                        return builder.build();
                                })
                                .header(
                                                HttpHeaders.AUTHORIZATION,
                                                "Bearer " + accessToken)
                                .retrieve()
                                .body(new ParameterizedTypeReference<>() {
                                });

                return response != null ? response : List.of();
        }

        /**
         * Giữ overload cũ để không ảnh hưởng các nơi đang gọi phương thức này.
         */
        public List<TourPriceSearchResponse> searchReadyToursByMinimumPrice(
                        BigDecimal minPrice) {

                return searchReadyToursByMinimumPrice(minPrice, 3);
        }

        private List<TourPriceSearchResponse> executeSearch(
                        String path,
                        BigDecimal minPrice,
                        BigDecimal maxPrice,
                        int limit) {

                String accessToken = serviceTokenClient.getAccessToken(tourScope);

                List<TourPriceSearchResponse> response = restClient.get()
                                .uri(uriBuilder -> {
                                        var builder = uriBuilder.path(path);

                                        if (minPrice != null) {
                                                builder.queryParam("minPrice", minPrice);
                                        }

                                        if (maxPrice != null) {
                                                builder.queryParam("maxPrice", maxPrice);
                                        }

                                        return builder
                                                        .queryParam("limit", limit)
                                                        .build();
                                })
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .retrieve()
                                .body(new ParameterizedTypeReference<>() {
                                });

                return response != null ? response : List.of();
        }

        private void validatePrice(BigDecimal price, String fieldName) {
                if (price == null || price.signum() <= 0) {
                        throw new IllegalArgumentException(
                                        fieldName + " must be greater than zero");
                }
        }

        private void validateLimit(int limit) {
                if (limit < 1 || limit > 10) {
                        throw new IllegalArgumentException(
                                        "Tour search limit must be between 1 and 10");
                }
        }
}
