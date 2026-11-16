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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tour_route_legs")
public class TourRouteLeg {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_route_id", nullable = false)
    private TourRoute tourRoute;

    @Column(name = "leg_order", nullable = false)
    private Integer legOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_port_id", nullable = false)
    private Port fromPort;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_port_id", nullable = false)
    private Port toPort;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "route_geo_json", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> routeGeoJson;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

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

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TourRoute getTourRoute() {
        return tourRoute;
    }

    public void setTourRoute(TourRoute tourRoute) {
        this.tourRoute = tourRoute;
    }

    public Integer getLegOrder() {
        return legOrder;
    }

    public void setLegOrder(Integer legOrder) {
        this.legOrder = legOrder;
    }

    public Port getFromPort() {
        return fromPort;
    }

    public void setFromPort(Port fromPort) {
        this.fromPort = fromPort;
    }

    public Port getToPort() {
        return toPort;
    }

    public void setToPort(Port toPort) {
        this.toPort = toPort;
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