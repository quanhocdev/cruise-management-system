package com.project.tour.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assignment_activity_cruise", uniqueConstraints = {
        @UniqueConstraint(name = "uk_assignment_activity_cruise_tour_area", columnNames = { "tour_id",
                "cruise_area_id" })
})
public class AssignmentActivityCruise {

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

    /**
     * ID của ActivityCruiseTour thuộc activity-cruise-service.
     */
    @Column(name = "activity_cruise_tour_id")
    private UUID activityCruiseTourId;

    /**
     * ID của ActivityCruise master.
     */
    @Column(name = "activity_cruise_id")
    private UUID activityCruiseId;

    /**
     * Snapshot thông tin ActivityCruise
     * tại thời điểm cấu hình Tour.
     */
    @Column(name = "activity_name", length = 255)
    private String activityName;

    @Column(name = "activity_description", columnDefinition = "TEXT")
    private String activityDescription;

    /**
     * Thời gian hoạt động trong Tour.
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * Số hành khách tối đa.
     */
    @Column(name = "max_passengers")
    private Integer maxPassengers;

    /**
     * Giá hoạt động trong Tour.
     */
    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    /**
     * Ảnh ActivityCruise.
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * Trạng thái ActivityCruise trong Tour.
     */
    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AssignmentActivityCruise() {
    }

    public AssignmentActivityCruise(
            UUID tourId,
            UUID cruiseAreaId) {

        this.tourId = tourId;
        this.cruiseAreaId = cruiseAreaId;
    }

    // =====================================================
    // JPA LIFECYCLE
    // =====================================================

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

    public UUID getActivityCruiseTourId() {
        return activityCruiseTourId;
    }

    public void setActivityCruiseTourId(UUID activityCruiseTourId) {
        this.activityCruiseTourId = activityCruiseTourId;
    }

    public UUID getActivityCruiseId() {
        return activityCruiseId;
    }

    public void setActivityCruiseId(UUID activityCruiseId) {
        this.activityCruiseId = activityCruiseId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}