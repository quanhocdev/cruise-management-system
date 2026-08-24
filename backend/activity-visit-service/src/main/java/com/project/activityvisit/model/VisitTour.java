package com.project.activityvisit.model;

import com.project.activityvisit.model.enums.VisitTourStatus;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "visit_tour", uniqueConstraints = {
        @UniqueConstraint(name = "uk_visit_tour_tour_stop", columnNames = {
                "tour_id",
                "schedule_stop_id"
        })
})
public class VisitTour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // =====================================================
    // TOUR REFERENCE
    // =====================================================

    @Column(name = "tour_id", nullable = false)
    private UUID tourId;

    // =====================================================
    // SCHEDULE STOP REFERENCE
    // =====================================================

    @Column(name = "schedule_stop_id", nullable = false)
    private UUID scheduleStopId;

    // =====================================================
    // VISIT TOUR CONFIGURATION
    // =====================================================

    @Column(nullable = true, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "max_passengers")
    private Integer maxPassengers;

    @Column(nullable = true, precision = 15, scale = 2)
    private BigDecimal price;

    // =====================================================
    // STATUS
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VisitTourStatus status = VisitTourStatus.WAITING_CONFIG;

    // =====================================================
    // AUDIT
    // =====================================================

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // =====================================================
    // JPA LIFECYCLE
    // =====================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = VisitTourStatus.WAITING_CONFIG;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public UUID getScheduleStopId() {
        return scheduleStopId;
    }

    public void setScheduleStopId(UUID scheduleStopId) {
        this.scheduleStopId = scheduleStopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxPassengers() {
        return maxPassengers;
    }

    public void setMaxPassengers(Integer maxPassengers) {
        this.maxPassengers = maxPassengers;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public VisitTourStatus getStatus() {
        return status;
    }

    public void setStatus(VisitTourStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}