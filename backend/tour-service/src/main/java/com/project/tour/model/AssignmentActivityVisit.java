package com.project.tour.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assignment_activity_visit", uniqueConstraints = {
        @UniqueConstraint(name = "uk_assignment_activity_visit_tour_stop", columnNames = {
                "tour_id",
                "schedule_stop_id"
        })
})
public class AssignmentActivityVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tour_id", nullable = false)
    private UUID tourId;

    @Column(name = "schedule_stop_id", nullable = false)
    private UUID scheduleStopId;

    @Column(name = "visit_tour_id")
    private UUID visitTourId;

    @Column(name = "visit_name", length = 200)
    private String visitName;

    @Column(name = "visit_description", columnDefinition = "TEXT")
    private String visitDescription;

    // =====================================================
    // CONFIGURATION
    // =====================================================

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "max_passengers")
    private Integer maxPassengers;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AssignmentActivityVisit() {
    }

    public AssignmentActivityVisit(
            UUID tourId,
            UUID scheduleStopId) {

        this.tourId = tourId;
        this.scheduleStopId = scheduleStopId;
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

    public UUID getScheduleStopId() {
        return scheduleStopId;
    }

    public void setScheduleStopId(UUID scheduleStopId) {
        this.scheduleStopId = scheduleStopId;
    }

    public UUID getVisitTourId() {
        return visitTourId;
    }

    public void setVisitTourId(UUID visitTourId) {
        this.visitTourId = visitTourId;
    }

    public String getVisitName() {
        return visitName;
    }

    public void setVisitName(String visitName) {
        this.visitName = visitName;
    }

    public String getVisitDescription() {
        return visitDescription;
    }

    public void setVisitDescription(String visitDescription) {
        this.visitDescription = visitDescription;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}