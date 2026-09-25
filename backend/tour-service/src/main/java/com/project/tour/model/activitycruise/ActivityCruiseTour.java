package com.project.tour.model.activitycruise;

import com.project.tour.model.activitycruise.enums.ActivityCruiseTourStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_cruise_tour", uniqueConstraints = {
        @UniqueConstraint(name = "uk_activity_cruise_tour_tour_area", columnNames = {
                "tour_id",
                "cruise_area_id"
        })
})
public class ActivityCruiseTour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * ID của Tour.
     */
    @Column(name = "tour_id", nullable = false)
    private UUID tourId;

    /**
     * ID của CruiseArea.
     */
    @Column(name = "cruise_area_id", nullable = false)
    private UUID cruiseAreaId;

    /**
     * ActivityCruise master.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_cruise_id")
    private ActivityCruise activityCruise;

    /**
     * Snapshot tên ActivityCruise
     * tại thời điểm Activity được phân công vào Tour.
     */
    @Column(name = "activity_name", length = 255)
    private String activityName;

    /**
     * Snapshot mô tả ActivityCruise.
     */
    @Column(name = "activity_description", columnDefinition = "TEXT")
    private String activityDescription;

    /**
     * Snapshot ảnh ActivityCruise.
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * Thời gian bắt đầu hoạt động.
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * Thời gian kết thúc hoạt động.
     */
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
     * Trạng thái ActivityCruise trong Tour.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ActivityCruiseTourStatus status = ActivityCruiseTourStatus.WAITING_CONFIG;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ActivityCruiseTour() {
    }

    public ActivityCruiseTour(
            UUID tourId,
            UUID cruiseAreaId) {

        this.tourId = tourId;
        this.cruiseAreaId = cruiseAreaId;
        this.status = ActivityCruiseTourStatus.WAITING_CONFIG;
    }

    // =====================================================
    // JPA LIFECYCLE
    // =====================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = ActivityCruiseTourStatus.WAITING_CONFIG;
        }
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

    public ActivityCruise getActivityCruise() {
        return activityCruise;
    }

    public void setActivityCruise(ActivityCruise activityCruise) {
        this.activityCruise = activityCruise;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public ActivityCruiseTourStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityCruiseTourStatus status) {
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
