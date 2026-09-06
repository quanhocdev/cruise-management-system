package com.project.booking.model;

import com.project.booking.model.enums.BookingStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_bookings_creator", columnList = "created_by_user_id"),
        @Index(name = "idx_bookings_tour", columnList = "tour_id"),
        @Index(name = "idx_bookings_code", columnList = "booking_code")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_by_user_id")
    private Long createdByUserId; // ID User thực hiện đặt tour

    @Column(name = "tour_id", nullable = false)
    private UUID tourId; // Tour được đặt

    @Column(name = "tour_package_id", nullable = false)
    private UUID tourPackageId; // Gói dịch vụ được chọn (VD: Người lớn, Trẻ em)

    @Column(name = "booking_code", unique = true, length = 30, nullable = false)
    private String bookingCode; // Mã duy nhất dùng để tạo QR Code (ZXing)

    @Column(name = "number_passengers", nullable = false)
    private Integer numberPassengers; // Tổng số khách trong đơn

    @Column(name = "primary_contact_name", nullable = false, length = 150)
    private String primaryContactName; // Tên người đại diện liên hệ

    @Column(name = "primary_contact_phone", nullable = false, length = 30)
    private String primaryContactPhone; // SĐT người đại diện

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount; // Tổng tiền thanh toán

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status = BookingStatus.PENDING_PAYMENT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = BookingStatus.PENDING_PAYMENT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- GETTERS & SETTERS ---
    // (Bạn hãy generate đầy đủ getters và setters cho các trường trên)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public UUID getTourId() {
        return tourId;
    }

    public void setTourId(UUID tourId) {
        this.tourId = tourId;
    }

    public UUID getTourPackageId() {
        return tourPackageId;
    }

    public void setTourPackageId(UUID tourPackageId) {
        this.tourPackageId = tourPackageId;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public Integer getNumberPassengers() {
        return numberPassengers;
    }

    public void setNumberPassengers(Integer numberPassengers) {
        this.numberPassengers = numberPassengers;
    }

    public String getPrimaryContactName() {
        return primaryContactName;
    }

    public void setPrimaryContactName(String primaryContactName) {
        this.primaryContactName = primaryContactName;
    }

    public String getPrimaryContactPhone() {
        return primaryContactPhone;
    }

    public void setPrimaryContactPhone(String primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}