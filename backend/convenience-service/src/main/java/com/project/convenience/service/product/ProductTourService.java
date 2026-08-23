package com.project.convenience.service.product;

import com.project.convenience.dto.product.convenience.ProductTourResponse; // Sửa package DTO
import com.project.convenience.mapper.ProductTourMapper; // Sửa package Mapper
import com.project.convenience.model.enums.ProductTourStatus;
import com.project.convenience.repository.ProductTourRepository; // Sửa Repository mới

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductTourService {

    private final ProductTourRepository productTourRepository;
    private final ProductTourMapper mapper;

    public ProductTourService(
            ProductTourRepository productTourRepository,
            ProductTourMapper mapper) {

        this.productTourRepository = productTourRepository;
        this.mapper = mapper;
    }

    // =====================================================
    // GET CONFIGURABLE PRODUCTS
    // =====================================================

    /**
     * Lấy các ProductTour mà Convenience có thể cấu hình hoặc chỉnh sửa.
     *
     * Điều kiện ProductTour.status:
     * - WAITING_CONFIG
     * - NOT_STARTED
     */
    @Transactional(readOnly = true)
    public List<ProductTourResponse> getPendingConfig() {

        return productTourRepository
                .findConfigurable(
                        List.of(
                                ProductTourStatus.WAITING_CONFIG,
                                ProductTourStatus.NOT_STARTED))
                .stream()
                .map(mapper::toProductTourResponse)
                .toList();
    }
}