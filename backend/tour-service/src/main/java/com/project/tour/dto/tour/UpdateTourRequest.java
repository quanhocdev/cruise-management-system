package com.project.tour.dto.tour;

import com.project.tour.model.enums.tour.TourBookingStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class UpdateTourRequest {

    @NotBlank(message = "Tour code is required")
    @Size(max = 50)
    private String code;

    @NotBlank(message = "Tour name is required")
    @Size(max = 200)
    private String name;

    @Size(max = 5000)
    private String description;

    @NotNull
    @Min(1)
    private Integer dayStart;

    @NotNull
    @Min(1)
    private Integer dayEnd;

    @NotNull
    private UUID cruiseId;

    private TourStatusTrip statusTrip;

    private LocalDateTime bookingStart;

    private LocalDateTime bookingEnd;

    private TourBookingStatus statusBooking;

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

    public TourStatusTrip getStatusTrip() {
        return statusTrip;
    }

    public void setStatusTrip(TourStatusTrip statusTrip) {
        this.statusTrip = statusTrip;
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

    public TourBookingStatus getStatusBooking() {
        return statusBooking;
    }

    public void setStatusBooking(TourBookingStatus statusBooking) {
        this.statusBooking = statusBooking;
    }
}