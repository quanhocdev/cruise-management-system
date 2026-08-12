package com.project.common.provider.location;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class NominatimGeocodingProvider {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public NominatimGeocodingProvider(
            @Value("${nominatim.url}") String nominatimUrl,
            ObjectMapper objectMapper) {
        this.restClient = RestClient.builder()
                .baseUrl(nominatimUrl)
                .defaultHeader(
                        "User-Agent",
                        "Cruise-Management-System")
                .build();

        this.objectMapper = objectMapper;
    }

    public JsonNode reverseGeocode(
            BigDecimal latitude,
            BigDecimal longitude) {

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException(
                    "Latitude and longitude are required");
        }

        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("format", "json")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("language", "vi")
                        .queryParam("zoom", 18)
                        .queryParam("addressdetails", 1)
                        .build())
                .retrieve()
                .body(String.class);

        try {
            return objectMapper.readTree(response);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to parse Nominatim response",
                    e);
        }
    }
}