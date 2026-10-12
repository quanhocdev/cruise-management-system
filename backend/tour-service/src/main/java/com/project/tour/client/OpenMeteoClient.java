package com.project.tour.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenMeteoClient {

    private final RestClient restClient;

    public OpenMeteoClient(
            RestClient.Builder restClientBuilder,
            @Value("${open-meteo.url}") String openMeteoUrl) {
        this.restClient = restClientBuilder
                .baseUrl(openMeteoUrl)
                .build();
    }

    public JsonNode getCurrentWeather(
            double latitude,
            double longitude) {
        JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam(
                                "current",
                                "temperature_2m,"
                                        + "relative_humidity_2m,"
                                        + "apparent_temperature,"
                                        + "is_day,"
                                        + "precipitation,"
                                        + "rain,"
                                        + "showers,"
                                        + "snowfall,"
                                        + "weather_code,"
                                        + "cloud_cover,"
                                        + "pressure_msl,"
                                        + "wind_speed_10m,"
                                        + "wind_direction_10m")
                        .queryParam("timezone", "auto")
                        .build())
                .retrieve()
                .body(JsonNode.class);

        if (response == null
                || !response.path("current").isObject()) {
            throw new IllegalStateException(
                    "Open-Meteo không trả về dữ liệu thời tiết hợp lệ");
        }

        return response;
    }
}