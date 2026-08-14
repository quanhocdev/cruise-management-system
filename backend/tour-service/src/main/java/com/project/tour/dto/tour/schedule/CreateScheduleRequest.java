package com.project.tour.dto.tour.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateScheduleRequest {

    @NotBlank(message = "Schedule name is required")
    @Size(max = 150, message = "Schedule name must not exceed 150 characters")
    private String name;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @NotNull(message = "Day number is required")
    @Positive(message = "Day number must be greater than 0")
    private Integer dayNumber;

    @NotNull(message = "Real day is required")
    private LocalDate realDay;

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
}