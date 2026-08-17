package com.project.tour.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "activity_cruise")
public class ActivityCruise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cruise_area_id", nullable = false)
    private Long cruiseAreaId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "max_passengers")
    private Integer maxPassengers;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(length = 50)
    private String status;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "image_public_id", length = 255)
    private String imagePublicId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default Constructor (Bắt buộc cho JPA)
    public ActivityCruise() {
    }

    // All Arguments Constructor
    public ActivityCruise(Long id, Long cruiseAreaId, String name, String description,
            LocalDateTime startTime, LocalDateTime endTime, Integer maxPassengers,
            BigDecimal price, String status, String imageUrl,
            String imagePublicId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.cruiseAreaId = cruiseAreaId;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxPassengers = maxPassengers;
        this.price = price;
        this.status = status;
        this.imageUrl = imageUrl;
        this.imagePublicId = imagePublicId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor dùng để tạo mới (không có id, createdAt, updatedAt)
    public ActivityCruise(Long cruiseAreaId, String name, String description,
            LocalDateTime startTime, LocalDateTime endTime, Integer maxPassengers,
            BigDecimal price, String status, String imageUrl, String imagePublicId) {
        this.cruiseAreaId = cruiseAreaId;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxPassengers = maxPassengers;
        this.price = price;
        this.status = status;
        this.imageUrl = imageUrl;
        this.imagePublicId = imagePublicId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCruiseAreaId() {
        return cruiseAreaId;
    }

    public void setCruiseAreaId(Long cruiseAreaId) {
        this.cruiseAreaId = cruiseAreaId;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxPassengers() {
        return maxPassengers;
    }

    public void setMaxPassengers(Integer maxPassengers) {
        this.maxPassengers = maxPassengers;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePublicId() {
        return imagePublicId;
    }

    public void setImagePublicId(String imagePublicId) {
        this.imagePublicId = imagePublicId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ActivityCruise that = (ActivityCruise) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ActivityCruise{" +
                "id=" + id +
                ", cruiseAreaId=" + cruiseAreaId +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}