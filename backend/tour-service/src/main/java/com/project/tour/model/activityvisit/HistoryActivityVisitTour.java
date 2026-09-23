package com.project.tour.model.activityvisit;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "history_activity_visit_tour", uniqueConstraints = {
        @UniqueConstraint(name = "uk_history_activity_visit_tour_tour_id", columnNames = "tour_id")
})
public class HistoryActivityVisitTour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tour_id", nullable = false, unique = true)
    private UUID tourId;

    @Column(name = "total_configurations", nullable = false)
    private Integer totalConfigurations;

    @Column(name = "completed_at", nullable = false, updatable = false)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {

        if (completedAt == null) {
            completedAt = LocalDateTime.now();
        }
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
