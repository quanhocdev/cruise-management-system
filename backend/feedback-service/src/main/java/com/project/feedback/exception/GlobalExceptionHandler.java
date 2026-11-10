
package com.project.feedback.exception;

import com.project.feedback.dto.error.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

        ApiErrorResponse response = buildResponse(
                status,
                exception.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity.status(status).body(response);
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

        ApiErrorResponse response = buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getRequestURI(),
                validationErrors);

        return ResponseEntity.badRequest().body(response);
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

        ApiErrorResponse response = buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body",
                request.getRequestURI(),
                null);

        return ResponseEntity.badRequest().body(response);
    }

    /*
     * =====================================================
     * RESPONSE STATUS EXCEPTION
     * =====================================================
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.valueOf(
                exception.getStatusCode().value());

        ApiErrorResponse response = buildResponse(
                status,
                exception.getReason() != null
                        ? exception.getReason()
                        : status.getReasonPhrase(),
                request.getRequestURI(),
                null);

        return ResponseEntity.status(status).body(response);
    }

    /*
     * =====================================================
     * BIND EXCEPTION
     * Dành cho @ModelAttribute / form-data
     * =====================================================
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponse> handleBindException(
            BindException exception,
            HttpServletRequest request) {

        Map<String, String> validationErrors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            log.warn(
                    "Form binding failed - field: {}, message: {}",
                    error.getField(),
                    error.getDefaultMessage());

            validationErrors.put(
                    error.getField(),
                    error.getDefaultMessage());
        });

        ApiErrorResponse response = buildResponse(
                HttpStatus.BAD_REQUEST,
                "Form data binding failed",
                request.getRequestURI(),
                validationErrors);

        return ResponseEntity.badRequest().body(response);
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

        log.error(
                "Unexpected error at {}",
                request.getRequestURI(),
                exception);

        ApiErrorResponse response = buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                request.getRequestURI(),
                null);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /*
     * =====================================================
     * COMMON RESPONSE BUILDER
     * =====================================================
     */
    private ApiErrorResponse buildResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> validationErrors) {

        ApiErrorResponse response = new ApiErrorResponse();

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status.value());
        response.setError(status.getReasonPhrase());
        response.setMessage(message);
        response.setPath(path);
        response.setValidationErrors(validationErrors);

        return response;
    }
}
