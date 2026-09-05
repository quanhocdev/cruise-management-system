package com.project.feedback.exception;
import org.springframework.http.HttpStatus;
public class FeedbackException extends RuntimeException {
    private final HttpStatus status;
    public FeedbackException(HttpStatus status, String message) { super(message); this.status = status; }
    public HttpStatus getStatus() { return status; }
}
