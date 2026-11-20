package com.project.tour.model.admin;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "room_types", uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_types_name", columnNames = "name")
})
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

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 255)
    private String imagePublicId;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<RoomTypeImage> images = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (price == null) {
            price = BigDecimal.ZERO;
        }
        if (capacity == null) {
            capacity = 1;
        }
    }

    public void addImage(RoomTypeImage image) {
        images.add(image);
        image.setRoomType(this);
    }

    public void removeImage(RoomTypeImage image) {
        images.remove(image);
        image.setRoomType(null);
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
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

    public List<RoomTypeImage> getImages() {
        return images;
    }

    public void setImages(List<RoomTypeImage> images) {
        this.images = images;
    }
}