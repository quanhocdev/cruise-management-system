package com.project.tour.model;

import com.project.tour.model.enums.convenience.ProductTourStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_tour")
public class ProductTour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * Tour mà tiện ích/sản phẩm này thuộc về.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    /*
     * Sản phẩm/Tiện ích master do Operation chọn phân công.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    /*
     * Khu vực phục vụ/giao nhận trên du thuyền (Tùy chọn).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cruise_area_id", nullable = false)
    private CruiseArea cruiseArea;

    /*
     * Số lượng sản phẩm/tiện ích Operation cấp/định mức cho Tour này.
     */
    @Column(name = "quantity")
    private Integer quantity;

    /*
     * Trạng thái của tiện ích/sản phẩm trong Tour.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductTourStatus status = ProductTourStatus.WAITING_CONFIG;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = ProductTourStatus.WAITING_CONFIG;
        }
        if (quantity == null) {
            quantity = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public CruiseArea getCruiseArea() {
        return cruiseArea;
    }

    public void setCruiseArea(CruiseArea cruiseArea) {
        this.cruiseArea = cruiseArea;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ProductTourStatus getStatus() {
        return status;
    }

    public void setStatus(ProductTourStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}