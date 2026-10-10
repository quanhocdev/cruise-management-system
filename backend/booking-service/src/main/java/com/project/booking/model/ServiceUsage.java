package com.project.booking.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_usages")
public class ServiceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Passenger đang sử dụng service.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_passenger_id", nullable = false)
    private BookingPassenger bookingPassenger;

    /**
     * ServiceTour được cấu hình trong Tour Service.
     */
    @Column(name = "service_tour_id", nullable = false)
    private UUID serviceTourId;

    /**
     * Giá service tại thời điểm sử dụng.
     */
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Tổng tiền được giảm.
     */
    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount;

    /**
     * Số tiền cuối cùng passenger phải trả.
     */
    @Column(name = "final_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalAmount;

    /**
     * Thời điểm bắt đầu sử dụng service.
     */
    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;

    /**
     * Thời điểm service tự hết hạn.
     *
     * - Service có duration: usedAt + durationMinutes
     * - Service không giới hạn: null
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * Thời điểm passenger chủ động kết thúc usage.
     *
     * - Service không giới hạn: được set khi passenger scan NFC lần 2
     * - Service có duration: có thể set nếu passenger rời sớm
     * - Đang sử dụng: null
     */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    public ServiceUsage() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookingPassenger getBookingPassenger() {
        return bookingPassenger;
    }

    public void setBookingPassenger(BookingPassenger bookingPassenger) {
        this.bookingPassenger = bookingPassenger;
    }

    public UUID getServiceTourId() {
        return serviceTourId;
    }

    public void setServiceTourId(UUID serviceTourId) {
        this.serviceTourId = serviceTourId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
}