package com.project.tour.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assignment_service", uniqueConstraints = {
        @UniqueConstraint(name = "uk_assignment_service_tour_area", columnNames = { "tour_id", "cruise_area_id" })
})
public class AssignmentService {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tour_id", nullable = false)
    private UUID tourId;

    @Column(name = "cruise_area_id", nullable = false)
    private UUID cruiseAreaId;

    @Column(name = "service_tour_id")
    private UUID serviceTourId;

    @Column(name = "service_id")
    private UUID serviceId;

    @Column(name = "service_name", length = 150)
    private String serviceName;

    @Column(name = "service_description", columnDefinition = "TEXT")
    private String serviceDescription;

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "max_passengers")
    private Integer maxPassengers;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AssignmentService() {
    }

    public AssignmentService(UUID tourId, UUID cruiseAreaId) {
        this.tourId = tourId;
        this.cruiseAreaId = cruiseAreaId;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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

    public UUID getCruiseAreaId() {
        return cruiseAreaId;
    }

    public void setCruiseAreaId(UUID cruiseAreaId) {
        this.cruiseAreaId = cruiseAreaId;
    }

    public UUID getServiceTourId() {
        return serviceTourId;
    }

    public void setServiceTourId(UUID serviceTourId) {
        this.serviceTourId = serviceTourId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}