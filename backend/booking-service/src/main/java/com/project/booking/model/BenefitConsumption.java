package com.project.booking.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "benefit_consumptions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_benefit_consumption_booking_benefit", columnNames = {
                "booking_id",
                "package_benefit_id"
        })
}, indexes = {
        @Index(name = "idx_benefit_consumptions_booking", columnList = "booking_id"),
        @Index(name = "idx_benefit_consumptions_benefit", columnList = "package_benefit_id")
})
public class BenefitConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "package_benefit_id", nullable = false)
    private UUID packageBenefitId;

    @Column(name = "used_quantity", nullable = false)
    private Integer usedQuantity = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public BenefitConsumption() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (usedQuantity == null) {
            usedQuantity = 0;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getPackageBenefitId() {
        return packageBenefitId;
    }

    public void setPackageBenefitId(UUID packageBenefitId) {
        this.packageBenefitId = packageBenefitId;
    }

    public Integer getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(Integer usedQuantity) {
        this.usedQuantity = usedQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}