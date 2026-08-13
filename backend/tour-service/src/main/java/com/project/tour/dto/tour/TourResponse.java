package com.project.tour.dto.tour;

import com.project.tour.model.enums.tour.TourBookingStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;

import java.time.LocalDateTime;
import java.util.UUID;

public class TourResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Integer dayStart;
    private Integer dayEnd;
    private UUID cruiseId;
    private String cruiseName;
    private TourStatusTrip statusTrip;
    private LocalDateTime bookingStart;
    private LocalDateTime bookingEnd;
    private TourBookingStatus statusBooking;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public String getCruiseName() {
        return cruiseName;
    }

    public void setCruiseName(String cruiseName) {
        this.cruiseName = cruiseName;
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