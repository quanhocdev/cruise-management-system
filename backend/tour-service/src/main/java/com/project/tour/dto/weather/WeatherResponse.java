package com.project.tour.dto.weather;

import java.util.Map;

public record WeatherResponse(
        double latitude,
        double longitude,
        double elevation,
        String timezone,
        String timezoneAbbreviation,
        CurrentWeather current,
        Map<String, String> currentUnits) {

    public record CurrentWeather(
            String time,
            double temperature2m,
            int relativeHumidity2m,
            double apparentTemperature,
            int isDay,
            double precipitation,
            double rain,
            double showers,
            double snowfall,
            int weatherCode,
            int cloudCover,
            double pressureMsl,
            double windSpeed10m,
            int windDirection10m) {
    }
}