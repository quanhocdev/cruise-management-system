
package com.project.tour.model.scheduler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.project.tour.model.admin.Port;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tour_routes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tour_routes_tour", columnNames = "tour_id")
})
public class TourRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false, unique = true)
    private Tour tour;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departure_port_id", nullable = false)
    private Port departurePort;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_port_id", nullable = false)
    private Port destinationPort;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "route_geo_json", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> routeGeoJson;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Column(name = "distance_from_departure_km")
    private Double distanceFromDepartureKm;

    @Column(name = "distance_to_destination_km")
    private Double distanceToDestinationKm;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public Port getDeparturePort() {
        return departurePort;
    }

    public void setDeparturePort(Port departurePort) {
        this.departurePort = departurePort;
    }

    public Port getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(Port destinationPort) {
        this.destinationPort = destinationPort;
    }

    public Map<String, Object> getRouteGeoJson() {
        return routeGeoJson;
    }

    public void setRouteGeoJson(Map<String, Object> routeGeoJson) {
        this.routeGeoJson = routeGeoJson;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getDistanceFromDepartureKm() {
        return distanceFromDepartureKm;
    }

    public void setDistanceFromDepartureKm(Double distanceFromDepartureKm) {
        this.distanceFromDepartureKm = distanceFromDepartureKm;
    }

    public Double getDistanceToDestinationKm() {
        return distanceToDestinationKm;
    }

    public void setDistanceToDestinationKm(Double distanceToDestinationKm) {
        this.distanceToDestinationKm = distanceToDestinationKm;
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
