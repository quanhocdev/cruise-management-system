package com.project.booking.model;

import com.project.booking.model.enums.BookingStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_bookings_creator", columnList = "created_by_user_id"),
    @Index(name = "idx_bookings_voyage", columnList = "voyage_id")
})
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_by_user_id") private Long createdByUserId;
    @Column(name = "voyage_id", nullable = false) private UUID voyageId;
    @Column(name = "booking_code", unique = true, length = 30) private String bookingCode;
    @Column(name = "primary_contact_name", nullable = false, length = 150) private String primaryContactName;
    @Column(name = "primary_contact_phone", nullable = false, length = 30) private String primaryContactPhone;
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2) private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private BookingStatus status;
    @Column(name = "payment_id") private Long paymentId;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long value) { this.createdByUserId = value; }
    public UUID getVoyageId() { return voyageId; }
    public void setVoyageId(UUID value) { this.voyageId = value; }
    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String value) { this.bookingCode = value; }
    public String getPrimaryContactName() { return primaryContactName; }
    public void setPrimaryContactName(String value) { this.primaryContactName = value; }
    public String getPrimaryContactPhone() { return primaryContactPhone; }
    public void setPrimaryContactPhone(String value) { this.primaryContactPhone = value; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
