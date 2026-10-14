package com.project.tour.exception;

public class WeatherApiException extends RuntimeException {

    public WeatherApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeatherApiException(String message) {
        super(message);
    }
}