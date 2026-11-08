
package com.project.chat.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "confirmed_booking", schema = "chat")
public class ConfirmedBooking {

    @Id
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "primary_holder_user_id", nullable = false)
    private UUID primaryHolderUserId;

    @Column(name = "booking_code", nullable = false, unique = true, length = 50)
    private String bookingCode;

    @Column(name = "recipient_email", length = 255)
    private String recipientEmail;

    @Column(name = "recipient_name", length = 150)
    private String recipientName;

    @Column(name = "number_passengers")
    private Integer numberPassengers;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "confirmed_at", nullable = false)
    private Instant confirmedAt;

    public ConfirmedBooking() {
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getPrimaryHolderUserId() {
        return primaryHolderUserId;
    }

    public void setPrimaryHolderUserId(UUID primaryHolderUserId) {
        this.primaryHolderUserId = primaryHolderUserId;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public Integer getNumberPassengers() {
        return numberPassengers;
    }

    public void setNumberPassengers(Integer numberPassengers) {
        this.numberPassengers = numberPassengers;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}
