package com.project.booking.service.queue;

import com.project.booking.dto.booking.CreateBookingRequest;
import com.project.booking.dto.booking.BookingResponse;
import com.project.booking.model.enums.BookingQueueStatus;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BookingQueueTask {

    private final UUID requestId;
    private final UUID userId;
    private final String email;
    private final CreateBookingRequest request;
    private final Instant receivedAt;

    private final CompletableFuture<BookingResponse> resultFuture;

    private volatile BookingQueueStatus status;

    public BookingQueueTask(
            UUID requestId,
            UUID userId,
            String email,
            CreateBookingRequest request) {

        this.requestId = requestId;
        this.userId = userId;
        this.email = email;
        this.request = request;
        this.receivedAt = Instant.now();

        this.resultFuture = new CompletableFuture<>();
        this.status = BookingQueueStatus.WAITING;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public CreateBookingRequest getRequest() {
        return request;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public CompletableFuture<BookingResponse> getResultFuture() {
        return resultFuture;
    }

    public BookingQueueStatus getStatus() {
        return status;
    }

    public void setStatus(BookingQueueStatus status) {
        this.status = status;
    }
}