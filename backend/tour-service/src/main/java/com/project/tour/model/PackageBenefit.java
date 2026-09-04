package com.project.tour.model;

import com.project.tour.model.enums.tour.BenefitType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "package_benefits", uniqueConstraints = @UniqueConstraint(name = "uk_package_benefits_package_reference", columnNames = {
        "tour_package_id", "type", "reference_id" }))
public class PackageBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tour_package_id", nullable = false)
    private UUID tourPackageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BenefitType type;

    @Column(name = "reference_id", nullable = false)
    private UUID referenceId; // ID từ bảng assignment tương ứng

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PackageBenefit() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTourPackageId() {
        return tourPackageId;
    }

    public void setTourPackageId(UUID tourPackageId) {
        this.tourPackageId = tourPackageId;
    }

    public BenefitType getType() {
        return type;
    }

    public void setType(BenefitType type) {
        this.type = type;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(UUID referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}