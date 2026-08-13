package com.project.tour.mapper.product;

import com.project.tour.dto.product.area.CreateProductAreaRequest;
import com.project.tour.dto.product.area.ProductAreaResponse;
import com.project.tour.dto.product.area.UpdateProductAreaRequest;
import com.project.tour.model.ProductArea;

public class ProductAreaMapper {

    public static ProductArea toEntity(
            CreateProductAreaRequest request) {

        return new ProductArea();
    }

    public static void updateEntity(
            ProductArea productArea,
            UpdateProductAreaRequest request) {

        // Hiện tại chưa có field nào để update.
    }

    public static ProductAreaResponse toResponse(
            ProductArea productArea) {

        ProductAreaResponse response = new ProductAreaResponse();

        response.setId(productArea.getId());

        response.setAreaId(
                productArea.getCruiseArea().getId());

        response.setAreaName(
                productArea.getCruiseArea().getName());

        response.setProductId(
                productArea.getProduct().getId());

        response.setProductName(
                productArea.getProduct().getName());

        response.setCreatedAt(
                productArea.getCreatedAt());

        response.setUpdatedAt(
                productArea.getUpdatedAt());

        return response;
    }
}