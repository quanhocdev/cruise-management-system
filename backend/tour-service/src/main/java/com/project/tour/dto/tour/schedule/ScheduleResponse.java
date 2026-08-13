package com.project.tour.dto.tour.schedule;

import com.project.tour.model.enums.ScheduleStatus;

import java.time.LocalDate;
import java.util.UUID;

public class ScheduleResponse {

    private UUID id;
    private UUID tourId;
    private String name;
    private String description;
    private Integer dayNumber;
    private LocalDate realDay;
    private ScheduleStatus status;

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

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public LocalDate getRealDay() {
        return realDay;
    }

    public void setRealDay(LocalDate realDay) {
        this.realDay = realDay;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }
}