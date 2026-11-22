
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

    /**
     * Tìm tour READY có giá thấp nhất >= minPrice.
     *
     * @param minPrice Giá tối thiểu, đơn vị VND.
     * @param limit    Số lượng kết quả tối đa.
     */
    public List<TourPriceSearchResponse> searchReadyToursByMinimumPrice(
            BigDecimal minPrice,
            int limit) {

        if (minPrice == null || minPrice.signum() < 0) {
            throw new IllegalArgumentException(
                    "Minimum tour price must not be negative or null");
        }

        if (limit < 1 || limit > 10) {
            throw new IllegalArgumentException(
                    "Tour search limit must be between 1 and 10");
        }

        String accessToken = serviceTokenClient.getAccessToken(tourScope);

        List<TourPriceSearchResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/tours/search-by-min-price")
                        .queryParam("minPrice", minPrice)
                        .queryParam("limit", limit)
                        .build())
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return response != null ? response : List.of();
    }

    /**
     * Tìm tối đa 3 tour READY.
     */
    public List<TourPriceSearchResponse> searchReadyToursByMinimumPrice(
            BigDecimal minPrice) {

        return searchReadyToursByMinimumPrice(minPrice, 3);
    }
}
