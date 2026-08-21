package com.project.tour.model;

import com.project.tour.model.enums.convenience.ServiceTourStatus;
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

    /**
     * Tour được phân công dịch vụ.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    /**
     * Khu vực/phòng trên du thuyền được phân công cho dịch vụ.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cruise_area_id", nullable = false)
    private CruiseArea cruiseArea;

    /**
     * Dịch vụ được Convenience cấu hình sau.
     *
     * Operation chỉ tạo assignment nên field này có thể null
     * trong trạng thái WAITING_CONFIG.
     */
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

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public CruiseArea getCruiseArea() {
        return cruiseArea;
    }

    public void setCruiseArea(CruiseArea cruiseArea) {
        this.cruiseArea = cruiseArea;
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