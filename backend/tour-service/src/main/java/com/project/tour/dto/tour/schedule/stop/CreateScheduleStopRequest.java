package com.project.tour.dto.tour.schedule.stop;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateScheduleStopRequest {

    @NotNull(message = "Port id is required")
    private UUID portId;

    @NotNull(message = "Stop order is required")
    @Positive(message = "Stop order must be greater than 0")
    private Integer stopOrder;

    @NotNull(message = "Arrival time is required")
    @FutureOrPresent(message = "Arrival time must be present or future")
    private LocalDateTime arriveAt;

    @NotNull(message = "Departure time is required")
    @FutureOrPresent(message = "Departure time must be present or future")
    private LocalDateTime leaveAt;

    public UUID getPortId() {
        return portId;
    }

    public void setPortId(UUID portId) {
        this.portId = portId;
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