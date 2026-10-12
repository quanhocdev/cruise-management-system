
package com.project.tour.dto.weather;

import java.util.List;
import java.util.Map;

public record WeatherResponse(
        double latitude,
        double longitude,
        double elevation,
        String timezone,
        String timezoneAbbreviation,
        CurrentWeather current,
        Map<String, String> currentUnits,
        DailyForecast daily,
        Map<String, String> dailyUnits) {

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

    public record DailyForecast(
            List<String> time,
            List<Double> temperature2mMax,
            List<Double> temperature2mMin,
            List<Double> apparentTemperatureMax,
            List<Double> apparentTemperatureMin,
            List<String> sunrise,
            List<String> sunset,
            List<Double> daylightDuration,
            List<Double> sunshineDuration,
            List<Double> precipitationSum,
            List<Double> precipitationHours,
            List<Double> precipitationProbabilityMax,
            List<Double> rainSum,
            List<Double> showersSum,
            List<Double> snowfallSum,
            List<Integer> weatherCode,
            List<Double> windSpeed10mMax,
            List<Integer> windDirection10mDominant,
            List<Double> windGusts10mMax) {
    }
}
