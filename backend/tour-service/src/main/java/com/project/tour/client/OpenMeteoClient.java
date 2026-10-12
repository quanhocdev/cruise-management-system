
package com.project.tour.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Component
public class OpenMeteoClient {

    private static final String CURRENT_FIELDS = "temperature_2m,"
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
            + "wind_direction_10m";

    private static final String DAILY_FIELDS = "temperature_2m_max,"
            + "temperature_2m_min,"
            + "apparent_temperature_max,"
            + "apparent_temperature_min,"
            + "sunrise,"
            + "sunset,"
            + "daylight_duration,"
            + "sunshine_duration,"
            + "precipitation_sum,"
            + "precipitation_hours,"
            + "precipitation_probability_max,"
            + "rain_sum,"
            + "showers_sum,"
            + "snowfall_sum,"
            + "weather_code,"
            + "wind_speed_10m_max,"
            + "wind_direction_10m_dominant,"
            + "wind_gusts_10m_max";

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

        JsonNode response = requestWeather(
                latitude,
                longitude,
                null,
                null,
                false);

        if (response == null || !response.path("current").isObject()) {
            throw new IllegalStateException(
                    "Open-Meteo không trả về dữ liệu thời tiết hiện tại hợp lệ");
        }

        return response;
    }

    public JsonNode getDailyWeather(
            double latitude,
            double longitude,
            LocalDate startDate,
            LocalDate endDate) {

        JsonNode response = requestWeather(
                latitude,
                longitude,
                startDate,
                endDate,
                true);

        if (response == null
                || !response.path("current").isObject()
                || !response.path("daily").isObject()) {
            throw new IllegalStateException(
                    "Open-Meteo không trả về dữ liệu dự báo hợp lệ");
        }

        return response;
    }

    private JsonNode requestWeather(
            double latitude,
            double longitude,
            LocalDate startDate,
            LocalDate endDate,
            boolean includeDaily) {

        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", CURRENT_FIELDS)
                            .queryParam("timezone", "auto");

                    if (includeDaily) {
                        uriBuilder
                                .queryParam("daily", DAILY_FIELDS)
                                .queryParam("start_date", startDate)
                                .queryParam("end_date", endDate);
                    }

                    return uriBuilder.build();
                })
                .retrieve()
                .body(JsonNode.class);
    }
}
