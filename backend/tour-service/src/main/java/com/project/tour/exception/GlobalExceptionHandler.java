package com.project.tour.exception;

import com.project.tour.dto.error.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * =====================================================
     * APP EXCEPTION
     * =====================================================
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(
            AppException exception,
            HttpServletRequest request) {

        HttpStatus status = exception.getStatus();

        ApiErrorResponse response = new ApiErrorResponse();

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status.value());
        response.setError(status.getReasonPhrase());
        response.setMessage(exception.getMessage());
        response.setPath(request.getRequestURI());
        response.setValidationErrors(null);

        return ResponseEntity
                .status(status)
                .body(response);
    }

    /*
     * =====================================================
     * VALIDATION EXCEPTION
     * =====================================================
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        Map<String, String> validationErrors = new LinkedHashMap<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {

            validationErrors.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage());
        }

        ApiErrorResponse response = new ApiErrorResponse();

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setMessage("Validation failed");
        response.setPath(request.getRequestURI());
        response.setValidationErrors(validationErrors);

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    /*
     * =====================================================
     * INVALID JSON / INVALID ENUM
     * =====================================================
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        ApiErrorResponse response = new ApiErrorResponse();

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setMessage("Invalid request body");
        response.setPath(request.getRequestURI());
        response.setValidationErrors(null);

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    /*
     * =====================================================
     * ALL OTHER EXCEPTIONS
     * =====================================================
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(
            Exception exception,
            HttpServletRequest request) {

        exception.printStackTrace();

        ApiErrorResponse response = new ApiErrorResponse();

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setError(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        response.setMessage("Internal server error");
        response.setPath(request.getRequestURI());
        response.setValidationErrors(null);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}