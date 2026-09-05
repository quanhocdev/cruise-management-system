package com.project.activityvisit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "visit_schedule_stops")
public class ScheduleStopOfActivityVisit {

    @Id
    private UUID id; // Nhận scheduleStopId từ ScheduleStopDetail

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ScheduleOfActivityVisit schedule;

    @Column(name = "port_id", nullable = false)
    private UUID portId;

    @Column(name = "port_name", nullable = false, length = 150)
    private String portName;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    @Column(name = "arrive_at")
    private LocalDateTime arriveAt;

    @Column(name = "leave_at")
    private LocalDateTime leaveAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ScheduleOfActivityVisit getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleOfActivityVisit schedule) {
        this.schedule = schedule;
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

    public Integer getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(Integer stopOrder) {
        this.stopOrder = stopOrder;
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
}