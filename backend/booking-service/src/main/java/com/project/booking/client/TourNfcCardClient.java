package com.project.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TourNfcCardClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public TourNfcCardClient(
            RestClient.Builder builder,
            ServiceTokenClient serviceTokenClient,
            @Value("${tour-service.url}") String tourServiceUrl) {

        this.restClient = builder
                .baseUrl(tourServiceUrl)
                .build();

        this.serviceTokenClient = serviceTokenClient;
    }

    public void assignCard(String cardUid) {
        if (cardUid == null || cardUid.isBlank()) {
            throw new IllegalArgumentException(
                    "NFC UID không được để trống");
        }

        String normalizedUid = cardUid.trim();
        String token = serviceTokenClient.getAccessToken();

        restClient.put()
                .uri(
                        "/internal/nfc-cards/{cardUid}/assign",
                        normalizedUid)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }
}
