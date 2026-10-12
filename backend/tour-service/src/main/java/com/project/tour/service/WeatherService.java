
package com.project.tour.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.project.tour.client.OpenMeteoClient;
import com.project.tour.dto.weather.WeatherResponse;
import com.project.tour.exception.ForecastUnavailableException;
import com.project.tour.exception.WeatherApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class WeatherService {

    private static final int MAX_FORECAST_DAYS = 16;

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
        return getWeather(latitude, longitude, null, null);
    }

    public WeatherResponse getWeather(
            double latitude,
            double longitude,
            LocalDate startDate,
            LocalDate endDate) {

        validateCoordinates(latitude, longitude);
        validateDates(startDate, endDate);

        double normalizedLatitude = normalize(latitude);
        double normalizedLongitude = normalize(longitude);

        String cacheKey = String.format(
                Locale.ROOT,
                "%.4f,%.4f|%s|%s",
                normalizedLatitude,
                normalizedLongitude,
                startDate == null ? "current" : startDate,
                endDate == null ? "current" : endDate);

        return weatherCache.get(
                cacheKey,
                key -> fetchWeather(
                        normalizedLatitude,
                        normalizedLongitude,
                        startDate,
                        endDate));
    }

    private WeatherResponse fetchWeather(
            double latitude,
            double longitude,
            LocalDate startDate,
            LocalDate endDate) {

        try {
            JsonNode root = startDate == null
                    ? openMeteoClient.getCurrentWeather(
                            latitude, longitude)
                    : openMeteoClient.getDailyWeather(
                            latitude, longitude, startDate, endDate);

            JsonNode current = root.path("current");

            Map<String, String> currentUnits = readUnits(root.path("current_units"));

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

            WeatherResponse.DailyForecast dailyForecast = null;
            Map<String, String> dailyUnits = Map.of();

            if (startDate != null) {
                JsonNode daily = root.path("daily");

                dailyForecast = parseDailyForecast(
                        daily,
                        startDate,
                        endDate);

                dailyUnits = readUnits(root.path("daily_units"));
            }

            return new WeatherResponse(
                    requiredDouble(root, "latitude"),
                    requiredDouble(root, "longitude"),
                    requiredDouble(root, "elevation"),
                    requiredText(root, "timezone"),
                    requiredText(root, "timezone_abbreviation"),
                    currentWeather,
                    Map.copyOf(currentUnits),
                    dailyForecast,
                    Map.copyOf(dailyUnits));

        } catch (ForecastUnavailableException ex) {
            throw ex;
        } catch (RestClientException | IllegalStateException ex) {
            throw new WeatherApiException(
                    "Không thể lấy hoặc xử lý dữ liệu thời tiết từ Open-Meteo",
                    ex);
        }
    }

    private WeatherResponse.DailyForecast parseDailyForecast(
            JsonNode daily,
            LocalDate startDate,
            LocalDate endDate) {

        List<String> dates = textList(daily, "time");

        if (dates.isEmpty()) {
            throw new ForecastUnavailableException(
                    "Open-Meteo chưa có dữ liệu dự báo cho ngày yêu cầu.");
        }

        LocalDate firstReturnedDate = LocalDate.parse(dates.get(0));
        LocalDate lastReturnedDate = LocalDate.parse(dates.get(dates.size() - 1));

        if (firstReturnedDate.isAfter(startDate)
                || lastReturnedDate.isBefore(endDate)) {
            throw new ForecastUnavailableException(
                    "Chưa có dữ liệu dự báo đầy đủ cho khoảng ngày yêu cầu.");
        }

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

            if (!dates.contains(date.toString())) {
                throw new ForecastUnavailableException(
                        "Chưa có dữ liệu dự báo cho ngày " + date + ".");
            }
        }

        int size = dates.size();

        return new WeatherResponse.DailyForecast(
                dates,
                doubleList(daily, "temperature_2m_max", size),
                doubleList(daily, "temperature_2m_min", size),
                doubleList(daily, "apparent_temperature_max", size),
                doubleList(daily, "apparent_temperature_min", size),
                textList(daily, "sunrise", size),
                textList(daily, "sunset", size),
                doubleList(daily, "daylight_duration", size),
                doubleList(daily, "sunshine_duration", size),
                doubleList(daily, "precipitation_sum", size),
                doubleList(daily, "precipitation_hours", size),
                doubleList(daily, "precipitation_probability_max", size),
                doubleList(daily, "rain_sum", size),
                doubleList(daily, "showers_sum", size),
                doubleList(daily, "snowfall_sum", size),
                intList(daily, "weather_code", size),
                doubleList(daily, "wind_speed_10m_max", size),
                intList(daily, "wind_direction_10m_dominant", size),
                doubleList(daily, "wind_gusts_10m_max", size));
    }

    private Map<String, String> readUnits(JsonNode node) {
        Map<String, String> units = new HashMap<>();

        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> units.put(
                    entry.getKey(),
                    entry.getValue().asText()));
        }

        return units;
    }

    private List<String> textList(
            JsonNode node,
            String field) {
        JsonNode values = requiredArray(node, field);
        List<String> result = new ArrayList<>();

        values.forEach(value -> {
            if (value.isNull() || value.isMissingNode()) {
                throw new ForecastUnavailableException(
                        "Chưa có đủ dữ liệu dự báo cho trường " + field + ".");
            }
            result.add(value.asText());
        });

        return List.copyOf(result);
    }

    private List<String> textList(
            JsonNode node,
            String field,
            int expectedSize) {
        List<String> result = textList(node, field);
        validateListSize(field, result.size(), expectedSize);
        return result;
    }

    private List<Double> doubleList(
            JsonNode node,
            String field,
            int expectedSize) {
        JsonNode values = requiredArray(node, field);
        List<Double> result = new ArrayList<>();

        values.forEach(value -> {
            if (!value.isNumber()) {
                throw new ForecastUnavailableException(
                        "Chưa có đủ dữ liệu dự báo cho trường " + field + ".");
            }
            result.add(value.asDouble());
        });

        validateListSize(field, result.size(), expectedSize);
        return List.copyOf(result);
    }

    private List<Integer> intList(
            JsonNode node,
            String field,
            int expectedSize) {
        JsonNode values = requiredArray(node, field);
        List<Integer> result = new ArrayList<>();

        values.forEach(value -> {
            if (!value.isIntegralNumber()
                    || !value.canConvertToInt()) {
                throw new ForecastUnavailableException(
                        "Chưa có đủ dữ liệu dự báo cho trường " + field + ".");
            }
            result.add(value.asInt());
        });

        validateListSize(field, result.size(), expectedSize);
        return List.copyOf(result);
    }

    private JsonNode requiredArray(
            JsonNode node,
            String field) {
        JsonNode value = node.path(field);

        if (!value.isArray()) {
            throw new IllegalStateException(
                    "Thiếu danh sách dữ liệu Open-Meteo: " + field);
        }

        return value;
    }

    private void validateListSize(
            String field,
            int actualSize,
            int expectedSize) {
        if (actualSize != expectedSize) {
            throw new ForecastUnavailableException(
                    "Dữ liệu dự báo không đầy đủ cho trường " + field + ".");
        }
    }

    private void validateCoordinates(
            double latitude,
            double longitude) {
        if (!Double.isFinite(latitude)
                || latitude < -90
                || latitude > 90) {
            throw new IllegalArgumentException(
                    "Latitude phải nằm trong khoảng -90 đến 90.");
        }

        if (!Double.isFinite(longitude)
                || longitude < -180
                || longitude > 180) {
            throw new IllegalArgumentException(
                    "Longitude phải nằm trong khoảng -180 đến 180.");
        }
    }

    private void validateDates(
            LocalDate startDate,
            LocalDate endDate) {

        if ((startDate == null) != (endDate == null)) {
            throw new IllegalArgumentException(
                    "Phải cung cấp cả startDate và endDate.");
        }

        if (startDate == null) {
            return;
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "endDate không được trước startDate.");
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        if (days > MAX_FORECAST_DAYS) {
            throw new IllegalArgumentException(
                    "Mỗi yêu cầu không được vượt quá "
                            + MAX_FORECAST_DAYS + " ngày.");
        }

        LocalDate today = LocalDate.now();
        LocalDate lastSupportedDate = today.plusDays(MAX_FORECAST_DAYS - 1);

        if (startDate.isBefore(today)
                || endDate.isAfter(lastSupportedDate)) {
            throw new ForecastUnavailableException(
                    "Khoảng ngày yêu cầu nằm ngoài phạm vi dự báo "
                            + "được backend hỗ trợ hiện tại.");
        }
    }

    private double normalize(double coordinate) {
        return BigDecimal.valueOf(coordinate)
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private String requiredText(
            JsonNode node,
            String field) {
        JsonNode value = node.path(field);

        if (value.isMissingNode() || value.isNull()) {
            throw new IllegalStateException(
                    "Thiếu trường dữ liệu Open-Meteo: " + field);
        }

        return value.asText();
    }

    private double requiredDouble(
            JsonNode node,
            String field) {
        JsonNode value = node.path(field);

        if (!value.isNumber()) {
            throw new IllegalStateException(
                    "Trường dữ liệu không hợp lệ: " + field);
        }

        return value.asDouble();
    }

    private int requiredInt(
            JsonNode node,
            String field) {
        JsonNode value = node.path(field);

        if (!value.isIntegralNumber()
                || !value.canConvertToInt()) {
            throw new IllegalStateException(
                    "Trường dữ liệu không hợp lệ: " + field);
        }

        return value.asInt();
    }
}
