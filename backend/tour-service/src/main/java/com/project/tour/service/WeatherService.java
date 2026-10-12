package com.project.tour.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.project.tour.client.OpenMeteoClient;
import com.project.tour.dto.weather.WeatherResponse;
import com.project.tour.exception.WeatherApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class WeatherService {

    private final OpenMeteoClient openMeteoClient;
    private final Cache<String, WeatherResponse> weatherCache;

    public WeatherService(
            OpenMeteoClient openMeteoClient,
            Cache<String, WeatherResponse> weatherCache) {
        this.openMeteoClient = openMeteoClient;
        this.weatherCache = weatherCache;
    }

    public WeatherResponse getWeather(
            double latitude,
            double longitude) {
        validateCoordinates(latitude, longitude);

        double normalizedLatitude = normalize(latitude);
        double normalizedLongitude = normalize(longitude);

        String cacheKey = String.format(
                Locale.ROOT,
                "%.4f,%.4f",
                normalizedLatitude,
                normalizedLongitude);

        return weatherCache.get(
                cacheKey,
                key -> fetchWeather(
                        normalizedLatitude,
                        normalizedLongitude));
    }

    private WeatherResponse fetchWeather(
            double latitude,
            double longitude) {
        try {
            JsonNode root = openMeteoClient.getCurrentWeather(
                    latitude,
                    longitude);

            JsonNode current = root.path("current");
            JsonNode units = root.path("current_units");

            Map<String, String> currentUnits = new HashMap<>();

            if (units.isObject()) {
                units.fields().forEachRemaining(entry -> currentUnits.put(
                        entry.getKey(),
                        entry.getValue().asText()));
            }

            WeatherResponse.CurrentWeather currentWeather = new WeatherResponse.CurrentWeather(
                    requiredText(current, "time"),
                    requiredDouble(current, "temperature_2m"),
                    requiredInt(current, "relative_humidity_2m"),
                    requiredDouble(current, "apparent_temperature"),
                    requiredInt(current, "is_day"),
                    requiredDouble(current, "precipitation"),
                    requiredDouble(current, "rain"),
                    requiredDouble(current, "showers"),
                    requiredDouble(current, "snowfall"),
                    requiredInt(current, "weather_code"),
                    requiredInt(current, "cloud_cover"),
                    requiredDouble(current, "pressure_msl"),
                    requiredDouble(current, "wind_speed_10m"),
                    requiredInt(current, "wind_direction_10m"));

            return new WeatherResponse(
                    requiredDouble(root, "latitude"),
                    requiredDouble(root, "longitude"),
                    requiredDouble(root, "elevation"),
                    requiredText(root, "timezone"),
                    requiredText(root, "timezone_abbreviation"),
                    currentWeather,
                    Map.copyOf(currentUnits));

        } catch (RestClientException | IllegalStateException ex) {
            throw new WeatherApiException(
                    "Không thể lấy dữ liệu thời tiết từ Open-Meteo",
                    ex);
        }
    }

    private void validateCoordinates(
            double latitude,
            double longitude) {
        if (!Double.isFinite(latitude)
                || latitude < -90
                || latitude > 90) {
            throw new IllegalArgumentException(
                    "Latitude phải nằm trong khoảng -90 đến 90");
        }

        if (!Double.isFinite(longitude)
                || longitude < -180
                || longitude > 180) {
            throw new IllegalArgumentException(
                    "Longitude phải nằm trong khoảng -180 đến 180");
        }
    }

    private double normalize(double coordinate) {
        return BigDecimal.valueOf(coordinate)
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private String requiredText(JsonNode node, String field) {
        JsonNode value = node.path(field);

        if (value.isMissingNode() || value.isNull()) {
            throw new IllegalStateException(
                    "Thiếu trường dữ liệu Open-Meteo: " + field);
        }

        return value.asText();
    }

    private double requiredDouble(JsonNode node, String field) {
        JsonNode value = node.path(field);

        if (!value.isNumber()) {
            throw new IllegalStateException(
                    "Trường dữ liệu không hợp lệ: " + field);
        }

        return value.asDouble();
    }

    private int requiredInt(JsonNode node, String field) {
        JsonNode value = node.path(field);

        if (!value.isIntegralNumber() || !value.canConvertToInt()) {
            throw new IllegalStateException(
                    "Trường dữ liệu không hợp lệ: " + field);
        }

        return value.asInt();
    }
}