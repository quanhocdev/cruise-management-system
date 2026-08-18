package com.project.tour.model;

import com.project.tour.model.enums.onboard.ActivityCruiseTourStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_cruise_tour")
public class ActivityCruiseTour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * Tour mà hoạt động này thuộc về.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    /*
     * Hoạt động master được chọn bởi ONBOARD.
     *
     * Khi Operation mới phân công:
     * activityCruise = null
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_cruise_id")
    private ActivityCruise activityCruise;

    /*
     * Khu vực trên du thuyền mà Operation phân công.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cruise_area_id", nullable = false)
    private CruiseArea cruiseArea;

    /*
     * Thời gian hoạt động.
     *
     * Khi Operation phân công:
     * null
     *
     * ONBOARD sẽ cấu hình sau.
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    /*
     * Số hành khách tối đa.
     */
    @Column(name = "max_passengers")
    private Integer maxPassengers;

    /*
     * Giá hoạt động.
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    /*
     * Trạng thái của hoạt động trong Tour.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityCruiseTourStatus status = ActivityCruiseTourStatus.WAITING_CONFIG;

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
            status = ActivityCruiseTourStatus.WAITING_CONFIG;
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

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public ActivityCruise getActivityCruise() {
        return activityCruise;
    }

    public void setActivityCruise(ActivityCruise activityCruise) {
        this.activityCruise = activityCruise;
    }

    public CruiseArea getCruiseArea() {
        return cruiseArea;
    }

    public void setCruiseArea(CruiseArea cruiseArea) {
        this.cruiseArea = cruiseArea;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}