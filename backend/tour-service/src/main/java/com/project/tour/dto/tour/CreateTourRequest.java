package com.project.tour.dto.tour;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateTourRequest {

    @NotBlank(message = "Tour code is required")
    @Size(max = 50)
    private String code;

    @NotBlank(message = "Tour name is required")
    @Size(max = 200)
    private String name;

    @Size(max = 5000)
    private String description;

    @NotNull(message = "Day start is required")
    @Min(value = 1, message = "Day start must be at least 1")
    private Integer dayStart;

    @NotNull(message = "Day end is required")
    @Min(value = 1, message = "Day end must be at least 1")
    private Integer dayEnd;

    @NotNull(message = "Cruise id is required")
    private UUID cruiseId;

    private LocalDateTime bookingStart;

    private LocalDateTime bookingEnd;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Integer getDayStart() {
        return dayStart;
    }

    public void setDayStart(Integer dayStart) {
        this.dayStart = dayStart;
    }

    public Integer getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(Integer dayEnd) {
        this.dayEnd = dayEnd;
    }

    public UUID getCruiseId() {
        return cruiseId;
    }

    public void setCruiseId(UUID cruiseId) {
        this.cruiseId = cruiseId;
    }

    public LocalDateTime getBookingStart() {
        return bookingStart;
    }

    public void setBookingStart(LocalDateTime bookingStart) {
        this.bookingStart = bookingStart;
    }

    public LocalDateTime getBookingEnd() {
        return bookingEnd;
    }

    public void setBookingEnd(LocalDateTime bookingEnd) {
        this.bookingEnd = bookingEnd;
    }
}