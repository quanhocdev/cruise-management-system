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
    // TOUR
    // =====================================================

    @Column(name = "tour_id", nullable = false)
    private UUID tourId;

    @Column(name = "tour_code", nullable = false, length = 100)
    private String tourCode;

    @Column(name = "tour_name", nullable = false, length = 200)
    private String tourName;

    // =====================================================
    // SCHEDULE
    // =====================================================

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    // =====================================================
    // SCHEDULE STOP
    // =====================================================

    @Column(name = "schedule_stop_id", nullable = false)
    private UUID scheduleStopId;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    // =====================================================
    // PORT
    // =====================================================

    @Column(name = "port_id", nullable = false)
    private UUID portId;

    @Column(name = "port_name", nullable = false, length = 200)
    private String portName;

    // =====================================================
    // SHIP ARRIVAL / DEPARTURE
    // =====================================================

    @Column(name = "arrive_at", nullable = false)
    private LocalDateTime arriveAt;

    @Column(name = "leave_at", nullable = false)
    private LocalDateTime leaveAt;

    // =====================================================
    // VISIT TOUR CONFIGURATION
    // =====================================================

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "max_passengers", nullable = false)
    private Integer maxPassengers;

    @Column(nullable = false, precision = 15, scale = 2)
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

    public String getTourCode() {
        return tourCode;
    }

    public void setTourCode(String tourCode) {
        this.tourCode = tourCode;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public UUID getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(UUID scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public UUID getScheduleStopId() {
        return scheduleStopId;
    }

    public void setScheduleStopId(UUID scheduleStopId) {
        this.scheduleStopId = scheduleStopId;
    }

    public Integer getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(Integer stopOrder) {
        this.stopOrder = stopOrder;
    }

    public UUID getPortId() {
        return portId;
    }

    public void setPortId(UUID portId) {
        this.portId = portId;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public LocalDateTime getArriveAt() {
        return arriveAt;
    }

    public void setArriveAt(LocalDateTime arriveAt) {
        this.arriveAt = arriveAt;
    }

    public LocalDateTime getLeaveAt() {
        return leaveAt;
    }

    public void setLeaveAt(LocalDateTime leaveAt) {
        this.leaveAt = leaveAt;
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