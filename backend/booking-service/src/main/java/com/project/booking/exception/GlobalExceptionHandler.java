package com.project.booking.exception;

import com.project.booking.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookingException.class)
    ResponseEntity<ApiErrorResponse> booking(BookingException ex, HttpServletRequest request) {
        return response(ex.getStatus(), ex.getMessage(), request.getRequestURI(), null);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors())
            errors.put(error.getField(), error.getDefaultMessage());
        return response(HttpStatus.BAD_REQUEST, "Request validation failed", request.getRequestURI(), errors);
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> unexpected(Exception ex, HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI(), null);
    }
    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String message, String path, Map<String, String> errors) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(
            Instant.now(), status.value(), status.getReasonPhrase(), message, path, errors));
    }
}
