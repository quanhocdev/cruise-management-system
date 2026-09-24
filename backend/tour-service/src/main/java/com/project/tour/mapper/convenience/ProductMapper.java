package com.project.tour.mapper.convenience;

import com.project.tour.dto.convenience.product.admin.CreateProductRequest;
import com.project.tour.dto.convenience.product.admin.ProductResponse;
import com.project.tour.dto.convenience.product.admin.UpdateProductRequest;
import com.project.tour.dto.convenience.product.convenience.ProductConvenienceResponse;
import com.project.tour.model.convenience.product.Product;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static Product toEntity(CreateProductRequest request) {

        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());

        return product;
    }

    public static void updateEntity(
            Product product,
            UpdateProductRequest request) {

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setStatus(request.status());
    }

    public static ProductResponse toResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.getImagePublicId(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

    public static ProductConvenienceResponse toConvenienceResponse(
            Product product) {

        if (product == null) {
            return null;
        }

        return new ProductConvenienceResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.getStatus());
    }
}