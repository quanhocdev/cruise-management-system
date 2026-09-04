package com.project.convenience.model;

import com.project.convenience.model.enums.ServiceTourStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_tours", uniqueConstraints = {
        @UniqueConstraint(name = "uk_service_tour_area", columnNames = { "tour_id", "cruise_area_id" })
})
public class ServiceTour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tour_id", nullable = false)
    private UUID tourId;

    @Column(name = "cruise_area_id", nullable = false)
    private UUID cruiseAreaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    /**
     * Số lượng hành khách tối đa được phục vụ
     * cho dịch vụ này trong Tour.
     *
     * Có thể null nếu dịch vụ không giới hạn.
     */
    @Column(name = "max_passengers")
    private Integer maxPassengers;

    /**
     * Thời gian phục vụ tối đa cho một lượt / phiên dịch vụ.
     *
     * Có thể null nếu dịch vụ không giới hạn thời gian.
     */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceTourStatus status = ServiceTourStatus.WAITING_CONFIG;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = ServiceTourStatus.WAITING_CONFIG;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
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

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
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

    public ServiceTourStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceTourStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}