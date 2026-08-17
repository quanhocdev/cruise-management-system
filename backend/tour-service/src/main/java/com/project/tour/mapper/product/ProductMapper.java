package com.project.tour.mapper.product;

import com.project.tour.dto.product.CreateProductRequest;
import com.project.tour.dto.product.ProductConvenienceResponse;
import com.project.tour.dto.product.ProductResponse;
import com.project.tour.dto.product.UpdateProductRequest;
import com.project.tour.model.Product;

public class ProductMapper {

    private ProductMapper() {
    }

    public static Product toEntity(CreateProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        return product;
    }

    public static void updateEntity(
            Product product,
            UpdateProductRequest request) {

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setStatus(request.getStatus());
    }

    public static ProductResponse toResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setImageUrl(product.getImageUrl());
        response.setImagePublicId(product.getImagePublicId());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }

    /*
     * =====================================================
     * CONVENIENCE MAPPER
     * =====================================================
     */
    public static ProductConvenienceResponse toConvenienceResponse(Product product) {

        ProductConvenienceResponse response = new ProductConvenienceResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setImageUrl(product.getImageUrl());
        response.setStatus(product.getStatus());

        return response;
    }
}