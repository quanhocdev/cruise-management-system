package com.project.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public class TourPackageSyncedEvent {
    private UUID id;
    private UUID tourId;
    private String name;
    private BigDecimal price;
    private Integer maxPassengers;
    private String status;

    public TourPackageSyncedEvent() {
    }

    public TourPackageSyncedEvent(UUID id, UUID tourId, String name, BigDecimal price, Integer maxPassengers,
            String status) {
        this.id = id;
        this.tourId = tourId;
        this.name = name;
        this.price = price;
        this.maxPassengers = maxPassengers;
        this.status = status;
    }

    // Getters và Setters
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getMaxPassengers() {
        return maxPassengers;
    }

    public void setMaxPassengers(Integer maxPassengers) {
        this.maxPassengers = maxPassengers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}