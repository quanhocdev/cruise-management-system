// src/main/java/com/project/tour/model/VisitTour.java

package com.project.tour.model;

import com.project.tour.model.enums.visit.VisitTourStatus;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "visit_tour")
public class VisitTour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * VisitTour thuộc về một ScheduleStop cụ thể.
     *
     * Từ ScheduleStop có thể truy ngược:
     *
     * ScheduleStop
     * -> Schedule
     * -> Tour
     *
     * Đồng thời biết được:
     *
     * arriveAt
     * leaveAt
     * port
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_stop_id", nullable = false)
    private ScheduleStop scheduleStop;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /*
     * Thời gian thực tế của chuyến tham quan trên bờ.
     *
     * Phải nằm trong:
     *
     * ScheduleStop.arriveAt
     * ScheduleStop.leaveAt
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "max_passengers", nullable = false)
    private Integer maxPassengers;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VisitTourStatus status = VisitTourStatus.WAITING_CONFIG;

    /*
     * Nhà cung cấp tour địa phương.
     */
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "tour_provider_id")
    // private TourProvider tourProvider;

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

    public ScheduleStop getScheduleStop() {
        return scheduleStop;
    }

    public void setScheduleStop(ScheduleStop scheduleStop) {
        this.scheduleStop = scheduleStop;
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

    // public TourProvider getTourProvider() {
    // return tourProvider;
    // }

    // public void setTourProvider(TourProvider tourProvider) {
    // this.tourProvider = tourProvider;
    // }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}