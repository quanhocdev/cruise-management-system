package com.project.convenience.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "history_service_tour", uniqueConstraints = {
        @UniqueConstraint(name = "uk_history_service_tour_tour_id", columnNames = "tour_id")
})
public class HistoryServiceTour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * Tour đã hoàn thành cấu hình Service Tour.
     *
     * Mỗi Tour chỉ được hoàn thành một lần.
     */
    @Column(name = "tour_id", nullable = false)
    private UUID tourId;

    /*
     * Tổng số ServiceTour đã được cấu hình
     * tại thời điểm bấm Hoàn thành.
     */
    @Column(name = "total_configurations", nullable = false)
    private Integer totalConfigurations;

    /*
     * Thời điểm hoàn thành cấu hình.
     */
    @Column(name = "completed_at", nullable = false, updatable = false)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {

        if (completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }

    // =====================================================
    // GETTER / SETTER
    // =====================================================

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

    public Integer getTotalConfigurations() {
        return totalConfigurations;
    }

    public void setTotalConfigurations(Integer totalConfigurations) {
        this.totalConfigurations = totalConfigurations;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}