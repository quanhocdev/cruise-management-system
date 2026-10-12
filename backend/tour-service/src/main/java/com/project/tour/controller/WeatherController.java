package com.project.tour.controller;

import com.project.tour.dto.weather.WeatherResponse;
import com.project.tour.exception.WeatherApiException;
import com.project.tour.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<?> getWeather(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        try {
            WeatherResponse response = weatherService.getWeather(
                    latitude,
                    longitude);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", ex.getMessage()));

        } catch (WeatherApiException ex) {
            return ResponseEntity.status(
                    HttpStatus.BAD_GATEWAY).body(
                            Map.of("error", ex.getMessage()));
        }
    }
}