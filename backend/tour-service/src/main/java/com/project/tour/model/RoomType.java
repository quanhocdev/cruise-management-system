package com.project.tour.model;

import jakarta.persistence.*;

import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(
    name = "room_types",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_room_types_name",
            columnNames = "name"
        )
    }
)
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 19, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column
    private Integer capacity = 1;

    @PrePersist
    protected void onCreate() {
        if (price == null) price = BigDecimal.ZERO;
        if (capacity == null) capacity = 1;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
