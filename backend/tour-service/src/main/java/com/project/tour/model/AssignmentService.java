package com.project.tour.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assignment_service", uniqueConstraints = {
        @UniqueConstraint(name = "uk_assignment_service_tour_area", columnNames = { "tour_id", "cruise_area_id" })
})
public class AssignmentService {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * ID của Tour thuộc tour-service.
     */
    @Column(name = "tour_id", nullable = false)
    private UUID tourId;

    /**
     * ID của CruiseArea thuộc tour-service.
     */
    @Column(name = "cruise_area_id", nullable = false)
    private UUID cruiseAreaId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor cho JPA
    public AssignmentService() {
    }

    // Custom constructor giúp tạo object nhanh trong Service
    public AssignmentService(UUID tourId, UUID cruiseAreaId) {
        this.tourId = tourId;
        this.cruiseAreaId = cruiseAreaId;
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
    // GETTER / SETTER
    // =====================================================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTourId() {
        return tourId;
    }

    public void setTourId(UUID tourId) {
        this.tourId = tourId;
    }

    public UUID getCruiseAreaId() {
        return cruiseAreaId;
    }

    public void setCruiseAreaId(UUID cruiseAreaId) {
        this.cruiseAreaId = cruiseAreaId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}